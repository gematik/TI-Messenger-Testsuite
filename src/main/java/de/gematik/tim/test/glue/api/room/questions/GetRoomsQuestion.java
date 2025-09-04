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
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

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
  @SneakyThrows
  public List<RoomDTO> searchParallel() {
    final CloseableHttpClient client = getClient();
    final HttpGet request = new HttpGet(GET_ROOMS.getResolvedPath(actor));
    request.addHeader(TEST_CASE_ID_HEADER, getTestcaseId());
    String jsonString;
    try (final CloseableHttpResponse response = client.execute(request)) {
      final HttpEntity entity = response.getEntity();
      jsonString = entity != null ? new String(entity.getContent().readAllBytes()) : "";
    }
    final List<RoomDTO> rooms = List.of(fromJson(jsonString, RoomDTO[].class));
    updateAvailableRooms(rooms, actor);
    return rooms;
  }
}
