/*
 * Copyright 2024 gematik GmbH
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

package de.gematik.tim.test.glue.api.authorization;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_BLOCKED_SERVER_NAMES;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.SERVER_NAME_VARIABLE;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;

import de.gematik.tim.test.models.ServerNameDTO;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

@RequiredArgsConstructor
public class GetBlockedServerQuestion implements Question<ServerNameDTO> {

  private final String blockedServerName;

  public static GetBlockedServerQuestion getBlockedServerName(String blockedServerName) {
    return new GetBlockedServerQuestion(blockedServerName);
  }

  @Override
  public ServerNameDTO answeredBy(Actor actor) {
    actor.attemptsTo(
            GET_BLOCKED_SERVER_NAMES
            .request()
            .with(req -> req.pathParam(SERVER_NAME_VARIABLE, blockedServerName)));
    return parseResponse(ServerNameDTO.class);
  }
}
