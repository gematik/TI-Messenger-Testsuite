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

package de.gematik.tim.test.glue.api.utils;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMemberDTO;
import io.cucumber.java.ParameterType;
import java.io.FileReader;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class GlueUtils {

  public static final String TEST_RESOURCES_JSON_PATH = "src/test/resources/json/";

  // Utils

  public static RoomDTO filterForRoomWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    assertThat(filteredRooms).as(
            "%s matching room for this members (%s) have been found.", filteredRooms.size(),
            memberIds.size())
        .hasSize(1);
    return filteredRooms.get(0);
  }

  @NotNull
  public static List<RoomDTO> filterForRoomsWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    return rooms.stream()
        .filter(r -> requireNonNull(r.getMembers()).size() >= memberIds.size())
        .filter(
            r -> new HashSet<>(
                r.getMembers().stream()
                    .map(RoomMemberDTO::getMxid)
                    .filter(Objects::nonNull)
                    .toList()).containsAll(memberIds)).toList();
  }

  public static MessageDTO filterMessageForSenderAndText(String messageText, String userName,
      List<MessageDTO> messages) {
    List<MessageDTO> filteredMessages = filterMessagesForSenderAndText(messageText, userName,
        messages);
    assertThat(filteredMessages).as(
            "%s matching messages for this members (%s) have been found.", filteredMessages.size(),
            filteredMessages.size())
        .hasSize(1);
    return filteredMessages.get(0);
  }

  @NotNull
  public static List<MessageDTO> filterMessagesForSenderAndText(String message, String userName,
      List<MessageDTO> messages) {
    return messages.stream()
        .filter(e -> requireNonNull(e.getBody()).equals(message))
        .filter(e -> requireNonNull(e.getAuthor()).equals(theActorCalled(userName).recall(MX_ID)))
        .toList();
  }

  @ParameterType(value = "(?:.*)", preferForRegexMatch = true)
  public List<String> listOfStrings(String arg) {
    return stream(arg.split(",\\s?"))
        .map(str -> str.replace("\"", ""))
        .toList();
  }

  public static <T> T readJsonFile(String file, Class<T> type) {
    return new Gson().fromJson(readJsonFile(file), type);
  }

  @SneakyThrows
  public static String readJsonFile(String file) {
    FileReader fileReader = new FileReader(TEST_RESOURCES_JSON_PATH + file);
    return IOUtils.toString(fileReader);
  }

  public static boolean lastResponseWasSuccessful() {
    return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful();
  }

  public static String homeserverFromMxId(String mxId) {
    String[] splitted = mxId.split(":");
    if (splitted.length != 2) {
      throw new IllegalArgumentException(mxId + " is not a valid MxId");
    }
    return splitted[1];
  }
}
