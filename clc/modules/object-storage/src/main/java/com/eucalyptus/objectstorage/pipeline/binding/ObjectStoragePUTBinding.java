/*************************************************************************
 * Copyright 2009-2013 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 *
 * This file may incorporate work covered under the following copyright
 * and permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms,
 *   with or without modification, are permitted provided that the
 *   following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 *   THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 *   COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 *   AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 *   SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 *   WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 *   REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 *   IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 *   NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/

package com.eucalyptus.objectstorage.pipeline.binding;

import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMethod;

import com.eucalyptus.objectstorage.util.ObjectStorageProperties;
import com.eucalyptus.objectstorage.util.ObjectStorageProperties.BucketParameter;
import com.eucalyptus.objectstorage.util.ObjectStorageProperties.ObjectParameter;
import com.google.common.collect.ImmutableMap;

public class ObjectStoragePUTBinding extends ObjectStorageRESTBinding {

  private static final ImmutableMap<String, String> SUPPORTED_OPS = ImmutableMap.<String,String>builder( )
    // Bucket operations
    .put(BUCKET + HttpMethod.PUT, "CreateBucket")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.acl.toString(), "SetBucketAccessControlPolicy")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.logging.toString(), "SetBucketLoggingStatus")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.versioning.toString(), "SetBucketVersioningStatus")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.lifecycle.toString(), "SetBucketLifecycle")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.tagging.toString(), "SetBucketTagging")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.cors.toString(), "SetBucketCors")
    .put(BUCKET + HttpMethod.PUT + BucketParameter.policy.toString(), "SetBucketPolicy")

    .put(BUCKET + HttpMethod.POST + BucketParameter.delete, "DeleteMultipleObjects")

    // Object operations
    .put(OBJECT + HttpMethod.PUT, "PutObject")
    .put(OBJECT + HttpMethod.PUT + ObjectParameter.acl.toString(), "SetObjectAccessControlPolicy")
    .put(OBJECT + HttpMethod.PUT + ObjectStorageProperties.COPY_SOURCE, "CopyObject")

    // Multipart Uploads
    .put(OBJECT + HttpMethod.PUT + ObjectParameter.partNumber.toString().toLowerCase()
        + ObjectParameter.uploadId.toString().toLowerCase(), "UploadPart")
    .put(OBJECT + HttpMethod.PUT + ObjectParameter.uploadId.toString().toLowerCase()
        + ObjectParameter.partNumber.toString().toLowerCase(), "UploadPart")

    .put(OBJECT + HttpMethod.POST + ObjectParameter.uploads.toString(), "InitiateMultipartUpload")
    .put(OBJECT + HttpMethod.POST + ObjectParameter.uploadId.toString().toLowerCase(), "CompleteMultipartUpload")
    .build( );

  private static final ImmutableMap<String, String> UNSUPPORTED_OPS = ImmutableMap.<String,String>builder( )
    // Bucket operations
    // Notification
    .put(BUCKET + HttpMethod.PUT + BucketParameter.notification.toString(), "PUT Bucket notification")

    // Website
    .put(BUCKET + HttpMethod.PUT + BucketParameter.website.toString(), "PUT Bucket website")
    .build( );

  @Override
  protected Map<String, String> populateOperationMap() {
    return SUPPORTED_OPS;
  }

  @Override
  protected Map<String, String> populateUnsupportedOperationMap() {
    return UNSUPPORTED_OPS;
  }
}
