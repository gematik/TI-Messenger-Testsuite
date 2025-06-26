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

package de.gematik.tim.test.glue.api.room;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.room.tasks.ForgetRoomTask.forgetRoom;
import static de.gematik.tim.test.glue.api.room.tasks.LeaveRoomTask.leaveRoom;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForTeardown;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getInternalRoomNameForActor;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMembershipStateDTO;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.NoArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.Task;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = PRIVATE)
public class UseRoomAbility extends MultiTargetAbility<String, RoomDTO>
    implements TestdriverApiAbility, HasTeardown, RefersToActor {

  private UseRoomAbility(String roomName, RoomDTO room) {
    addAndSetActive(roomName, room);
  }

  public static <T extends Actor> void addRoomToActor(RoomDTO room, T actor) {
    addRoomToActor(getInternalRoomNameForActor(room, actor), room, actor);
  }

  public static <T extends Actor> void addRoomToActor(String roomName, RoomDTO room, T actor) {
    UseRoomAbility ability = actor.abilityTo(UseRoomAbility.class);
    if (isNull(ability)) {
      actor.can(useRoom(roomName, room));
    } else {
      ability.addAndSetActive(roomName, room);
    }
  }

  private static UseRoomAbility useRoom(String roomName, RoomDTO room) {
    return new UseRoomAbility(roomName, room);
  }

  private static UseRoomAbility useRooms(List<RoomDTO> rooms) {
    UseRoomAbility ability = new UseRoomAbility();
    rooms.forEach(ability::addAndSetActive);
    return ability;
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String roomId = getActive().getRoomId();
    return requestSpecification.pathParam(ROOM_ID_VARIABLE, roomId);
  }

  public void addAndSetActive(RoomDTO room) {
    addAndSetActive(getInternalRoomNameForActor(room, actor), room);
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
    return getTarget(roomName).getRoomId();
  }

  @Override
  protected void update() {
    actor.asksFor(ownRooms());
  }

  @Override
  protected void clearDouble() {
    List<RoomDTO> rooms = this.getAll().stream().map(Entry::getValue).distinct().toList();
    this.clear();
    rooms.forEach(this::addAndSetActive);
  }

  @Override
  protected Task tearDownPerTarget(String roomName) {
    return new Task() {
      @Override
      public <T extends Actor> void performAs(T actor) {
        repeatedRequestForTeardown(() -> leaveRoomInTeardown(), actor);
        repeatedRequestForTeardown(() -> forgetRoomInTeardown(), actor);
      }
    };
  }

  private Optional<Boolean> leaveRoomInTeardown() {
    RoomMembershipStateDTO status =
        actor.recall(
            actor.abilityTo(UseRoomAbility.class).getActiveValue().getRoomId()
                + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX);
    if (status == null || status.equals(RoomMembershipStateDTO.LEAVE)) {
      return Optional.of(TRUE);
    } else {
      actor.attemptsTo(leaveRoom());
      return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
          ? Optional.of(TRUE)
          : Optional.empty();
    }
  }

  private Optional<Boolean> forgetRoomInTeardown() {
    RoomMembershipStateDTO status =
        actor.recall(
            actor.abilityTo(UseRoomAbility.class).getActiveValue().getRoomId()
                + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX);
    if (status == null) {
      return Optional.of(TRUE);
    } else {
      actor.attemptsTo(forgetRoom());
      return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
          ? Optional.of(TRUE)
          : Optional.empty();
    }
  }
}
