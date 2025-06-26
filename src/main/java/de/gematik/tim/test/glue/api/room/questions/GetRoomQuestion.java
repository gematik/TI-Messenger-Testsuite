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

package de.gematik.tim.test.glue.api.room.questions;

import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getRoomByInternalName;
import static de.gematik.tim.test.models.RoomMembershipStateDTO.LEAVE;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMemberDTO;
import de.gematik.tim.test.models.RoomMembershipStateDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class GetRoomQuestion implements Question<RoomDTO> {

  private final List<Predicate<RoomDTO>> filterList = new ArrayList<>();

  private Long customTimeout;
  private Long customPollInterval;

  public static GetRoomQuestion ownRoom() {
    return new GetRoomQuestion();
  }

  public GetRoomQuestion withMembers(List<String> memberList) {
    this.filterList.add(
        member ->
            new HashSet<>(member.getMembers().stream().map(RoomMemberDTO::getMxid).toList())
                .containsAll(memberList));
    return this;
  }

  public GetRoomQuestion withRoomId(String roomId) {
    this.filterList.add(room -> room.getRoomId().equals(roomId));
    return this;
  }

  public GetRoomQuestion withName(String roomName) {
    this.filterList.add(room -> room.getName().equals(getRoomByInternalName(roomName).getName()));
    return this;
  }

  public GetRoomQuestion withMemberHasStatus(String mxId, RoomMembershipStateDTO status) {
    this.filterList.add(
        room ->
            room.getMembers().stream()
                .anyMatch(
                    member ->
                        member.getMxid().equals(mxId) && member.getMembershipState() == status));
    return this;
  }

  public Question<RoomDTO> notHavingMember(String mxId) {
    this.filterList.add(
        room ->
            room.getMembers().stream()
                .noneMatch(
                    member ->
                        (member.getMxid().equals(mxId)
                            && !member.getMembershipState().equals(LEAVE))));
    return this;
  }

  public GetRoomQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public RoomDTO answeredBy(Actor actor) {
    try {
      RoomDTO room =
          repeatedRequest(() -> filterForResults(actor), "room", customTimeout, customPollInterval);
      actor.abilityTo(UseRoomAbility.class).addAndSetActive(room);
      return room;
    } catch (ConditionTimeoutException e) {
      log.error("Room could not be found with requested parameters", e);
      throw new AssertionFailed("Room could not be found with requested parameters");
    }
  }

  @NotNull
  private Optional<RoomDTO> filterForResults(Actor actor) {
    return actor.asksFor(ownRooms()).stream()
        .filter(filterList.stream().reduce(Predicate::and).orElseThrow())
        .findFirst();
  }
}
