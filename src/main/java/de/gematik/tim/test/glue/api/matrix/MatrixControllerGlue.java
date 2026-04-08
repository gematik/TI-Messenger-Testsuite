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

package de.gematik.tim.test.glue.api.matrix;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCESS_TOKEN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MATRIX_CLIENT_VERSION;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MEDIA_URI;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.matrix.questions.SupportedMatrixVersionQuestion.matrixSupportedServerVersion;
import static de.gematik.tim.test.glue.api.matrix.questions.SynapseVersionQuestion.synapseServerVersion;
import static de.gematik.tim.test.glue.api.matrix.tasks.CallMatrixApiTask.callMatrixEndpoint;
import static de.gematik.tim.test.glue.api.matrix.tasks.CallMatrixApiWithoutForwardingTask.callMatrixEndpointWithoutForwarding;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

public class MatrixControllerGlue {

  @When("{word} request supported Matrix-Versions from Home-Server")
  @Wenn("{word} die supporteten Matrix-Versions vom Home-Server abfragt")
  public void requestMatrixVersionFromHomeServer(String actorName) {
    Actor actor = theActorCalled(actorName);
    runWithHomeserverAsApi(() -> actor.asksFor(matrixSupportedServerVersion()), actor);
  }

  private void runWithHomeserverAsApi(Runnable task, Actor actor) {
    final String currentApiUrl = CallAnApi.as(actor).resolve("");
    try {
      final String homeServerUrl = actor.recall(HOME_SERVER);
      actor.can(CallAnApi.at(homeServerUrl));
      task.run();
    } finally {
      actor.can(CallAnApi.at(currentApiUrl));
    }
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
    runWithHomeserverAsApi(
        () -> {
          if (matrixUrl.startsWith("/.well-known")) {
            pingApiWithoutForwarding(actor, httpMethod, matrixUrl);
            return;
          }
          actor.attemptsTo(callMatrixEndpoint(httpMethod, matrixUrl));
        },
        actor);
  }

  @And(
      "{word} requests at their homeserver the API {string} with http method {string}, including the access_token")
  @Und(
      "{word} fragt inklusive access_token an seinem HomeServer die API {string} über ein {string} ab")
  public void pingApiOnHomeserverWithAccessToken(
      String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    String accessToken = actor.recall(ACCESS_TOKEN);
    if (accessToken == null || accessToken.isEmpty()) {
      throw new TestRunException("An access token is required for this API call.");
    }
    runWithHomeserverAsApi(
        () ->
            actor.attemptsTo(
                callMatrixEndpoint(httpMethod, matrixUrl).withAccessToken(accessToken)),
        actor);
  }

  private void pingApiWithoutForwarding(Actor actor, String httpMethod, String matrixUrl) {
    actor.attemptsTo(callMatrixEndpointWithoutForwarding(httpMethod, matrixUrl));
  }

  @And(
      "{word} requests at their homeserver the preview API {string} including parameter url as {string}, including the access_token")
  @Und(
      "{word} fragt inklusive access_token an seinem HomeServer die preview API {string} inkl Parameter url als {string} ab")
  public void pingApiOnHomeserverForPreview(String actorName, String matrixUrl, String url) {
    Actor actor = theActorCalled(actorName);
    String accessToken = actor.recall(ACCESS_TOKEN);
    if (accessToken == null || accessToken.isEmpty()) {
      throw new TestRunException("An access token is required for this API call.");
    }

    runWithHomeserverAsApi(
        () ->
            actor.attemptsTo(
                callMatrixEndpoint("GET", matrixUrl)
                    .withAccessToken(accessToken)
                    .withRequestParameter(Map.of("url", url))),
        actor);
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
    runWithHomeserverAsApi(
        () -> {
          String matrixUrlIncludingParameter = matrixUrl.replace("{userId}", userId);
          actor.attemptsTo(callMatrixEndpoint(httpMethod, matrixUrlIncludingParameter));
        },
        actor);
  }

  @And(
      "{word} requests at their homeserver the media API {string} including parameters with http method {string}")
  @Und(
      "{word} fragt an seinem HomeServer die media API {string} inkl Parameterbefüllung über ein {string} ab")
  public void pingApiOnHomeserverForMedia(String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    runWithHomeserverAsApi(
        () -> {
          String mediaUri = actor.recall(MEDIA_URI);
          String urlIncludingFilename = constructMediaUrl(mediaUri, matrixUrl);
          actor.attemptsTo(callMatrixEndpoint(httpMethod, urlIncludingFilename));
        },
        actor);
  }

  private String constructMediaUrl(String mediaUri, String matrixUrl) {
    String[] uriTokens = mediaUri.split("/");
    if (uriTokens.length != 4) {
      throw new TestRunException("Could not parse provided media uri " + mediaUri);
    }
    String serverName = uriTokens[2];
    String mediaId = uriTokens[3];

    String urlIncludingServername = matrixUrl.replace("{serverName}", serverName);
    String urlIncludingMediaId = urlIncludingServername.replace("{mediaId}", mediaId);
    return urlIncludingMediaId.replace("{fileName}", "myNewFilename");
  }

  @And(
      "{word} requests at their homeserver the media thumbnail API {string} including parameters with http method {string}")
  @Und(
      "{word} fragt an seinem HomeServer die media thumbnail API {string} inkl Parameterbefüllung über ein {string} ab")
  public void pingApiOnHomeserverForMediaThumbnail(
      String actorName, String matrixUrl, String httpMethod) {
    Actor actor = theActorCalled(actorName);
    runWithHomeserverAsApi(
        () -> {
          String mediaUri = actor.recall(MEDIA_URI);
          String urlIncludingFilename = constructMediaUrl(mediaUri, matrixUrl);
          actor.attemptsTo(
              callMatrixEndpoint(httpMethod, urlIncludingFilename)
                  .withRequestParameter(Map.of("height", "32", "width", "32")));
        },
        actor);
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

  @When("{word} requests the Synapse-Server-Version from Home-Server")
  @Wenn("{word} die Synapse-Server-Version vom Home-Server abfragt")
  public void requestSynapseServerVersionFromHomeServer(String actorName) {
    Actor actor = theActorCalled(actorName);
    runWithHomeserverAsApi(() -> actor.asksFor(synapseServerVersion()), actor);
  }

  @Then("Synapse-Server-Version is returned to {word}")
  @Dann("wird Synapse-Server-Version an {word} zurückgegeben")
  public void returnsSynapseServerVersion(String actorName) {
    // this step is empty, since Synapse is not a required component of TI-M and the response cannot
    // be automatically tested
  }

  @Then("{word} requests the API {string} at the homeserver with a POST")
  @Dann("{word} fragt an seinem HomeServer die API {string} über ein POST ab")
  public void callApiWithPost(String actorName, String matrixUrl) {
    Actor actor = theActorCalled(actorName);
    String publicRoomsFilter =
        """
        {
          "filter": {
            "generic_search_term": "gematik"
          },
          "limit": 10
        }
        """;
    runWithHomeserverAsApi(
        () ->
            actor.attemptsTo(
                callMatrixEndpoint("POST", matrixUrl).withRequestBody(publicRoomsFilter)),
        actor);
  }
}
