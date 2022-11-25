/*
 * Copyright (c) 2022 gematik GmbH
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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.INVITE_TO_ROOM;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.models.MxIdDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class InviteToRoomTask implements Task {

  private String roomId;

  private final List<MxIdDTO> invitees;

  public static InviteToRoomTask invite(List<String> mxIds) {
    List<MxIdDTO> mxIdDTOS = mxIds.stream()
        .map(mxId -> new MxIdDTO().mxid(mxId))
        .toList();
    return new InviteToRoomTask(mxIdDTOS);
  }

  public InviteToRoomTask toRoom(String roomId) {
    this.roomId = roomId;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    requireNonNull(roomId, "roomId of InviteToRoomTask has to be set with #toRoom(roomId)");

    actor.attemptsTo(INVITE_TO_ROOM.request()
        .with(req -> req.body(invitees)));
  }
}
