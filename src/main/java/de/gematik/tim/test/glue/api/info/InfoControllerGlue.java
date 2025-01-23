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

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MATRIX_CLIENT_VERSION;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_INFO;
import static de.gematik.tim.test.glue.api.info.ApiInfoQuestion.apiInfo;
import static de.gematik.tim.test.glue.api.info.CallMatrixApiTask.callForbiddenMatrixEndpoint;
import static de.gematik.tim.test.glue.api.info.SupportedMatrixVersionQuestion.matrixSupportedServerVersion;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.prepareApiNameForHttp;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpStatus.OK;

import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class InfoControllerGlue {

  @When("{string} requests information about the test driver")
  @Wenn("{string} Informationen über den Testtreiber anfragt")
  public void getInfoEndpoint(String actorName) {
    theActorCalled(actorName).attemptsTo(GET_INFO.request());
  }

  @Then("test driver information is returned to {string}")
  @Dann("werden Informationen über den Testtreiber an {string} zurückgegeben")
  public void returnsAppInfo(String actorName) {
    checkResponseCode(actorName, OK.value());
    theActorCalled(actorName)
        .should(
            seeThatResponse(
                "check application info is not empty",
                res ->
                    res.statusCode(200)
                        .body("title", not(emptyOrNullString()))
                        .body("description", not(emptyOrNullString()))
                        .body("contact", not(emptyOrNullString()))
                        .body("clientInfo.version", not(emptyOrNullString()))
                        .body("fachdienstInfo.version", not(emptyOrNullString()))
                        .body("testDriverVersion", not(emptyOrNullString()))));
  }

  @Given("{word} request Home-Server-Address at api {word}")
  @Angenommen("{word} fragt an Schnittstelle {word} die Home-Server-Adresse ab")
  public void requestHomeServerAddressOnInfoEndpoint(String actorName, String apiName) {
    String apiUrl = prepareApiNameForHttp(apiName);
    Actor actor = theActorCalled(actorName).can(CallAnApi.at(apiUrl));
    apiInfo().withActor(actor).run();
  }

  @When("{word} request supported Matrix-Versions from Home-Server")
  @Wenn("{word} die supporteten Matrix-Versions vom Home-Server abfragt")
  public void requestMatrixVersionFromHomeServer(String actorName) {
    Actor actor = theActorCalled(actorName);

    actor.can(CallAnApi.at(actor.recall(HOME_SERVER)));
    actor.asksFor(matrixSupportedServerVersion());
  }

  @Then("no version is higher than {string}")
  @Dann("ist keine unterstützte Version über {string}")
  public void noVersionIsHigherThan(String highestVersion) {
    SupportedMatrixVersionInfo supportedMatrixVersionInfo =
        theActorInTheSpotlight().recall(MATRIX_CLIENT_VERSION);
    List<SemVersion> supportedVersions =
        supportedMatrixVersionInfo.versions().stream().map(SemVersion::new).toList();
    SemVersion maxVersion = new SemVersion(highestVersion);
    assertThat(supportedVersions.stream().anyMatch(version -> version.compareTo(maxVersion) > 0))
        .as("Found higher version than %s in %s", maxVersion, supportedVersions)
        .isFalse();
  }

  @And("{word} requests at interface {word} the API {string} with http method {string}")
  @Und("{word} fragt an Schnittstelle {word} die API {string} über ein {string} ab")
  public void pingApiOnHomeserver(
      String actorName, String interfaceName, String matrixUrl, String httpMethod) {
    String interfaceUrl = prepareApiNameForHttp(interfaceName);
    Actor actor = theActorCalled(actorName).can(CallAnApi.at(interfaceUrl));
    actor.attemptsTo(callForbiddenMatrixEndpoint(matrixUrl, httpMethod));
  }

  @And(
      "{word} requests at interface {word} the profile API {string} including parameters with http method {string}")
  @Und(
      "{word} fragt an Schnittstelle {word} die profile API {string} inkl Parameterbefüllung über ein {string} ab")
  public void pingApiOnHomeserverForUser(
      String actorName, String interfaceName, String matrixUrl, String httpMethod) {
    String interfaceUrl = prepareApiNameForHttp(interfaceName);
    Actor actor = theActorCalled(actorName).can(CallAnApi.at(interfaceUrl));
    String matrixUrlIncludingParameter = matrixUrl.replace("{userId}", "@tim-gematik:matrix.org");
    actor.attemptsTo(callForbiddenMatrixEndpoint(matrixUrlIncludingParameter, httpMethod));
  }
}
