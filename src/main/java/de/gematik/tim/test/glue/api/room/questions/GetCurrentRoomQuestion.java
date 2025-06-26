/*
 * Copyright (Change Date see Readme), gematik GmbH
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
 *
 * ******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.tim.test.glue.api.room.questions;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ROOM;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static lombok.AccessLevel.PRIVATE;

import de.gematik.tim.test.models.RoomDTO;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

@AllArgsConstructor(access = PRIVATE)
public class GetCurrentRoomQuestion implements Question<RoomDTO> {

  public static GetCurrentRoomQuestion currentRoom() {
    return new GetCurrentRoomQuestion();
  }

  @Override
  public RoomDTO answeredBy(Actor actor) {
    actor.attemptsTo(GET_ROOM.request());
    return parseResponse(RoomDTO.class);
  }
}
