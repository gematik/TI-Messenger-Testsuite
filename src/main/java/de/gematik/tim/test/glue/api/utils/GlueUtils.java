/*
 * Copyright 2023 gematik GmbH
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

package de.gematik.tim.test.glue.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import de.gematik.tim.test.models.*;
import io.cucumber.java.ParameterType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.*;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GlueUtils {

  public static final String TEST_RESOURCES_JSON_PATH = "src/test/resources/json/";
  public static final String MXID_PREFIX = "@";
  public static final String MXID_URL_PREFIX = "matrix:user/";
  private static final Faker faker = new Faker();
  private static final Random random = new Random();

  // Utils
  public static RoomDTO getRoomBetweenTwoActors(Actor actor, String senderName) {
    String actorId = actor.recall(MX_ID);
    String senderId = theActorCalled(senderName).recall(MX_ID);
    List<String> membersIds = List.of(actorId, senderId);
    List<RoomDTO> rooms = actor.asksFor(ownRooms());
    return requireNonNull(filterForRoomWithSpecificMembers(rooms, membersIds));
  }

  public static RoomDTO filterForRoomWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    assertThat(filteredRooms).as("%s matching room for this members (%s) have been found.",
        filteredRooms.size(), StringUtils.join(memberIds, ",")).hasSize(1);
    return filteredRooms.get(0);
  }

  @NotNull
  public static List<RoomDTO> filterForRoomsWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    return rooms.stream().filter(r -> requireNonNull(r.getMembers()).size() >= memberIds.size())
        .filter(r -> new HashSet<>(
            r.getMembers().stream().map(RoomMemberDTO::getMxid).filter(Objects::nonNull)
                .toList()).containsAll(memberIds)).toList();
  }

  public static MessageDTO filterMessageForSenderAndText(String messageText, String userName,
      List<MessageDTO> messages) {
    List<MessageDTO> filteredMessages = filterMessagesForSenderAndText(messageText, userName,
        messages);
    assertThat(filteredMessages).as("%s matching messages for this members (%s) have been found.",
        filteredMessages.size(), filteredMessages.size()).hasSize(1);
    return filteredMessages.get(0);
  }

  @NotNull
  public static List<MessageDTO> filterMessagesForSenderAndText(String message, String userName,
      List<MessageDTO> messages) {
    return messages.stream().filter(e -> requireNonNull(e.getBody()).equals(message))
        .filter(e -> requireNonNull(e.getAuthor()).equals(theActorCalled(userName).recall(MX_ID)))
        .toList();
  }

  @SneakyThrows
  public static <T> T readJsonFile(String file, Class<T> returnType) {
    return new ObjectMapper().registerModule(new JavaTimeModule())
        .readValue(new File(TEST_RESOURCES_JSON_PATH + file), returnType);
  }

  @SneakyThrows
  public static String readJsonFile(String file) {
    FileReader fileReader = new FileReader(TEST_RESOURCES_JSON_PATH + file);
    return IOUtils.toString(fileReader);
  }

  public static boolean isSameHomeserver(String id1, String id2) {
    return homeserverFromMxId(id1).equals(homeserverFromMxId(id2));
  }

  public static String homeserverFromMxId(String mxId) {
    String[] splitted = mxId.split(":");
    if (splitted.length != 2) {
      throw new IllegalArgumentException(mxId + " is not a valid MxId");
    }
    return splitted[1];
  }

  public static String createUniqueHsName() {
    return faker.gameOfThrones().character() + " of " + faker.gameOfThrones().house() + "-"
        + Instant.now()
        .toEpochMilli();
  }

  public static String createUniqueEndpointName() {
    return format("The %s need some %s for %s", faker.hacker().noun(), faker.hacker().ingverb(),
        faker.hacker().abbreviation());
  }

  public static String createUniqueRoomName() {
    return faker.company().buzzword() + " " + faker.company().industry() + "-" + Instant.now()
        .toEpochMilli();
  }

  public static String createUniqueMessageText() {
    return switch (random.nextInt(7)) {
      case 1 -> faker.gameOfThrones().quote() + "-" + Instant.now().toEpochMilli();
      case 2 -> faker.friends().quote() + "-" + Instant.now().toEpochMilli();
      case 3 -> faker.harryPotter().quote() + "-" + Instant.now().toEpochMilli();
      case 4 -> faker.rickAndMorty().quote() + "-" + Instant.now().toEpochMilli();
      case 5 -> faker.lebowski().quote() + "-" + Instant.now().toEpochMilli();
      case 6 -> faker.witcher().quote() + "-" + Instant.now().toEpochMilli();
      default -> faker.yoda().quote() + "-" + Instant.now().toEpochMilli();
    };
  }

  public static List<?> getResourcesFromSearchResult(FhirSearchResultDTO result,
      FhirResourceTypeDTO type) {
    return switch (type) {
      case PRACTITIONER -> getResourcesFromSearchResult(result, type, FhirPractitionerDTO.class);
      case PRACTITIONERROLE -> getResourcesFromSearchResult(result, type, FhirPractitionerRoleDTO.class);
      case ORGANIZATION -> getResourcesFromSearchResult(result, type, FhirOrganizationDTO.class);
      case ENDPOINT -> getResourcesFromSearchResult(result, type, FhirEndpointDTO.class);
      case LOCATION -> getResourcesFromSearchResult(result, type, FhirLocationDTO.class);
      case HEALTHCARESERVICE -> getResourcesFromSearchResult(result, type, FhirHealthcareServiceDTO.class);
    };
  }

  public static <T> List<T> getResourcesFromSearchResult(FhirSearchResultDTO result,
      FhirResourceTypeDTO type, Class<T> clazz) {
    List<FhirBaseResourceDTO> ressourcen = orderByResourceType(result).get(type);
    return nonNull(ressourcen) ? ressourcen.stream().map(clazz::cast).toList() : List.of();
  }

  public static Map<FhirResourceTypeDTO, List<FhirBaseResourceDTO>> orderByResourceType(
      FhirSearchResultDTO res) {
    if (requireNonNull(res.getTotal()) == 0) {
      return new EnumMap<>(FhirResourceTypeDTO.class);
    }
    return requireNonNull(res.getEntry()).stream().map(FhirEntryDTO::getResource)
        .filter(Objects::nonNull)
        .collect(groupingBy(FhirBaseResourceDTO::getResourceType));
  }

  public static List<String> getEndpointIdsOrLocationIdsOfHealthcareService(
      FhirSearchResultDTO res, String parentResourceId, FhirResourceTypeDTO filterFor) {
    Optional<FhirEntryDTO> entry = requireNonNull(
        res.getEntry()).stream()
        .filter(e -> requireNonNull(e.getResource()).getId().equals(parentResourceId))
        .findFirst();
    if (entry.isEmpty()) {
      return List.of();
    }
    return getEndpointIdsOrLocationIdsOfHealthcareService(
        (FhirHealthcareServiceDTO) entry.get().getResource(),
        filterFor);
  }

  public static List<String> getEndpointIdsOrLocationIdsOfHealthcareService(
      FhirHealthcareServiceDTO hs, FhirResourceTypeDTO filterFor) {
    if (filterFor.equals(ENDPOINT)) {
      return nonNull(hs.getEndpoint()) ?
          hs.getEndpoint().stream().map(e -> e.getReference().split("/")[1]).toList() : List.of();
    }
    return nonNull(hs.getLocation()) ?
        hs.getLocation().stream().map(e -> e.getReference().split("/")[1]).toList() : List.of();
  }

  public static String mxidToUrl(String mxid) {
    return mxid.replace(MXID_PREFIX, MXID_URL_PREFIX);
  }

  public static String urlToMxid(String url) {
    return url.replace(MXID_URL_PREFIX, MXID_PREFIX);
  }

  public static void assertMxIdsInEndpoint(List<FhirEndpointDTO> endpoint, List<String> mxids) {
    List<String> mxidsInEndpoints = endpoint.stream().map(FhirEndpointDTO::getAddress).toList();
    assertThat(mxidsInEndpoints).hasSize(mxids.size());
    for (String mxid : mxids) {
      assertThat(mxids).satisfiesAnyOf(
          t -> {
            assertThat(mxidsInEndpoints).contains(mxid);
            individualLog("Mxid found which is not in URL form in assertion");
          },
          t -> assertThat(mxidsInEndpoints).contains(mxidToUrl(mxid))
      );
    }
  }

  public static void assertCorrectEndpointNameAndMxid(List<FhirEndpointDTO> endpoints, Actor searchedActor) {
    String endpointName = getEndpointFromInternalName(searchedActor.recall(DISPLAY_NAME)).getName();
    endpoints = endpoints.stream()
        .filter(e -> requireNonNull(e.getName()).equals(endpointName))
        .toList();
    assertThat(endpoints)
        .as(format("Could not find endpoint with name %s", endpointName))
        .isNotEmpty();

    List<String> mxids = endpoints.stream().map(FhirEndpointDTO::getAddress).toList();
    assertThat(mxids).satisfiesAnyOf(
        t -> {
          individualLog("Mxid found which is not in URL form in assertion");
          assertThat(mxids).contains((String) searchedActor.recall(MX_ID));
        },
        t -> assertThat(mxids).contains(mxidToUrl(searchedActor.recall(MX_ID)))
    );
  }

  @ParameterType(value = "(?:.*)", preferForRegexMatch = true)
  public List<String> listOfStrings(String arg) {
    return stream(arg.split(",\\s?")).map(str -> str.replace("\"", "")).toList();
  }

}
