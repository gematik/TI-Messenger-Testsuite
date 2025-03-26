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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_BLOCKED_USERS;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MXID_VARIABLE;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;

import de.gematik.tim.test.models.MxIdDTO;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

@RequiredArgsConstructor
public class GetBlockedUserQuestion implements Question<MxIdDTO> {

  private final String blockedUserId;

  public static GetBlockedUserQuestion getBlockedUser(String blockedUserId) {
    return new GetBlockedUserQuestion(blockedUserId);
  }

  @Override
  public MxIdDTO answeredBy(Actor actor) {
    actor.attemptsTo(
        GET_BLOCKED_USERS.request().with(req -> req.pathParam(MXID_VARIABLE, blockedUserId)));
    return parseResponse(MxIdDTO.class);
  }
}
