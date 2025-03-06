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

package de.gematik.tim.test.glue.api;

import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.threading.ParallelExecutor;
import de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;

public class GeneralStepsGlue {

  private GeneralStepsGlue() {}

  @Then("{string} receives a response code {int}")
  @Dann("erhält {string} einen Responsecode {int}")
  public static void checkResponseCode(String actorName, int responseCode) {
    if (TestcasePropertiesManager.isRunningParallel()) {
      assertThat(ParallelExecutor.getLastResponseCodeForActor(actorName)).isEqualTo(responseCode);
    } else {
      if (lastResponse().statusCode() != responseCode) {
        throw new TestRunException("Operation returned error code " + lastResponse().statusCode());
      }
    }
  }

  @Then("{string} checks, that the response is not empty")
  @Dann("{string} überprüft, dass die Response befüllt ist")
  public static void responseContainsBody(String actorName) {
    theActorCalled(actorName)
        .should(
            seeThatResponse(
                "check response body is not empty", res -> res.body(not(emptyOrNullString()))));
  }
}
