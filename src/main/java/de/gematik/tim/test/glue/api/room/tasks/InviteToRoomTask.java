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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.INVITE_TO_ROOM;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.homeserverFromMxId;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getAllActiveActorsByMxIds;
import static de.gematik.tim.test.models.RoomMembershipStateDTO.INVITE;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.MxIdDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class InviteToRoomTask implements Task {

  private final List<MxIdDTO> invitees;
  private String roomId;
  private boolean shouldFindMxid = true;
  private boolean canBeInvited = true;

  public static InviteToRoomTask invite(List<String> mxIds) {
    List<MxIdDTO> mxIdDTOS = mxIds.stream().map(mxId -> new MxIdDTO().mxid(mxId)).toList();
    return new InviteToRoomTask(mxIdDTOS);
  }

  public InviteToRoomTask toRoom(String roomId) {
    this.roomId = roomId;
    return this;
  }

  public InviteToRoomTask actorCouldBeFound(boolean couldBeFound) {
    this.shouldFindMxid = couldBeFound;
    return this;
  }

  public InviteToRoomTask actorCanBeInvited(boolean canBeInvited) {
    this.canBeInvited = canBeInvited;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(INVITE_TO_ROOM.request().with(req -> req.body(invitees)));

    if (canBeInvited) {
      getAllActiveActorsByMxIds(invitees.stream().map(MxIdDTO::getMxid).toList(), shouldFindMxid)
          .forEach(
              invitee -> invitee.remember(roomId + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX, INVITE));
      sendRawDataEvent(actor.recall(MX_ID));
    }
  }

  @SuppressWarnings("java:S5411")
  private void sendRawDataEvent(String actorMxId) {
    String actorHomeserver = homeserverFromMxId(actorMxId);
    invitees.stream()
        .map(mxid -> homeserverFromMxId(mxid.getMxid()))
        .map(actorHomeserver::equals)
        .forEach(
            sameHomeserver -> {
              if (sameHomeserver) {
                RawDataStatistics.inviteToRoomSameHomeserver();
              } else {
                RawDataStatistics.inviteToRoomMultiHomeserver();
              }
            });
  }
}
