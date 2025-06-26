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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ROOMS;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.updateAvailableRooms;
import static de.gematik.tim.test.glue.api.threading.ClientFactory.getClient;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.fromJson;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;

import de.gematik.tim.test.glue.api.threading.ParallelQuestionRunner;
import de.gematik.tim.test.models.RoomDTO;
import java.util.List;
import kong.unirest.UnirestInstance;
import net.serenitybdd.screenplay.Actor;

public class GetRoomsQuestion extends ParallelQuestionRunner<List<RoomDTO>> {

  public static GetRoomsQuestion ownRooms() {
    return new GetRoomsQuestion();
  }

  @Override
  public List<RoomDTO> answeredBy(Actor actor) {
    actor.attemptsTo(GET_ROOMS.request());
    List<RoomDTO> rooms = List.of(parseResponse(RoomDTO[].class));
    updateAvailableRooms(rooms, actor);
    return rooms;
  }

  @Override
  public List<RoomDTO> searchParallel() {
    UnirestInstance client = getClient();
    List<RoomDTO> rooms =
        List.of(
            fromJson(
                client
                    .get(GET_ROOMS.getResolvedPath(actor))
                    .header(TEST_CASE_ID_HEADER, getTestcaseId())
                    .asString()
                    .getBody(),
                RoomDTO[].class));
    updateAvailableRooms(rooms, actor);
    return rooms;
  }
}
