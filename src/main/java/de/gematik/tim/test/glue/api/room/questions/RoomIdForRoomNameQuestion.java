/*
 * Copyright 20023 gematik GmbH
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

package de.gematik.tim.test.glue.api.room.questions;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ROOMS;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.RoomDTO;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.openqa.selenium.NotFoundException;

@RequiredArgsConstructor
public class RoomIdForRoomNameQuestion implements Question<String> {

  private final String roomName;

  public static RoomIdForRoomNameQuestion roomIdForRoomName(String roomName) {
    return new RoomIdForRoomNameQuestion(roomName);
  }

  @Override
  public String answeredBy(Actor actor) {
    actor.attemptsTo(GET_ROOMS.request());
    List<RoomDTO> rooms = List.of(lastResponse().body().as(RoomDTO[].class));

    return rooms.stream()
        .filter(roomDTO -> roomName.equals(roomDTO.getName()))
        .map(RoomDTO::getRoomId)
        .filter(Objects::nonNull)
        .findAny()
        .orElseThrow(() -> new NotFoundException(
            format("Actor with name \"%s\" have no room with name \"%s\"", actor.getName(), roomName)));
  }
}
