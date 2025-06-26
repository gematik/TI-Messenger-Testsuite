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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DENY_ROOM;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;

import java.util.Objects;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class DenyRoomInvitationTask implements Task {

  private String roomId;

  public static DenyRoomInvitationTask denyInvitation() {
    return new DenyRoomInvitationTask();
  }

  public DenyRoomInvitationTask toRoom(String roomid) {
    this.roomId = roomid;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    Objects.requireNonNull(roomId);
    actor.attemptsTo(DENY_ROOM.request().with(req -> req.pathParam(ROOM_ID_VARIABLE, roomId)));
  }
}
