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

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCESS_TOKEN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MATRIX_CLIENT_VERSION;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MEDIA_URI;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.TEST_DRIVER_URL;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_INFO;
import static de.gematik.tim.test.glue.api.info.ApiInfoQuestion.apiInfo;
import static de.gematik.tim.test.glue.api.info.CallMatrixApiTask.callMatrixEndpoint;
import static de.gematik.tim.test.glue.api.info.CallMatrixApiWithoutForwardingTask.callMatrixEndpointWithoutForwarding;
import static de.gematik.tim.test.glue.api.info.SupportedMatrixVersionQuestion.matrixSupportedServerVersion;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.prepareApiNameForHttp;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
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

    registerHomeserverAsApi(actor);
    actor.asksFor(matrixSupportedServerVersion());
    registerTestDriverAsApi(actor);
  }

  private void registerHomeserverAsApi(Actor actor) {
    String testDriverUrl = actor.abilityTo(CallAnApi.class).resolve("");
    actor.remember(TEST_DRIVER_URL, testDriverUrl);
    actor.can(CallAnApi.at(actor.recall(HOME_SERVER)));
  }

  private void registerTestDriverAsApi(Actor actor) {
    actor.can(CallAnApi.at(actor.recall(TEST_DRIVER_URL)));
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

  @And("{word} requests at their homeserver the API {string} with http method {string}")
  @Und("{word} fragt an seinem HomeServer die API {string} über ein {string} ab")
  public void pingApiOnHomeserver(String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    registerHomeserverAsApi(actor);
    if (matrixUrl.startsWith("/.well-known")) {
      pingApiWithoutForwarding(actor, httpMethod, matrixUrl);
      return;
    }
    actor.attemptsTo(callMatrixEndpoint(httpMethod, matrixUrl));
    registerTestDriverAsApi(actor);
  }

  @And(
      "{word} requests at their homeserver the API {string} with http method {string}, including the access_token")
  @Und(
      "{word} fragt inklusive access_token an seinem HomeServer die API {string} über ein {string} ab")
  public void pingApiOnHomeserverWithAccessToken(
      String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    registerHomeserverAsApi(actor);
    String accessToken = actor.recall(ACCESS_TOKEN);
    if (accessToken == null || accessToken.isEmpty()) {
      throw new TestRunException("An access token is required for this API call.");
    }
    actor.attemptsTo(callMatrixEndpoint(httpMethod, matrixUrl).withAccessToken(accessToken));
    registerTestDriverAsApi(actor);
  }

  private void pingApiWithoutForwarding(Actor actor, String httpMethod, String matrixUrl) {
    actor.attemptsTo(callMatrixEndpointWithoutForwarding(httpMethod, matrixUrl));
  }

  @When(
      "{word} requests at their homeserver the profile API {string} including parameters of user {string} with http method {string}")
  @Wenn(
      "{word} fragt an seinem HomeServer die profile API {string} inkl Parameterbefüllung von {string} über ein {string} ab")
  public void pingApiOnHomeserverWithUserParameters(
      String actorName, String matrixUrl, String userName, String httpMethod) {
    Actor user = theActorCalled(userName);
    String userId = user.recall(MX_ID);

    Actor actor = theActorCalled(actorName);
    registerHomeserverAsApi(actor);
    String matrixUrlIncludingParameter = matrixUrl.replace("{userId}", userId);
    actor.attemptsTo(callMatrixEndpoint(httpMethod, matrixUrlIncludingParameter));
    registerTestDriverAsApi(actor);
  }

  @And(
      "{word} requests at their homeserver the media API {string} including parameters with http method {string}")
  @Und(
      "{word} fragt an seinem HomeServer die media API {string} inkl Parameterbefüllung über ein {string} ab")
  public void pingApiOnHomeserverForMedia(String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    registerHomeserverAsApi(actor);
    String mediaUri = actor.recall(MEDIA_URI);
    String[] uriTokens = mediaUri.split("/");
    if (uriTokens.length != 4) {
      throw new TestRunException("Could not parse provided media uri " + mediaUri);
    }
    String serverName = uriTokens[2];
    String mediaId = uriTokens[3];

    String urlIncludingServername = matrixUrl.replace("{serverName}", serverName);
    String urlIncludingMediaId = urlIncludingServername.replace("{mediaId}", mediaId);
    String urlIncludingFilename = urlIncludingMediaId.replace("{fileName}", "myNewFilename");

    actor.attemptsTo(callMatrixEndpoint(httpMethod, urlIncludingFilename));
    registerTestDriverAsApi(actor);
  }

  @And(
      "{word} requests at their homeserver the media thumbnail API {string} including parameters with http method {string}")
  @Und(
      "{word} fragt an seinem HomeServer die media thumbnail API {string} inkl Parameterbefüllung über ein {string} ab")
  public void pingApiOnHomeserverForMediaThumbnail(
      String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    registerHomeserverAsApi(actor);
    String mediaUri = actor.recall(MEDIA_URI);
    String[] uriTokens = mediaUri.split("/");
    if (uriTokens.length != 4) {
      throw new TestRunException("Could not parse provided media uri " + mediaUri);
    }
    String serverName = uriTokens[2];
    String mediaId = uriTokens[3];

    String urlIncludingServername = matrixUrl.replace("{serverName}", serverName);
    String urlIncludingMediaId = urlIncludingServername.replace("{mediaId}", mediaId);
    String urlIncludingFilename = urlIncludingMediaId.replace("{fileName}", "myNewFilename");

    actor.attemptsTo(
        callMatrixEndpoint(httpMethod, urlIncludingFilename)
            .withRequestParameter(Map.of("height", "32", "width", "32")));
    registerTestDriverAsApi(actor);
  }

  @When("is the supported Matrix version {string}")
  @Wenn("ist die unterstützte Matrix-Version {string}")
  public void checkMatrixVersionInLastResponse(String matrixVersion) {
    SupportedMatrixVersionInfo supportedMatrixVersionInfo =
        theActorInTheSpotlight().recall(MATRIX_CLIENT_VERSION);
    List<String> supportedVersions =
        supportedMatrixVersionInfo.versions().stream()
            .map(version -> version.replaceAll("[^0-9.]", ""))
            .toList();
    boolean matrixVersionFound =
        supportedVersions.stream().anyMatch(version -> version.equals(matrixVersion));
    assertThat(matrixVersionFound)
        .as("Expected Matrix version %s was not found", matrixVersion)
        .isTrue();
  }
}
