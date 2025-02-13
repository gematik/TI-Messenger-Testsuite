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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.*;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareServiceInfo;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.TestCaseStarted;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

@NoArgsConstructor(access = PRIVATE)
public class TestcasePropertiesManager {

  private static final String EXCEPTION_MESSAGE =
      "The requested %s with name: %s does not exist in central management. Was it created before?";
  private static final String TCID_PREFIX = "@TCID";
  private static String id;
  private static boolean dryRun;
  private static Map<String, HealthcareServiceInfo> healthcareServices;
  private static Map<String, FhirEndpointDTO> endpoints;
  private static Map<String, RoomDTO> rooms;
  private static Map<String, MessageDTO> messages;
  private static List<Actor> failedTeardownActors;
  private static CopyOnWriteArrayList<Actor> activeActors;
  @Getter private static Scenario currentScenario;
  @Getter private static boolean runningParallel;

  public static void createTestcaseId(TestCaseStarted testCaseStarted) {
    String testId =
        testCaseStarted.getTestCase().getTags().stream()
            .filter(tag -> tag.startsWith(TCID_PREFIX))
            .findFirst()
            .orElseThrow(
                () ->
                    new CucumberException(
                        "This scenario seems to have no TCID! Name: "
                            + testCaseStarted.getTestCase().getName()));
    id = format("%s/%s", testId, UUID.randomUUID());
  }

  public static void setDryRun(boolean newDryRun) {
    dryRun = newDryRun;
  }

  public static boolean isDryRun() {
    return dryRun;
  }

  public static void startTest(Scenario scenario) {
    currentScenario = scenario;
    reset();
    Serenity.recordReportData().withTitle("TestcaseId").andContents(id);
  }

  public static String getTestcaseId() {
    if (isEmpty(id)) {
      throw new TestRunException("The testcase has no id! Please provide id first!");
    }
    return id;
  }

  public static void addHs(String name, HealthcareServiceInfo info) {
    healthcareServices.put(name, info);
  }

  public static void addEndpoint(String name, FhirEndpointDTO endpoint) {
    endpoints.put(name, endpoint);
  }

  public static void addRoom(String name, RoomDTO room) {
    rooms.put(name, room);
  }

  public static void addMessage(String name, MessageDTO message) {
    messages.put(name, message);
  }

  public static HealthcareServiceInfo getHealthcareServiceFromInternalName(String name) {
    if (healthcareServices.containsKey(name)) {
      return healthcareServices.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "HealthcareService", name));
  }

  public static FhirEndpointDTO getEndpointFromInternalName(String name) {
    if (endpoints.containsKey(name)) {
      return endpoints.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "Endpoint", name));
  }

  public static RoomDTO getRoomByInternalName(String name) {
    if (rooms.containsKey(name)) {
      return rooms.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "room", name));
  }

  public static String getInternalRoomNameForActor(RoomDTO room, Actor actor) {
    List<Entry<String, RoomDTO>> entries =
        rooms.entrySet().stream()
            .filter(entry -> (entry.getValue().getRoomId()).equals(room.getRoomId()))
            .toList();
    if (entries.size() == 1) {
      return entries.get(0).getKey();
    }
    if (nonNull(actor)) {
      return entries.stream()
          .filter(
              entry ->
                  !entry.getKey().contains(actor.recall(MX_ID))
                      && entry.getKey().contains(DIRECT_CHAT_NAME))
          .map(Entry::getKey)
          .findFirst()
          .orElse(room.getName());
    }
    return room.getName();
  }

  public static void removeInternalEndpointWithName(String externalName) {
    endpoints =
        endpoints.entrySet().stream()
            .filter(endpoint -> !endpoint.getValue().getName().equals(externalName))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static MessageDTO getCreatedMessage(String messageText) {
    if (messages.containsKey(messageText)) {
      return messages.get(messageText);
    }
    throw new TestRunException(
        format(
            "The requested message with text: %s does not exist in central management. Was it created before?",
            messageText));
  }

  public static <T extends Actor> void addFailedActor(T actor) {
    failedTeardownActors.add(actor);
  }

  public static <T extends Actor> boolean isActorFailed(T actor) {
    return failedTeardownActors.contains(actor);
  }

  public static <T extends Actor> void registerActor(T actor) {
    activeActors.add(actor);
  }

  public static List<Actor> getAllActiveActors() {
    return activeActors;
  }

  public static List<Actor> getAllActiveActorsByMxIds(List<String> mxids) {
    return getAllActiveActorsByMxIds(mxids, true);
  }

  public static List<Actor> getAllActiveActorsByMxIds(
      List<String> mxids, boolean shouldFindAllMxidsAsActiveActor) {
    List<Actor> actors =
        activeActors.stream()
            .filter(
                actor -> actor.recall(IS_ORG_ADMIN) == null && mxids.contains(actor.recall(MX_ID)))
            .toList();
    if (shouldFindAllMxidsAsActiveActor && mxids.size() != actors.size()) {
      throw new TestRunException(
          format(
              "Unknown actor/s with mxid/s <%s> was requested",
              mxids.stream()
                  .filter(
                      mxid ->
                          !actors.stream()
                              .map(actor -> actor.recall(MX_ID))
                              .toList()
                              .contains(mxid))));
    }
    return actors;
  }

  public static void setParallelFlag(boolean status) {
    runningParallel = status;
  }

  private static void reset() {
    healthcareServices = new HashMap<>();
    endpoints = new HashMap<>();
    rooms = new HashMap<>();
    messages = new HashMap<>();
    failedTeardownActors = new ArrayList<>();
    activeActors = new CopyOnWriteArrayList<>();
    runningParallel = false;
  }
}
