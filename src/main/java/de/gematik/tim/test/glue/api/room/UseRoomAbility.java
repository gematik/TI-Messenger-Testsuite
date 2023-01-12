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

package de.gematik.tim.test.glue.api.room;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.room.tasks.DeleteRoomTask.deleteRoom;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.models.RoomDTO;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import lombok.NoArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.Task;

@NoArgsConstructor(access = PRIVATE)
public class UseRoomAbility extends MultiTargetAbility<String, String> implements
    TestdriverApiAbility, HasTeardown, RefersToActor {

  private UseRoomAbility(RoomDTO room) {
    addAndSetActive(room);
  }

  public static <T extends Actor> void addRoomToActor(RoomDTO room, T actor) {
    UseRoomAbility ability = actor.abilityTo(UseRoomAbility.class);
    if (isNull(ability)) {
      actor.can(useRoom(room));
    } else {
      ability.addAndSetActive(room);
    }
  }

  private static UseRoomAbility useRoom(RoomDTO room) {
    return new UseRoomAbility(room);
  }

  private static UseRoomAbility useRooms(List<RoomDTO> rooms) {
    UseRoomAbility ability = new UseRoomAbility();
    rooms.forEach(ability::addAndSetActive);
    return ability;
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String roomId = getActive();
    requireNonNull(roomId);
    return requestSpecification.pathParam(ROOM_ID_VARIABLE, roomId);
  }

  public void addAndSetActive(RoomDTO room) {
    addAndSetActive(room.getName(), room.getRoomId());
  }

  public static void updateAvailableRooms(List<RoomDTO> rooms, Actor actor) {
    UseRoomAbility ability = actor.abilityTo(UseRoomAbility.class);
    if (isNull(ability)) {
      actor.can(useRooms(rooms));
    } else {
      ability.clear();
      rooms.forEach(ability::addAndSetActive);
    }
  }

  public String getRoomIdByName(String roomName) {
    return getTarget(roomName);
  }

  @Override
  protected Task tearDownPerTarget(String roomName) {
   return deleteRoom().withName(roomName);
  }

}
