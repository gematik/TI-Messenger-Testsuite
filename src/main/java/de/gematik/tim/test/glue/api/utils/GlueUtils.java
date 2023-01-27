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
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.gson.Gson;
import de.gematik.tim.test.glue.api.exceptions.RequestedRessourceNotAvailable;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMemberDTO;
import io.cucumber.java.ParameterType;
import java.io.FileReader;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@Slf4j
public class GlueUtils {

  public static final String TEST_RESOURCES_JSON_PATH = "src/test/resources/json/";

  private static Long timeout = 10L;
  private static Long pollInterval = 1L;

  static {
    String timeoutString = System.getProperty("timeout");
    String pollIntervalString = System.getProperty("pollInterval");
    try {
      if (timeoutString != null && pollIntervalString != null) {
        timeout = Long.parseLong(timeoutString);
        pollInterval = Long.parseLong(pollIntervalString);
      } else if (timeoutString != null || pollIntervalString != null) {
        log.error(format(
            "Please set timeout and pollInterval! Will use default -> timeout: %s, pollInterval: %s",
            timeout, pollInterval));
      }
    } catch (Exception ex) {
      log.info(format(
          "Could not parse timeout (%s) or pollInterval (%s). Will use default -> timeout: %s, pollInterval: %s",
          timeoutString, pollIntervalString, timeout, pollInterval));
    }
  }

  // Utils
  public static RoomDTO filterForRoomWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    assertThat(filteredRooms).as(
            "%s matching room for this members (%s) have been found.", filteredRooms.size(),
            StringUtils.join(memberIds, ","))
        .hasSize(1);
    return filteredRooms.get(0);
  }

  public static Optional<RoomDTO> getRoomWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    if (filteredRooms.size() == 1) {
      return Optional.of(filteredRooms.get(0));
    }
    if (filteredRooms.size() > 1) {
      assertThat(filteredRooms).as(
              "%s matching room for this members (%s) have been found.", filteredRooms.size(),
              StringUtils.join(memberIds, ","))
          .hasSize(1);
    }
    return Optional.empty();
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

  public static <T> T repeatedRequest(Supplier<Optional<T>> request) {
    return repeatedRequest(request, "resource");
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request, String resourceType) {
    return repeatedRequest(request, resourceType, timeout, pollInterval);
  }

  public static <T> T repeatedRequestWithLongerTimeout(Supplier<Optional<T>> request,
      String resourceType, int factor) {
    return repeatedRequest(request, resourceType, timeout * factor, pollInterval);
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request, String resourceType,
      Long customTimeout, Long customPollInterval) {
    if (customTimeout <= 1) {
      return request.get()
          .orElseThrow(() -> new ConditionTimeoutException(
              format("Asked for %s, but could not be found", resourceType)));
    }
    return await()
        .atMost(Duration.of(customTimeout, SECONDS))
        .pollInSameThread()
        .pollInterval(Duration.of(customPollInterval, SECONDS))
        .until(request::get, Optional::isPresent)
        .orElseThrow(
            () -> new RequestedRessourceNotAvailable(
                format("Asked for %s, but could not be found", resourceType)));
  }
}
