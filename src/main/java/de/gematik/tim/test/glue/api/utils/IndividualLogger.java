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
import io.cucumber.java.Scenario;
import lombok.SneakyThrows;
import net.serenitybdd.core.Serenity;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.INDIVIDUAL_LOG_PATH;
import static org.apache.commons.lang3.StringUtils.join;

public class IndividualLogger {

  private static final File file = new File(INDIVIDUAL_LOG_PATH);
  private static final Set<IndividualLogEntry> OVERALL_LOGS = new TreeSet<>();
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static Set<IndividualLogEntry> logs = new TreeSet<>();

  public static void startTest() {
    logs = new TreeSet<>();
  }

  public static synchronized void individualLog(String msg) {
    Scenario s = TestcasePropertiesManager.getCurrentScenario();
    String tcid = TestcasePropertiesManager.getTestcaseId();
    IndividualLogEntry newEntry = new IndividualLogEntry(s.getName(), TestcasePropertiesManager.getTestcaseId(),
        new HashMap<>(Map.of(msg, Pair.of(getTimestamp(), 1))));
    if (logs.contains(newEntry)) {
      logs.stream()
          .filter(l -> l.testcaseName.equals(s.getName()) && l.testcaseId.equals(tcid))
          .findFirst()
          .orElseThrow()
          .addCountingMessage(msg);
    } else {
      logs.add(newEntry);
    }
  }

  @SneakyThrows
  public static void addToReport() {
    if (logs.isEmpty()) {
      return;
    }
    Serenity.recordReportData().withTitle("Individual Log").andContents(join(logs, "\n"));
    OVERALL_LOGS.addAll(logs);
    FileUtils.write(file, MAPPER.writeValueAsString(OVERALL_LOGS), StandardCharsets.UTF_8);
  }

  private static String getTimestamp() {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
  }

  public record IndividualLogEntry(String testcaseName, String testcaseId, Map<String, Pair> messages)
      implements Comparable<IndividualLogEntry> {

    public void addCountingMessage(String msg) {
      messages.put(msg, Pair.of(getTimestamp(), messages.getOrDefault(msg, Pair.of(null, 0)).amount() + 1));
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof IndividualLogEntry o) {
        return o.testcaseId.equals(this.testcaseId);
      }
      return false;
    }

    @Override
    public int compareTo(@NotNull IndividualLogEntry other) {
      return other.testcaseId.compareTo(this.testcaseId);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Name: ").append(this.testcaseName).append("\n");
      sb.append("\tTCID: ").append(this.testcaseId).append("\n");
      sb.append("\tMessages: ").append(this.testcaseId).append("\n");
      this.messages.forEach((k, v) -> sb.append("\t\t").append(v.lastOccurrence() + " -> times: " + v.amount() + " => " + k + "\n"));

      return sb.toString();
    }
  }

  public record Pair(String lastOccurrence, Integer amount) {

    static Pair of(String lastOccurrence, Integer amount) {
      return new Pair(lastOccurrence, amount);
    }
  }

}
