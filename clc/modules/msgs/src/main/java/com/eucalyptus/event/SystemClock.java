/*******************************************************************************
 *Copyright (c) 2009  Eucalyptus Systems, Inc.
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, only version 3 of the License.
 * 
 * 
 *  This file is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 * 
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Please contact Eucalyptus Systems, Inc., 130 Castilian
 *  Dr., Goleta, CA 93101 USA or visit <http://www.eucalyptus.com/licenses/>
 *  if you need additional information or have any questions.
 * 
 *  This file may incorporate work covered under the following copyright and
 *  permission notice:
 * 
 *    Software License Agreement (BSD License)
 * 
 *    Copyright (c) 2008, Regents of the University of California
 *    All rights reserved.
 * 
 *    Redistribution and use of this software in source and binary forms, with
 *    or without modification, are permitted provided that the following
 *    conditions are met:
 * 
 *      Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *      Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 * 
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 *    IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *    TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *    PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 *    OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. USERS OF
 *    THIS SOFTWARE ACKNOWLEDGE THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE
 *    LICENSED MATERIAL, COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS
 *    SOFTWARE, AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *    IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA, SANTA
 *    BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY, WHICH IN
 *    THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION, REPLACEMENT
 *    OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO IDENTIFIED, OR
 *    WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT NEEDED TO COMPLY WITH
 *    ANY SUCH LICENSES OR RIGHTS.
 *******************************************************************************
 * @author chris grzegorczyk <grze@eucalyptus.com>
 */
package com.eucalyptus.event;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import com.eucalyptus.bootstrap.Bootstrap;
import com.eucalyptus.bootstrap.Bootstrapper;
import com.eucalyptus.bootstrap.OrderedShutdown;
import com.eucalyptus.bootstrap.Provides;
import com.eucalyptus.bootstrap.RunDuring;
import com.eucalyptus.empyrean.Empyrean;

public class SystemClock extends TimerTask implements UncaughtExceptionHandler {
  private static Logger      LOG   = Logger.getLogger( SystemClock.class );
  
  private static final long  RATE  = 10000;
  
  private static SystemClock clock;
  private static Timer       timer;
  private static Timer       hzTimer;
  private static HzClock     hertz;
  private int                phase = 0;
  
  public SystemClock( ) {
    super( );
  }
  
  public static long getRate( ) {
    return RATE;
  }
  
  public static void setupTimer( ) {
    synchronized ( SystemClock.class ) {
      if ( timer == null ) {
        timer = new Timer( "SystemClockTimer" );
        hzTimer = new Timer( "SystemHzTimer" );
        clock = new SystemClock( );
        hertz = new HzClock( );
        ListenerRegistry.getInstance( ).register( ClockTick.class, new Dummy( ) );
        ListenerRegistry.getInstance( ).register( Hertz.class, new Dummy( ) );
        timer.scheduleAtFixedRate( clock, 0, 10000 );//TODO: make configurable
        hzTimer.scheduleAtFixedRate( hertz, 0, 1000 );
        OrderedShutdown.registerPreShutdownHook( new Runnable( ) {
          @Override
          public void run( ) {
            timer.cancel( );
          }
        } );
        OrderedShutdown.registerPreShutdownHook( new Runnable( ) {
          
          @Override
          public void run( ) {
            hzTimer.cancel( );
          }
        } );
      }
    }
  }
  
  @Override
  public void run( ) {
    Thread.currentThread( ).setUncaughtExceptionHandler( ( UncaughtExceptionHandler ) this );
    try {
      long sign = ( long ) ( Math.pow( -1f, ( float ) ( ++phase % 2 ) ) );
      ListenerRegistry.getInstance( ).fireEvent( new ClockTick( ).setMessage( sign * System.currentTimeMillis( ) ) );
    } catch ( EventFailedException e ) {} catch ( Exception t ) {
      LOG.error( t, t );
    }
  }
  
  public static class Dummy implements EventListener {
    @Override
    public void fireEvent( Event event ) {}
  }
  
  @Override
  public void uncaughtException( Thread t, Throwable e ) {
    LOG.fatal( e, e );
  }
  
  @Provides( Empyrean.class )
  @RunDuring( Bootstrap.Stage.Final )
  public static class SystemClockBootstrapper extends Bootstrapper {
    
    @Override
    public boolean load( ) throws Exception {
      return true;
    }
    
    @Override
    public boolean start( ) throws Exception {
      setupTimer( );
      return true;
    }
    
    @Override
    public boolean enable( ) throws Exception {
      return true;
    }
    
    @Override
    public boolean stop( ) throws Exception {
      //ASAP:FIXME:GRZE restarting the timer
      return true;
    }
    
    @Override
    public void destroy( ) throws Exception {}
    
    @Override
    public boolean disable( ) throws Exception {
      return true;
    }
    
    @Override
    public boolean check( ) throws Exception {
      return true;
    }
    
  }
  
  public static class HzClock extends TimerTask implements UncaughtExceptionHandler {
    private int phase = 0;
    
    @Override
    public void uncaughtException( Thread thread, Throwable t ) {
      LOG.error( t, t );
    }
    
    public void run( ) {
      Thread.currentThread( ).setUncaughtExceptionHandler( ( UncaughtExceptionHandler ) this );
      try {
        ListenerRegistry.getInstance( ).fireEvent( new Hertz( ) );
      } catch ( EventFailedException e ) {} catch ( Exception t ) {
        LOG.error( t, t );
      }
    }
  }
  
}
