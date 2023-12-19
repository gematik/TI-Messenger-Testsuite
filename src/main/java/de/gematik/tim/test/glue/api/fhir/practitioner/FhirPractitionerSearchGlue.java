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

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

import java.util.List;
import java.util.NoSuchElementException;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ENDPOINT_ID;
import static de.gematik.tim.test.glue.api.account.AccountQuestion.ownAccountInfos;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSearchQuestion.practitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertCorrectEndpointNameAndMxid;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class FhirPractitionerSearchGlue {

  @When("{string} can not find {string}")
  @Wenn("{string} kann {string} nicht finden in FHIR")
  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Practitioner-Verzeichnis im VZD NICHT")
  public static void dontFindPractitionerInFhir(String actorName, String userName) {
    dontFindPractitionerInFhir(actorName, userName, null, null);
  }

  @Wenn("{string} kann {string} nicht finden in FHIR [Retry {long} - {long}]")
  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Practitioner-Verzeichnis im VZD NICHT [Retry {long} - {long}]")
  public static void dontFindPractitionerInFhir(String actorName, String userName, Long timeout,
      Long pollInterval) {
    dontFindPractitionerInFhir(actorName, userName, timeout, pollInterval, false);
  }

  public static void dontFindPractitionerInFhir(String actorName, String userName, Long timeout,
      Long pollInterval, boolean shouldWaitTillDeleted) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    Serenity.recordReportData();
    FhirSearchResultDTO result = actor.asksFor(practitionerInFhirDirectory()
        .withMxid(user.recall(MX_ID))
        .withCustomInterval(timeout, pollInterval)
        .shouldWaitTillDeleted(shouldWaitTillDeleted));
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).isEmpty();
  }

  //<editor-fold, desc="MXID" search>
  @When("{string} can find {listOfStrings} in directory")
  @Wenn("{string} findet {listOfStrings} in FHIR")
  public void findUser(String actorName, List<String> actorNames) {
    Actor actor1 = theActorCalled(actorName);
    actorNames.forEach(a -> {
      Actor actorToFind = theActorCalled(a);
      FhirSearchResultDTO result = actor1.asksFor(
          practitionerInFhirDirectory().withMxid(actorToFind.recall(MX_ID)));
      List<FhirEndpointDTO> endpoint = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
      assertThat(endpoint).hasSize(1);
      assertCorrectEndpointNameAndMxid(endpoint, actorToFind);
    });
  }

  @Then("{string} can not find {string} in FHIR anymore")
  @Dann("{string} kann {string} nicht mehr finden in FHIR")
  public void cantFindPractitionerInFhirAnymore(String actorName, String userName) {
    cantFindPractitionerInFhirAnymore(actorName, userName, null, null);
  }

  @Then("{string} can not find {string} in FHIR anymore [Retry {long} - {long}]")
  @Dann("{string} kann {string} nicht mehr finden in FHIR [Retry {long} - {long}]")
  public void cantFindPractitionerInFhirAnymore(String actorName, String userName, Long timeout,
      Long pollInterval) {
    dontFindPractitionerInFhir(actorName, userName, timeout, pollInterval, true);
  }
  //</editor-fold>

  // Parameter search
  @When("{string} can find {string} by searching by name with cut {int}-{int} (amount front-back) char(s)")
  @Wenn("{string} findet TI-Messenger-Nutzer {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten")
  public void findUserWithSearchParam(String actorName, String userName, int begin, int end) {
    Actor actor = theActorCalled(actorName);
    Actor searchedActor = theActorCalled(userName);
    String displayName = requireNonNull(searchedActor.asksFor(ownAccountInfos()).getDisplayName());

    assertThat(begin + end).as("You cut to much of the displayname: " + displayName)
        .isLessThan(displayName.length());
    String cutName = displayName.substring(begin, displayName.length() - end);

    FhirSearchResultDTO result = actor.asksFor(
        practitionerInFhirDirectory().withName(cutName));
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    String searchedEndpointId = searchedActor.recall(OWN_ENDPOINT_ID);
    FhirEndpointDTO endpoint = endpoints.stream()
        .filter(e -> requireNonNull(e.getId()).equals(searchedEndpointId))
        .findFirst().orElseThrow(() -> new NoSuchElementException(
            "Delivered search set does not provide the search endpoint with id -> " + searchedEndpointId));
    assertCorrectEndpointNameAndMxid(List.of(endpoint), searchedActor);
  }

  @When("{string} can NOT find {string} by searching by name {string}")
  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche mit Namen {string} NICHT")
  public void dontFindUserWithName(String actorName, String userName, String value) {
    Actor actor = theActorCalled(actorName);
    String actor2Id = theActorCalled(userName).recall(MX_ID);
    FhirSearchResultDTO result = actor.asksFor(practitionerInFhirDirectory().withName(value));

    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).extracting("address").doesNotContain(actor2Id);
  }

  // Conditions
  @Then("practitioner information is returned")
  public void practitionerInformationIsReturned() {
    theActorInTheSpotlight().should(seeThatResponse("Search practitioner",
        res -> res.statusCode(200)
            .body("searchResults", not(empty()))));
  }
}
