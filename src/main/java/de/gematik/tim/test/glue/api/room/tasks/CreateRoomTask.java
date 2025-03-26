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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_ROOM;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.addRoomToActor;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createTopic;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueRoomName;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.CreateRoomRequestDTO.RoomAccessEnum.PRIVATE;
import static de.gematik.tim.test.models.RoomMembershipStateDTO.JOIN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.gematik.tim.test.models.CreateRoomRequestDTO;
import de.gematik.tim.test.models.RoomDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class CreateRoomTask implements Task {

  private String roomName;
  private String topic;

  public static CreateRoomTask createRoom() {
    return new CreateRoomTask();
  }

  public CreateRoomTask withName(String roomName) {
    this.roomName = roomName;
    return this;
  }

  public CreateRoomTask withTopic() {
    this.topic = createTopic();
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    String uniqueName = createUniqueRoomName();
    CreateRoomRequestDTO requestDTO =
        new CreateRoomRequestDTO().roomAccess(PRIVATE).name(uniqueName).theme(topic);

    actor.attemptsTo(CREATE_ROOM.request().with(req -> req.body(requestDTO)));
    RoomDTO room = parseResponse(RoomDTO.class);
    assertThat(room.getName()).isEqualTo(uniqueName);
    assertThat(room.getRoomAccess().getValue()).isEqualTo(PRIVATE.getValue());
    addRoomToActor(roomName, room, actor);
    actor.remember(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX, JOIN);
  }
}
