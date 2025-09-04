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

package de.gematik.tim.test.glue.api.utils;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DIRECT_CHAT_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_ORG_ADMIN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomQuestion.ownRoom;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getAllActiveActors;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getRoomByInternalName;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CHECK_ROOM_STATE_FAIL;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import de.gematik.tim.test.models.FhirBaseResourceDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirEntryDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirLocationDTO;
import de.gematik.tim.test.models.FhirOrganizationDTO;
import de.gematik.tim.test.models.FhirPractitionerDTO;
import de.gematik.tim.test.models.FhirPractitionerRoleDTO;
import de.gematik.tim.test.models.FhirResourceTypeDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMemberDTO;
import de.gematik.tim.test.models.RoomMembershipStateDTO;
import io.cucumber.java.ParameterType;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import jxl.common.AssertionFailed;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class GlueUtils {

  public static final String TEST_RESOURCES_JSON_PATH = "src/test/resources/json/";
  public static final String MXID_PREFIX = "@";
  public static final String MXID_URL_PREFIX = "matrix:u/";
  private static final Faker faker = new Faker();
  private static final Random random = new Random();

  public static RoomDTO getRoomBetweenTwoActors(Actor actor, String senderName) {
    String actorId = actor.recall(MX_ID);
    String senderId = theActorCalled(senderName).recall(MX_ID);
    List<String> membersIds = List.of(actorId, senderId);
    List<RoomDTO> rooms = actor.asksFor(ownRooms());
    RoomDTO room = filterForRoomWithSpecificMembers(rooms, membersIds);
    checkRoomVersion(room);
    return room;
  }

  public static void checkRoomVersion() {
    RoomDTO room = parseResponse(RoomDTO.class);
    checkRoomVersion(room);
  }

  public static void checkRoomVersion(RoomDTO room) {
    if (room.getRoomVersion() == null || versionNotSupported(room.getRoomVersion())) {
      throw new AssertionFailed(
          format(
              "room %s should have a valid version, but version was %s",
              room.getRoomId(), room.getRoomVersion()));
    }
  }

  public static void checkRoomVersionIs(RoomDTO room, String version) {
    if (room.getRoomVersion() == null || !room.getRoomVersion().equals(version)) {
      throw new AssertionFailed(
          format(
              "Room %s does not have correct version. Expected version %s, but was %s",
              room.getRoomId(), version, room.getRoomVersion()));
    }
  }

  private static boolean versionNotSupported(String roomVersion) {
    List<String> supportedVersions = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    return !supportedVersions.contains(roomVersion);
  }

  public static RoomDTO checkRoomMembershipStateInDirectChatOf(String actorName) {
    Actor actor1 = theActorCalled(actorName);
    String roomId = getRoomByInternalName(DIRECT_CHAT_NAME + actor1.recall(MX_ID)).getRoomId();
    return checkRoomMembershipState(actor1.asksFor(ownRoom().withRoomId(roomId)));
  }

  public static RoomDTO checkRoomMembershipState() {
    return checkRoomMembershipState(parseResponse(RoomDTO.class));
  }

  public static RoomDTO checkRoomMembershipState(Actor actor, String internalRoomName) {
    return checkRoomMembershipState(
        actor.asksFor(ownRoom().withRoomId(getRoomByInternalName(internalRoomName).getRoomId())));
  }

  public static RoomDTO checkRoomMembershipState(RoomDTO room) {
    checkIfAllMembersAreCorrect(room);
    checkIfAllActorsRelatedToRoomHaveCorrectMembershipState(room);
    return room;
  }

  public static RoomDTO filterForRoomWithSpecificMembers(
      List<RoomDTO> rooms, List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    assertThat(filteredRooms)
        .as(
            "%s matching room for this members (%s) have been found.",
            filteredRooms.size(), StringUtils.join(memberIds, ","))
        .hasSize(1);
    return filteredRooms.get(0);
  }

  @NotNull
  public static List<RoomDTO> filterForRoomsWithSpecificMembers(
      List<RoomDTO> rooms, List<String> memberIds) {
    return rooms.stream()
        .filter(room -> room.getMembers().size() >= memberIds.size())
        .filter(
            room ->
                new HashSet<>(room.getMembers().stream().map(RoomMemberDTO::getMxid).toList())
                    .containsAll(memberIds))
        .toList();
  }

  public static MessageDTO filterMessageForSenderAndText(
      String messageText, String userName, List<MessageDTO> messages) {
    List<MessageDTO> filteredMessages =
        filterMessagesForSenderAndText(messageText, userName, messages);
    assertThat(filteredMessages)
        .as(
            "%s matching messages for this members (%s) have been found.",
            filteredMessages.size(), filteredMessages.size())
        .hasSize(1);
    return filteredMessages.get(0);
  }

  @NotNull
  public static List<MessageDTO> filterMessagesForSenderAndText(
      String text, String userName, List<MessageDTO> messages) {
    return messages.stream()
        .filter(message -> message.getBody().equals(text))
        .filter(message -> message.getAuthor().equals(theActorCalled(userName).recall(MX_ID)))
        .toList();
  }

  @SneakyThrows
  public static <T> T readJsonFile(String file, Class<T> returnType) {
    return new ObjectMapper()
        .registerModule(new JavaTimeModule())
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
    return faker.gameOfThrones().character()
        + " of "
        + faker.gameOfThrones().house()
        + "-"
        + Instant.now().toEpochMilli();
  }

  public static String createUniqueEndpointName() {
    return format(
        "The %s need some %s for %s",
        faker.hacker().noun(), faker.hacker().ingverb(), faker.hacker().abbreviation());
  }

  public static String createUniqueRoomName() {
    return faker.company().buzzword()
        + " "
        + faker.company().industry()
        + "-"
        + Instant.now().toEpochMilli();
  }

  public static String createUniqueMessageText() {
    String quote =
        switch (random.nextInt(7)) {
          case 1 -> "Game of Thrones: " + faker.gameOfThrones().quote();
          case 2 -> "Back to the future: " + faker.backToTheFuture().quote();
          case 3 -> "Harry Potter: " + faker.harryPotter().quote();
          case 4 -> "Twin Peaks: " + faker.twinPeaks().quote();
          case 5 -> "The big Lebowski: " + faker.lebowski().quote();
          case 6 -> "Buffy the vampire slayer: " + faker.buffy().quotes();
          default -> "Yoda: " + faker.yoda().quote();
        };
    return "Generated text for test - " + circumventProfanityFilters(quote);
  }

  private static String circumventProfanityFilters(String quote) {
    List<String> filterTriggers = List.of("bastards", "bitch", "jerk-off");
    if (filterTriggers.stream().anyMatch(quote::contains)) {
      return "Hitchhikers Guide to the galaxy: " + faker.hitchhikersGuideToTheGalaxy().quote();
    } else {
      return quote;
    }
  }

  public static String createUniqueMessageTextWithTimestamp() {
    return createUniqueMessageText() + "-" + Instant.now().toEpochMilli();
  }

  public static String createTopic() {
    String topic = createUniqueMessageText();
    if (topic.length() > 200) {
      topic = topic.substring(0, 200);
    }
    return topic;
  }

  public static List<?> getResourcesFromSearchResult(
      FhirSearchResultDTO result, FhirResourceTypeDTO type) {
    return switch (type) {
      case PRACTITIONER -> getResourcesFromSearchResult(result, type, FhirPractitionerDTO.class);
      case PRACTITIONER_ROLE ->
          getResourcesFromSearchResult(result, type, FhirPractitionerRoleDTO.class);
      case ORGANIZATION -> getResourcesFromSearchResult(result, type, FhirOrganizationDTO.class);
      case ENDPOINT -> getResourcesFromSearchResult(result, type, FhirEndpointDTO.class);
      case LOCATION -> getResourcesFromSearchResult(result, type, FhirLocationDTO.class);
      case HEALTHCARE_SERVICE ->
          getResourcesFromSearchResult(result, type, FhirHealthcareServiceDTO.class);
    };
  }

  public static <T> List<T> getResourcesFromSearchResult(
      FhirSearchResultDTO result, FhirResourceTypeDTO type, Class<T> clazz) {
    List<FhirBaseResourceDTO> ressourcen = orderByResourceType(result).get(type);
    return nonNull(ressourcen) ? ressourcen.stream().map(clazz::cast).toList() : List.of();
  }

  public static Map<FhirResourceTypeDTO, List<FhirBaseResourceDTO>> orderByResourceType(
      FhirSearchResultDTO searchResult) {
    if (searchResult.getTotal() == 0) {
      return new EnumMap<>(FhirResourceTypeDTO.class);
    }
    return searchResult.getEntry().stream()
        .map(FhirEntryDTO::getResource)
        .collect(groupingBy(FhirBaseResourceDTO::getResourceType));
  }

  public static List<String> getEndpointIdsOrLocationIdsOfHealthcareService(
      FhirSearchResultDTO searchResult, String parentResourceId, FhirResourceTypeDTO filterFor) {
    Optional<FhirEntryDTO> entry =
        searchResult.getEntry().stream()
            .filter(singleEntry -> singleEntry.getResource().getId().equals(parentResourceId))
            .findFirst();
    if (entry.isEmpty()) {
      return List.of();
    }
    return getEndpointIdsOrLocationIdsOfHealthcareService(
        (FhirHealthcareServiceDTO) entry.get().getResource(), filterFor);
  }

  public static List<String> getEndpointIdsOrLocationIdsOfHealthcareService(
      FhirHealthcareServiceDTO healthcareService, FhirResourceTypeDTO filterFor) {
    if (filterFor.equals(ENDPOINT)) {
      return nonNull(healthcareService.getEndpoint())
          ? healthcareService.getEndpoint().stream()
              .map(reference -> reference.getReference().split("/")[1])
              .toList()
          : List.of();
    }
    return nonNull(healthcareService.getLocation())
        ? healthcareService.getLocation().stream()
            .map(reference -> reference.getReference().split("/")[1])
            .toList()
        : List.of();
  }

  public static String mxidToUri(String mxid) {
    return mxid.replace(MXID_PREFIX, MXID_URL_PREFIX);
  }

  public static void assertMxIdsInEndpoint(List<FhirEndpointDTO> endpoint, List<String> mxids) {
    List<String> mxidsInEndpoints = endpoint.stream().map(FhirEndpointDTO::getAddress).toList();
    assertThat(mxidsInEndpoints)
        .as(
            format(
                "Could not find mxids %s. Expected to find %s but found %s",
                mxids, mxids.size(), mxidsInEndpoints.size()))
        .hasSize(mxids.size());
    for (String mxid : mxids) {
      assertThat(mxidsInEndpoints).contains(mxidToUri(mxid));
    }
  }

  public static void assertCorrectEndpointNameAndMxid(
      List<FhirEndpointDTO> endpoints, Actor searchedActor) {
    String endpointName = getEndpointFromInternalName(searchedActor.recall(DISPLAY_NAME)).getName();
    endpoints =
        endpoints.stream().filter(endpoint -> endpoint.getName().equals(endpointName)).toList();
    assertThat(endpoints)
        .as(format("Could not find endpoint with name %s", endpointName))
        .isNotEmpty();

    List<String> mxids = endpoints.stream().map(FhirEndpointDTO::getAddress).toList();
    assertThat(mxids).contains(mxidToUri(searchedActor.recall(MX_ID)));
  }

  public static String prepareApiNameForHttp(String apiName) {
    if (!apiName.startsWith("http")) {
      apiName = "http://" + apiName;
    }
    if (apiName.endsWith("/")) {
      apiName = apiName.substring(0, apiName.length() - 1);
    }
    return apiName;
  }

  private static void checkIfAllActorsRelatedToRoomHaveCorrectMembershipState(RoomDTO room) {
    getAllActiveActors().stream()
        .filter(
            actor ->
                actor.recall(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX) != null
                    && !actor
                        .recall(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX)
                        .equals(RoomMembershipStateDTO.LEAVE))
        .forEach(
            actor -> {
              RoomMembershipStateDTO status =
                  actor.recall(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX);
              String mxid = actor.recall(MX_ID);
              if (!room.getMembers().stream().map(RoomMemberDTO::getMxid).toList().contains(mxid)) {
                handleRoomStateInconsistency(
                    format("%s expected to be in room <%s>", actor.getName(), room.getName()));
              }
              boolean membershipStatusCorrect =
                  room.getMembers().stream()
                      .filter(member -> member.getMxid().equals(mxid))
                      .map(RoomMemberDTO::getMembershipState)
                      .findFirst()
                      .orElseThrow()
                      .equals(status);
              if (!membershipStatusCorrect) {
                handleRoomStateInconsistency(
                    format(
                        "%s should have membership-status <%s> in room <%s>",
                        actor.getName(), status, room.getName()));
              }
            });
  }

  private static void checkIfAllMembersAreCorrect(RoomDTO room) {
    room.getMembers()
        .forEach(
            member -> {
              List<Actor> allActors = getAllActiveActors();
              Actor actorForMember =
                  allActors.stream()
                      .filter(actor -> actor.recall(IS_ORG_ADMIN) == null)
                      .filter(actor -> actor.recall(MX_ID).equals(member.getMxid()))
                      .findFirst()
                      .orElseThrow(
                          () ->
                              new AssertionError(
                                  format(
                                      "Mxid %s should not be in room %s, or to many rooms for this mxid were found.",
                                      member.getMxid(), room.getRoomId())));
              RoomMembershipStateDTO status =
                  actorForMember.recall(room.getRoomId() + OWN_ROOM_MEMBERSHIP_STATUS_POSTFIX);

              if (!member.getMembershipState().equals(status)) {
                handleRoomStateInconsistency(
                    format(
                        "%s should have membership-status <%s> in room <%s>, but <%s> was found",
                        actorForMember.getName(),
                        status,
                        room.getName(),
                        member.getMembershipState()));
              }
            });
  }

  private static void handleRoomStateInconsistency(String msg) {
    if (!CHECK_ROOM_STATE_FAIL) {
      throw new AssertionFailed(msg);
    }
    individualLog(msg);
  }

  @ParameterType(value = "(?:.*)", preferForRegexMatch = true)
  public List<String> listOfStrings(String arg) {
    return stream(arg.split(",\\s?")).map(str -> str.replace("\"", "")).toList();
  }

  @ParameterType(value = "(?:.*)", name = "listOfInts")
  public List<Integer> listOfInts(String arg) {
    return stream(arg.split(",\\s?")).map(str -> Integer.valueOf(str.replace("\"", ""))).toList();
  }

  public static <T extends Actor> String getHomeServerWithoutHttpAndPort(T actor) {
    return actor.recall(HOME_SERVER).toString().replaceAll("http.?://", "").split(":")[0];
  }
}
