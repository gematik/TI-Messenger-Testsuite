/*
 * Copyright (c) 2022 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirAuthenticateTask.authenticateOnFhirVzd;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirDeleteOwnMxidTask.deleteMxidFromFhir;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSearchQuestion.practitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSetMxidTask.setMxid;
import static de.gematik.tim.test.glue.api.fhir.practitioner.OwnFhirResourceQuestion.ownFhirResource;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import de.gematik.tim.test.models.FhirPractitionerDTO;
import de.gematik.tim.test.models.FhirPractitionerSearchResultDTO;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;


public class FhirAdministrationGlue {

  @Before
  public void setup() {
    setTheStage(Cast.ofStandardActors());
  }

  @After
  public void teardown() {
    stage().drawTheCurtain();
  }

  // Register own MXID in vzd
  @When("{listOfStrings} sets MXID in own TIPractitioner resource")
  @Wenn("{listOfStrings} hinterlegt seine MXID im Verzeichnis Dienst")
  @Wenn("{listOfStrings} hinterlegen ihre MXIDs im Verzeichnis Dienst")
  public void addToFhir(List<String> actorNames) {
    actorNames.forEach(a -> {
      Actor actor = theActorCalled(a);
      actor.attemptsTo(authenticateOnFhirVzd());
      actor.attemptsTo(setMxid());
    });
  }

  @Und("{string} versucht seine MXID im Verzeichnis Dienst ohne Authencitation zu hinterlegen")
  public void tryAddToFhir(String actorName) {
    theActorCalled(actorName).attemptsTo(setMxid());
    theActorCalled(actorName).should(seeThatResponse(res ->
        res.statusCode(401)));
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
      assertThat(((Response) actor.recall(LAST_RESPONSE)).getStatusCode()).isEqualTo(
          NO_CONTENT.value());
    });
  }

  // Search and find MXID
  @Wenn("{string} search MXID of user {string} in vzd")
  @Und("{string} sucht die MXID des Benutzers {string} im Verzeichnis Dienst")
  public void searchUserInFhir(String clientName, String userName) {
    Actor actor1 = theActorCalled(clientName);
    Actor actor2 = theActorCalled(userName);
    FhirPractitionerSearchResultDTO searchResult = actor1.asksFor(
        practitionerInFhirDirectory().withMxid(actor2.recall(MX_ID)));
    assertThat(searchResult).isNotNull();
    assertThat(searchResult.getTotalSearchResults()).isEqualTo(1);
  }

  @Then("{string} gets own MXID")
  @Dann("{string} findet seine MXID im Verzeichnis Dienst")
  public void findUserInFhir(String actorName) {
    Actor actor = theActorCalled(actorName);
    FhirPractitionerDTO practitioner = actor.asksFor(ownFhirResource()).orElseThrow();
    assertThat(practitioner.getMxid()).isEqualTo(actor.recall(MX_ID));
  }

  // Assertions
  @Then("{string} has no MXID in Fhir")
  @Dann("{string} hat keine MXID im Fhir")
  public void getsOwnNullMXID(String actorName) {
    Optional<FhirPractitionerDTO> practitioner = theActorCalled(actorName).asksFor(
        ownFhirResource());
    assertThat(practitioner).isEmpty();
  }

  @Then("{string} is not authorized on Vzd")
  @Dann("{string} ist nicht berechtigt im Verzeichnis Dienst")
  public void noAccessToFhir(String clientName) {
    Actor actor = theActorCalled(clientName);
    actor.attemptsTo(setMxid());
    Response resp = lastResponse();
    assertThat(resp.statusCode()).isEqualTo(UNAUTHORIZED.value());
  }

}
