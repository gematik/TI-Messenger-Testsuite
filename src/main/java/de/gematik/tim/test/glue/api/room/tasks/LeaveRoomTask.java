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

package de.gematik.tim.test.glue.api.room.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LEAVE_ROOM;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.parallelClient;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.glue.api.threading.Parallel;
import java.io.IOException;
import net.serenitybdd.screenplay.Actor;
import okhttp3.Request;
import okhttp3.Response;

public class LeaveRoomTask extends RoomSpecificTask implements
    Parallel<ActorsNotes> {

  public static LeaveRoomTask leaveRoom() {
    return new LeaveRoomTask();
  }

  public LeaveRoomTask withName(String roomName) {
    return this.forRoomName(roomName);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(LEAVE_ROOM.request());
  }

  @Override
  public ActorsNotes parallel(ActorsNotes notes) {
    Request request = LEAVE_ROOM.parallelRequest(notes).build();
    try (Response res = parallelClient().get().newCall(request).execute()) {
      if (res.isSuccessful()) {
        return notes;
      } else {
        throw new TestRunException(
            "could not leave room, response code was %d".formatted(res.code()));
      }
    } catch (IOException e) {
      throw new TestRunException(e);
    }
  }
}
