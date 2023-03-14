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

package de.gematik.tim.test.glue.api.utils;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import io.cucumber.core.exception.CucumberException;
import io.cucumber.java.Scenario;
import java.util.UUID;
import lombok.NoArgsConstructor;
import net.serenitybdd.core.Serenity;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = PRIVATE)
public class TestcaseIdProvider {

  private static final String TCID_PREFIX = "@TCID";
  private static String id;

  public static void startTest(Scenario scenario) {
    if (StringUtils.isNotBlank(id)) {
      throw new TestRunException(
          "An old testcaseId is still available. Please go for sure that all tests got executed and finished correctly.");
    }
    String testId = scenario.getSourceTagNames().stream().filter(t -> t.startsWith(TCID_PREFIX))
        .findFirst()
        .orElseThrow(() -> new CucumberException(
            "This scenario seems to have not TCID! Name: " + scenario.getName()));
    id = format("%s/%s", testId, UUID.randomUUID());
  }

  public static String getTestcaseId() {
    if (isEmpty(id)) {
      throw new TestRunException("The testcase have no id! Please provide id first!");
    }
    return id;
  }

  public static void stopTest() {
    Serenity.recordReportData()
        .withTitle("TestcaseId")
        .andContents(id);
    id = null;
  }
}
