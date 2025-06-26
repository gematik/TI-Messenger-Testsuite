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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirAuthenticateTask.authenticateOnFhirVzd;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirDeleteEndpointVisibilityTask.deleteEndpointVisibility;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirDeleteOwnMxidTask.deleteMxidFromFhir;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSearchQuestion.practitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSetEndpointVisibilityTask.setEndpointVisibility;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSetMxidTask.setMxid;
import static de.gematik.tim.test.glue.api.fhir.practitioner.NoFhirVisibilityQuestion.practitionerVisibleInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.practitioner.OwnFhirResourceQuestion.ownFhirResource;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertCorrectEndpointNameAndMxid;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.lang.String.format;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import de.gematik.tim.test.models.FhirCodingDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirExtensionDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.serenitybdd.screenplay.Actor;

public class FhirAdministrationGlue {

  @When("{listOfStrings} sets MXID in own TIPractitioner resource")
  @Wenn("{listOfStrings} hinterlegt seine MXID im Verzeichnis Dienst")
  @Wenn("{listOfStrings} hinterlegen ihre MXIDs im Verzeichnis Dienst")
  public void addToFhir(List<String> actorNames) {
    actorNames.forEach(
        actorName -> {
          Actor actor = theActorCalled(actorName);
          actor.attemptsTo(authenticateOnFhirVzd());
          checkResponseCode(actorName, OK.value());
          actor.attemptsTo(setMxid());
          checkResponseCode(actorName, CREATED.value());
        });
  }

  @Then("{listOfStrings} removes the MXID in the own TIPractitioner FHIR resource")
  @Then("{listOfStrings} remove their MXIDs in the own TIPractitioner FHIR resource")
  @Dann("{listOfStrings} löscht seine MXID im Verzeichnis Dienst")
  @Dann("{listOfStrings} löschen ihre MXIDs im Verzeichnis Dienst")
  public void deleteFromFhir(List<String> actorNames) {
    actorNames.forEach(
        actorName -> {
          Actor actor = theActorCalled(actorName);
          actor.attemptsTo(deleteMxidFromFhir());
          checkResponseCode(actorName, NO_CONTENT.value());
          assertThat(((Response) actor.recall(LAST_RESPONSE)).getStatusCode())
              .isEqualTo(NO_CONTENT.value());
        });
  }

  @Wenn("{string} search MXID of user {string} in vzd")
  @Und("{string} sucht die MXID des Benutzers {string} im Verzeichnis Dienst")
  public void searchUserInFhir(String actorName, String userName) {
    Actor actor1 = theActorCalled(actorName);
    Actor actor2 = theActorCalled(userName);
    FhirSearchResultDTO result =
        actor1.asksFor(practitionerInFhirDirectory().withMxId(actor2.recall(MX_ID)));
    checkResponseCode(actorName, OK.value());
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).hasSize(1);
  }

  @Then("{string} gets own MXID")
  @Dann("{string} findet seine MXID im Verzeichnis Dienst")
  public void findUserInFhir(String actorName) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result = actor.asksFor(ownFhirResource().withAtLeastAmountEndpoints(1));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertCorrectEndpointNameAndMxid(endpoints, actor);
  }

  @Then("{string} is not authorized on Vzd")
  @Dann("{string} ist nicht berechtigt im Verzeichnis Dienst")
  @Und("{string} versucht seine MXID im Verzeichnis Dienst ohne Authentication zu hinterlegen")
  public void tryAddToFhir(String actorName) {
    theActorCalled(actorName).attemptsTo(setMxid());
    checkResponseCode(actorName, UNAUTHORIZED.value());
  }

  @When(
      "{string} sets the endpointVisibility of their practitioner endpoint extension to hide-versicherte")
  @Wenn(
      "{string} setzt die endpointVisibility für seine Practitioner Endpunkt Extension auf hide-versicherte")
  public void setEndpointVisibilityForPractitioner(String actorName) {
    theActorCalled(actorName).attemptsTo(setEndpointVisibility());
    checkResponseCode(actorName, CREATED.value());
  }

  @When(
      "{string} deletes the endpointVisibility hide-versicherte of their practitioner endpoint extension")
  @Wenn(
      "{string} löscht die endpointVisibility hide-versicherte für seine Practitioner Endpunkt Extension")
  public void deleteEndpointVisibilityForPractitioner(String actorName) {
    theActorCalled(actorName).attemptsTo(deleteEndpointVisibility());
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @And(
      "{string} sees their own endpointVisibility of the practitioner endpoint extension is hide-versicherte")
  @Und(
      "{string} sieht die eigene endpointVisibility der Practitioner Endpunkt Extension ist auf dem Wert hide-versicherte")
  public void getEndpointVisibilityForPractitioner(String practitionerName) {
    Actor practitioner = theActorCalled(practitionerName);
    FhirSearchResultDTO result =
        practitioner.asksFor(ownFhirResource().withAtLeastAmountEndpoints(1));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertCorrectEndpointNameAndNotVisible(endpoints, practitioner);
  }

  @And(
      "{string} sees their own endpointVisibility of the practitioner endpoint extension is NOT hide-versicherte [Retry {long} - {long}]")
  @Und(
      "{string} sieht die eigene endpointVisibility der Practitioner Endpunkt Extension NICHT mehr auf dem Wert hide-versicherte [Retry {long} - {long}]")
  public void doNotGetEndpointVisibilityForPractitioner(
      String practitionerName, Long customTimeout, Long customPollInterval) {
    Actor practitioner = theActorCalled(practitionerName);
    practitioner.asksFor(
        practitionerVisibleInFhirDirectory().withCustomInterval(customTimeout, customPollInterval));
  }

  private void assertCorrectEndpointNameAndNotVisible(
      List<FhirEndpointDTO> endpoints, Actor searchedActor) {
    String endpointName = getEndpointFromInternalName(searchedActor.recall(DISPLAY_NAME)).getName();
    endpoints =
        endpoints.stream().filter(endpoint -> endpoint.getName().equals(endpointName)).toList();
    assertThat(endpoints)
        .as(format("Could not find endpoint with name %s", endpointName))
        .isNotEmpty();

    List<FhirExtensionDTO> extensions =
        endpoints.stream()
            .map(FhirEndpointDTO::getExtension)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .toList();
    assertThat(extensions)
        .as(format("Could not find any extension for endpoint %s", endpointName))
        .isNotEmpty();

    List<FhirCodingDTO> valueCodes =
        extensions.stream().map(FhirExtensionDTO::getValueCoding).toList();
    assertThat(valueCodes)
        .as(format("Could not find any valueCodes for endpoint %s", endpointName))
        .isNotEmpty();

    List<String> codes = valueCodes.stream().map(FhirCodingDTO::getCode).toList();
    assertThat(codes)
        .as("No allowed code was found for endpoint %s", endpointName)
        .contains("hide-versicherte");
  }
}
