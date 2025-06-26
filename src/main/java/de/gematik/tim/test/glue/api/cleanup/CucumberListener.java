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

package de.gematik.tim.test.glue.api.cleanup;

import static de.gematik.tim.test.glue.api.cleanup.CleanupTrigger.removeClientOnCleanupTrigger;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.removeClientOnParallelExecutor;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.createTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.setDryRun;

import io.cucumber.core.plugin.SerenityReporterParallel;
import io.cucumber.core.runner.TestCaseDelegate;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;

public class CucumberListener extends SerenityReporterParallel {

  @Override
  public void setEventPublisher(EventPublisher publisher) {
    publisher.registerHandlerFor(TestCaseStarted.class, this::startTest);
    publisher.registerHandlerFor(TestRunFinished.class, this::endTest);
  }

  private void endTest(TestRunFinished t) {
    removeClientOnCleanupTrigger();
    removeClientOnParallelExecutor();
    TestCaseContext.clear();
  }

  private void startTest(TestCaseStarted tcs) {
    if (TestCaseDelegate.of(tcs.getTestCase()).isDryRun()) {
      setDryRun(true);
    } else {
      setDryRun(false);
      createTestcaseId(tcs);
      TestCaseContext.setTestCase(tcs.getTestCase());
    }
  }
}
