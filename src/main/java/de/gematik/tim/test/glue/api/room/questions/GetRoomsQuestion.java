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
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.parallelClient;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;

import de.gematik.tim.test.glue.api.threading.Parallel;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.glue.api.utils.ParallelUtils;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.models.RoomDTO;
import java.io.IOException;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import okhttp3.Call;
import okhttp3.Response;

public class GetRoomsQuestion implements Question<List<RoomDTO>>, Parallel<List<RoomDTO>> {

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
  public List<RoomDTO> parallel(ActorsNotes notes) {
    Call call = parallelClient().get().newCall(GET_ROOMS.parallelRequest(notes).build());
    try (Response response = call.execute()) {
      return List.of(ParallelUtils.fromJson(response.body().string(), RoomDTO[].class));
    } catch (IOException e) {
      throw new TestRunException(e);
    }
  }
}