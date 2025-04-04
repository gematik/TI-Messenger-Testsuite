/*
 *   Copyright 2025 gematik GmbH
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package de.gematik.tim.test.glue.api.authorization.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_ALLOWED_SERVER_NAMES;

import de.gematik.tim.test.models.AuthorizationListDTO;
import de.gematik.tim.test.models.ServerNameDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class DeleteAllowedServerNamesTask implements Task {

  private final List<String> allowedServerNames;

  public static DeleteAllowedServerNamesTask deleteAllowedServerNames(
      List<String> allowedServerNames) {
    return new DeleteAllowedServerNamesTask(allowedServerNames);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    List<ServerNameDTO> serverNameDTOs =
        allowedServerNames.stream()
            .map(servername -> new ServerNameDTO().serverName(servername))
            .toList();
    AuthorizationListDTO allowedList = new AuthorizationListDTO();
    allowedList.serverNames(serverNameDTOs);
    actor.attemptsTo(DELETE_ALLOWED_SERVER_NAMES.request().with(req -> req.body(allowedList)));
  }
}
