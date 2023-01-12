/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.room.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.JOIN_ROOM;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.addRoomToActor;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.RoomDTO;
import java.util.Objects;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class JoinRoomTask implements Task {

  private String roomId;

  public static JoinRoomTask joinRoom() {
    return new JoinRoomTask();
  }

  public JoinRoomTask withRoomId(String roomid) {
    this.roomId = roomid;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    Objects.requireNonNull(roomId,
        "roomId of JoinRoomTask hast to be set with #withRoomId(roomId)");

    actor.attemptsTo(JOIN_ROOM.request()
        .with(req -> req.pathParam(ROOM_ID_VARIABLE, roomId)));

    RoomDTO room = SerenityRest.lastResponse().body().as(RoomDTO.class);
    addRoomToActor(room, actor);

  }
}
