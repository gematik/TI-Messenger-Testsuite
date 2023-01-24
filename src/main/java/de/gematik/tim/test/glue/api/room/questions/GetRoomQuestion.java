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

package de.gematik.tim.test.glue.api.room.questions;

import static de.gematik.tim.test.glue.api.room.UseRoomAbility.updateAvailableRooms;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getRoomWithSpecificMembers;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.repeatedRequest;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.models.RoomDTO;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.jetbrains.annotations.NotNull;

public class GetRoomQuestion implements Question<RoomDTO> {

  private List<String> memberList;
  private String roomName;

  public static GetRoomQuestion ownRoomWithMembers(List<String> memberList) {
    GetRoomQuestion rq = new GetRoomQuestion();
    rq.memberList = memberList;
    return rq;
  }

  public static GetRoomQuestion ownRoomWithName(String roomName) {
    GetRoomQuestion rq = new GetRoomQuestion();
    rq.roomName = roomName;
    return rq;
  }


  @Override
  public RoomDTO answeredBy(Actor actor) {
    checkOnlyOneArgument();
    RoomDTO room = null;
    if (nonNull(memberList)) {
      room = repeatedRequest(filterForMembers(actor), "room");
    }
    if (nonNull(roomName)) {
      room = repeatedRequest(filterForRoomName(actor), "room");
    }
    updateAvailableRooms(List.of(room), actor);
    return room;
  }

  @NotNull
  private Supplier<Optional<RoomDTO>> filterForRoomName(Actor actor) {
    return () -> actor.asksFor(ownRooms()).stream().filter(r -> r.getName().equals(roomName))
        .findFirst();
  }

  @NotNull
  private Supplier<Optional<RoomDTO>> filterForMembers(Actor actor) {
    return () -> getRoomWithSpecificMembers(actor.asksFor(ownRooms()), memberList);
  }

  @SneakyThrows
  private void checkOnlyOneArgument() throws IllegalArgumentException {
    Field[] fields = this.getClass().getDeclaredFields();
    int count = 0;
    for (Field f : fields) {
      if (f.get(this) != null) {
        if (count != 0) {
          throw new IllegalArgumentException(
              "RoomQuestion is only allowed with exact one search parameter. You provided "
                  + count);
        }
        count++;
      }
    }
  }
}
