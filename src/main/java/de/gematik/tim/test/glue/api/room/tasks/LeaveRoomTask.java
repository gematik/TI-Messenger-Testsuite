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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LEAVE_ROOM;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.threading.ClientFactory.getClient;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.models.RoomMembershipStateDTO.LEAVE;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import kong.unirest.Empty;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestInstance;
import net.serenitybdd.screenplay.Actor;

public class LeaveRoomTask extends RoomSpecificTask {

  public static LeaveRoomTask leaveRoom() {
    return new LeaveRoomTask();
  }

  public LeaveRoomTask withName(String roomName) {
    return this.forRoomName(roomName);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(LEAVE_ROOM.request());
    actor.remember(
        actor.abilityTo(UseRoomAbility.class).getActiveValue().getRoomId()
            + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX,
        LEAVE);
  }

  @Override
  public void runParallel() {
    UnirestInstance client = getClient();
    HttpResponse<Empty> res =
        client
            .get(LEAVE_ROOM.getResolvedPath(actor))
            .header(TEST_CASE_ID_HEADER, getTestcaseId())
            .asEmpty();
    if (!res.isSuccess()) {
      throw new TestRunException(
          "could not leave room, response code was %d".formatted(res.getStatus()));
    }
    actor.remember(
        actor.abilityTo(UseRoomAbility.class).getActiveValue().getRoomId()
            + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX,
        LEAVE);
  }
}
