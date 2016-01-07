/*************************************************************************
 * Copyright 2009-2014 Eucalyptus Systems, Inc.
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
 ************************************************************************/
package com.eucalyptus.cloudformation.workflow

import com.netflix.glisten.InterfaceBasedWorkflowClient

/**
 * Created by ethomas on 6/9/44.
 */
class UpdateStackWorkflowClient implements UpdateStackWorkflow {
  UpdateStackWorkflow workflow

  InterfaceBasedWorkflowClient<UpdateStackWorkflow> getClient() {
    return client
  }

  InterfaceBasedWorkflowClient<UpdateStackWorkflow> client;

  UpdateStackWorkflowClient( InterfaceBasedWorkflowClient<UpdateStackWorkflow> client ) {
    this.client = client;
    workflow = client.asWorkflow( ) as UpdateStackWorkflow
  }

  @Override
  void updateStack(String stackId, String accountId, String resourceDependencyManagerJson, String effectiveUserId, int updateVersion) {
    workflow.updateStack(stackId, accountId, resourceDependencyManagerJson, effectiveUserId, updateVersion);
  }


}
