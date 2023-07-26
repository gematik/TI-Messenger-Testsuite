/*
 * Copyright 2023 gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.room.questions;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ROOM_STATES;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.RoomStateDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.List;

public class GetRoomStatesQuestion implements Question<List<RoomStateDTO>> {

  public static GetRoomStatesQuestion roomStates() {
    return new GetRoomStatesQuestion();
  }

  @Override
  public List<RoomStateDTO> answeredBy(Actor actor) {
    actor.attemptsTo(GET_ROOM_STATES.request());
    lastResponse().prettyPrint();
    return List.of(lastResponse().body().as(RoomStateDTO[].class));
  }
}
