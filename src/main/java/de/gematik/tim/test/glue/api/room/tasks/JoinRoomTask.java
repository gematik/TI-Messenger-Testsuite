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

package de.gematik.tim.test.glue.api.room.tasks;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.JOIN_ROOM;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.addRoomToActor;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.models.RoomMembershipStateDTO.JOIN;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.RoomDTO;
import io.restassured.response.Response;
import java.util.Objects;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.springframework.http.HttpStatus;

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
    Objects.requireNonNull(
        roomId, "roomId of JoinRoomTask hast to be set with #withRoomId(roomId)");
    repeatedRequest(() -> joinRoom(actor), "join Room");
  }

  private <T extends Actor> Optional<RoomDTO> joinRoom(T actor) {
    actor.attemptsTo(JOIN_ROOM.request().with(req -> req.pathParam(ROOM_ID_VARIABLE, roomId)));
    Response resp = lastResponse();
    if (HttpStatus.valueOf(resp.statusCode()).is2xxSuccessful()) {
      RoomDTO room = parseResponse(RoomDTO.class);
      addRoomToActor(room, actor);
      actor.remember(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX, JOIN);
      return Optional.of(room);
    } else {
      return Optional.empty();
    }
  }
}
