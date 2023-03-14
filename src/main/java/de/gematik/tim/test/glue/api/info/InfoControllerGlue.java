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

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_INFO;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

import io.cucumber.java.Before;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.actors.Cast;

public class InfoControllerGlue {

  @Before
  public void setup() {
    setTheStage(Cast.ofStandardActors());
  }


  @When("{string} requests information about the test driver")
  @Wenn("{string} Informationen über den Testtreiber anfragt")
  public void getInfoEndpoint(String actorName) {
    theActorCalled(actorName).attemptsTo(GET_INFO.request());
  }

  @Then("test driver information is returned to {string}")
  @Dann("werden Informationen über den Testtreiber an {string} zurückgegeben")
  public void returnsAppInfo(String actorName) {
    theActorCalled(actorName).should(seeThatResponse("check application info is not empty",
        res -> res.statusCode(200)
            .body("title", not(emptyOrNullString()))
            .body("description", not(emptyOrNullString()))
            //.body("termsOfService", not(emptyOrNullString()))
            .body("contact", not(emptyOrNullString()))
            .body("clientInfo.version", not(emptyOrNullString()))
            .body("fachdienstInfo.version", not(emptyOrNullString()))
            .body("testDriverVersion", not(emptyOrNullString()))
    ));
  }
}
