/*
 * Copyright 2025 gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.authorization.tasks;

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint;
import de.gematik.tim.test.models.AuthorizationListDTO;
import de.gematik.tim.test.models.GroupNameDTO;
import de.gematik.tim.test.models.MxIdDTO;
import de.gematik.tim.test.models.ServerNameDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public abstract class AuthorizationListTask implements Task {

  private final TestdriverApiEndpoint endpoint;

  private List<String> usersMxIdsAsString;
  private List<MxIdDTO> usersMxIds;

  private List<String> serverNamesAsString;
  private List<ServerNameDTO> serverNames;

  private List<GroupNameDTO> groupNames;

  public AuthorizationListTask(TestdriverApiEndpoint endpoint) {
    this.endpoint = endpoint;
  }

  public AuthorizationListTask withMxIds(List<String> usersMxIdsAsString) {
    this.usersMxIdsAsString = usersMxIdsAsString;
    return this;
  }

  public AuthorizationListTask withMxIdsAsDTO(List<MxIdDTO> usersMxIds) {
    this.usersMxIds = usersMxIds;
    return this;
  }

  public AuthorizationListTask withServerNames(List<String> serverNamesAsString) {
    this.serverNamesAsString = serverNamesAsString;
    return this;
  }

  public AuthorizationListTask withServerNamesAsDTO(List<ServerNameDTO> serverNames) {
    this.serverNames = serverNames;
    return this;
  }

  public AuthorizationListTask withGroups(List<GroupNameDTO> groupNames) {
    this.groupNames = groupNames;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    AuthorizationListDTO authorizationList = new AuthorizationListDTO();
    if (usersMxIdsAsString != null) {
      List<MxIdDTO> mxIdDTOs =
          usersMxIdsAsString.stream().map(mxId -> new MxIdDTO().mxid(mxId)).toList();
      authorizationList.setMxids(mxIdDTOs);
    }
    if (usersMxIds != null) {
      authorizationList.setMxids(usersMxIds);
    }
    if (serverNamesAsString != null) {
      List<ServerNameDTO> serverNameDTOs =
          serverNamesAsString.stream()
              .map(servername -> new ServerNameDTO().serverName(servername))
              .toList();
      authorizationList.setServerNames(serverNameDTOs);
    }
    if (serverNames != null) {
      authorizationList.setServerNames(serverNames);
    }
    if (groupNames != null) {
      authorizationList.setGroupNames(groupNames);
    }
    actor.attemptsTo(endpoint.request().with(req -> req.body(authorizationList)));
  }
}
