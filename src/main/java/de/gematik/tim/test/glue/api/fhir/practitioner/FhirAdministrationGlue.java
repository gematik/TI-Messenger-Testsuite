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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirAuthenticateTask.authenticateOnFhirVzd;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirDeleteOwnMxidTask.deleteMxidFromFhir;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSearchQuestion.practitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSetMxidTask.setMxid;
import static de.gematik.tim.test.glue.api.fhir.practitioner.OwnFhirResourceQuestion.ownFhirResource;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.List;
import net.serenitybdd.screenplay.Actor;


public class FhirAdministrationGlue {

  // Register own MXID in vzd
  @When("{listOfStrings} sets MXID in own TIPractitioner resource")
  @Wenn("{listOfStrings} hinterlegt seine MXID im Verzeichnis Dienst")
  @Wenn("{listOfStrings} hinterlegen ihre MXIDs im Verzeichnis Dienst")
  public void addToFhir(List<String> actorNames) {
    actorNames.forEach(a -> {
      Actor actor = theActorCalled(a);
      actor.attemptsTo(authenticateOnFhirVzd());
      actor.should(seeThatResponse("check status code", res -> res.statusCode(OK.value())));
      actor.attemptsTo(setMxid());
      checkResponseCode(a, CREATED.value());
    });
  }

  // Delete own MXID
  @Then("{listOfStrings} removes the MXID in the own TIPractitioner FHIR resource")
  @Then("{listOfStrings} remove their MXIDs in the own TIPractitioner FHIR resource")
  @Dann("{listOfStrings} löscht seine MXID im Verzeichnis Dienst")
  @Dann("{listOfStrings} löschen ihre MXIDs im Verzeichnis Dienst")
  public void deleteFromFhir(List<String> actorNames) {
    actorNames.forEach(a -> {
      Actor actor = theActorCalled(a);
      actor.attemptsTo(deleteMxidFromFhir());
      checkResponseCode(a, NO_CONTENT.value());
      assertThat(((Response) actor.recall(LAST_RESPONSE)).getStatusCode()).isEqualTo(
          NO_CONTENT.value());
    });
  }

  // Search and find MXID
  @Wenn("{string} search MXID of user {string} in vzd")
  @Und("{string} sucht die MXID des Benutzers {string} im Verzeichnis Dienst")
  public void searchUserInFhir(String actorName, String userName) {
    Actor actor1 = theActorCalled(actorName);
    Actor actor2 = theActorCalled(userName);
    FhirSearchResultDTO result = actor1.asksFor(
        practitionerInFhirDirectory().withMxid(actor2.recall(MX_ID)));
    checkResponseCode(actorName, OK.value());
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).hasSize(1);
  }

  @Then("{string} gets own MXID")
  @Dann("{string} findet seine MXID im Verzeichnis Dienst")
  public void findUserInFhir(String actorName) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result = actor.asksFor(ownFhirResource().withAtLeastAmountEndpoints(1));
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);

    assertThat(endpoints).extracting("address").contains((String) actor.recall(MX_ID));
  }

  // Assertions
  @Then("{string} has no MXID in Fhir")
  @Dann("{string} hat keine MXID im Fhir")
  public void getsOwnNullMXID(String actorName) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result = theActorCalled(actorName).asksFor(ownFhirResource());
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).extracting("address").doesNotContain(actor.recall(MX_ID));
  }

  @Then("{string} is not authorized on Vzd")
  @Dann("{string} ist nicht berechtigt im Verzeichnis Dienst")
  @Und("{string} versucht seine MXID im Verzeichnis Dienst ohne Authentication zu hinterlegen")
  public void tryAddToFhir(String actorName) {
    theActorCalled(actorName).attemptsTo(setMxid());
    checkResponseCode(actorName, UNAUTHORIZED.value());
  }

}
