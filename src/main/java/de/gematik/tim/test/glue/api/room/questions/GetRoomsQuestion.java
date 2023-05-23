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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ROOMS;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.updateAvailableRooms;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.RoomDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class GetRoomsQuestion implements Question<List<RoomDTO>> {

  public static GetRoomsQuestion ownRooms() {
    return new GetRoomsQuestion();
  }

  @Override
  public List<RoomDTO> answeredBy(Actor actor) {
    actor.attemptsTo(GET_ROOMS.request());
    List<RoomDTO> rooms = List.of(lastResponse().body().as(RoomDTO[].class));
    updateAvailableRooms(rooms, actor);
    return rooms;
  }
}