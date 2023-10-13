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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DIRECT_CHAT_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareServiceInfo;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.java.Scenario;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import lombok.NoArgsConstructor;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = PRIVATE)
public class TestcasePropertiesManager {

  public static final String EXCEPTION_MESSAGE = "The requested %s with name: %s does not exist in central management. Is it created before?";
  private static final String TCID_PREFIX = "@TCID";
  private static String id;
  private static String lastTcId;
  private static Map<String, HealthcareServiceInfo> healthcareServices;
  private static Map<String, RoomDTO> rooms;
  private static Map<String, MessageDTO> messages;

  public static void startTest(Scenario scenario) {
    createTestcaseId(scenario);
    resetPropertiesMap();
  }

  public static void stopTest() {
    Serenity.recordReportData()
        .withTitle("TestcaseId")
        .andContents(id);
    id = null;
  }

  private static void createTestcaseId(Scenario scenario) {
    if (StringUtils.isNotBlank(id)) {
      throw new TestRunException(
          "An old testcaseId is still available. Please go for sure that all tests got executed and finished correctly.");
    }
    String testId = scenario.getSourceTagNames().stream().filter(t -> t.startsWith(TCID_PREFIX))
        .findFirst()
        .orElseThrow(() -> new CucumberException(
            "This scenario seems to have not TCID! Name: " + scenario.getName()));
    id = format("%s/%s", testId, UUID.randomUUID());
    lastTcId = id;
  }

  public static String getTestcaseId() {
    if (isEmpty(id)) {
      throw new TestRunException("The testcase has no id! Please provide id first!");
    }
    return id;
  }

  public static String getLastTcId() {
    return lastTcId;
  }

  private static void resetPropertiesMap() {
    healthcareServices = new HashMap<>();
    rooms = new HashMap<>();
    messages = new HashMap<>();
  }

  public static void addHs(String name, HealthcareServiceInfo info) {
    healthcareServices.put(name, info);
  }

  public static void addRoom(String name, RoomDTO room) {
    rooms.put(name, room);
  }

  public static void addMessage(String name, MessageDTO message) {
    messages.put(name, message);
  }

  public static HealthcareServiceInfo getHsFromInternalName(String name) {
    if (healthcareServices.containsKey(name)) {
      return healthcareServices.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "HealthcareService", name));
  }

  public static RoomDTO getRoomByInternalName(String name) {
    if (rooms.containsKey(name)) {
      return rooms.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "room", name));
  }

  public static String getInternalRoomNameForActor(RoomDTO room, Actor actor) {
    List<Entry<String, RoomDTO>> entries = rooms.entrySet()
        .stream()
        .filter(e -> (e.getValue()).getRoomId().equals(room.getRoomId()))
        .toList();
    if (entries.size() == 1) {
      return entries.get(0).getKey();
    }
    if (nonNull(actor)) {
      return entries.stream()
          .filter(e -> !e.getKey().contains(actor.recall(MX_ID)) && e.getKey().contains(DIRECT_CHAT_NAME))
          .map(Entry::getKey)
          .findFirst().orElse(room.getName());
    }
    return room.getName();
  }


  public static MessageDTO getCreatedMessage(String name) {
    if (messages.containsKey(name)) {
      return messages.get(name);
    }
    throw new TestRunException(format(EXCEPTION_MESSAGE, "message", name));
  }
}
