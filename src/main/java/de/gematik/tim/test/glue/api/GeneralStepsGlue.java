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

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

import de.gematik.tim.test.glue.api.threading.ParallelExecutor;
import de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import net.serenitybdd.screenplay.Actor;
import org.hamcrest.Matcher;

public class GeneralStepsGlue {

  private GeneralStepsGlue() {
  }

  @Then("{string} receives a response code {int}")
  @Dann("erhält {string} einen Responsecode {int}")
  public static void checkResponseCode(String actorName, int responseCode) {
    checkResponseCode(theActorCalled(actorName), responseCode);
  }

  public static void checkResponseCode(Actor actor, int responseCode) {
    if (TestcasePropertiesManager.isRunningParallel()) {
      assertThat(ParallelExecutor.getLastResponseCodeForActor(actor.getName())).isEqualTo(responseCode);
    } else {
      actor.should(seeThatResponse(res -> res.statusCode(responseCode)));
    }
  }

  @Then("the operation was successful")
  @Dann("war die Operation erfolgreich")
  public static void checkResponseCode() {
    Matcher<Integer> is2xx = allOf(greaterThanOrEqualTo(200), lessThan(300));
    theActorInTheSpotlight().should(seeThatResponse(res -> res.statusCode(is2xx)));
  }
}
