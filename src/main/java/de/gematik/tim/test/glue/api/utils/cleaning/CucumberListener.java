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

package de.gematik.tim.test.glue.api.utils.cleaning;

import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.removeClientOnParallelExecutor;
import static de.gematik.tim.test.glue.api.utils.cleaning.CleanupTrigger.removeClientOnCleanupTrigger;
import static de.gematik.tim.test.glue.api.utils.cleaning.CleanupTrigger.sendCleanupRequest;

import io.cucumber.core.plugin.SerenityReporter;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;

public class CucumberListener extends SerenityReporter {


  @Override
  public void setEventPublisher(EventPublisher publisher) {

    publisher.registerHandlerFor(TestCaseStarted.class, this::startTest);
    publisher.registerHandlerFor(TestRunFinished.class, this::endTest);
  }

  private void endTest(TestRunFinished t) {
    removeClientOnCleanupTrigger();
    removeClientOnParallelExecutor();
  }

  private void startTest(TestCaseStarted tcs) {
    sendCleanupRequest(tcs.getTestCase().getTestSteps());
  }

}
