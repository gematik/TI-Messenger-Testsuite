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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ENDPOINT_ID;
import static de.gematik.tim.test.glue.api.account.AccountQuestion.ownAccountInfos;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirSearchQuestion.practitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.practitioner.NoFhirSearchQuestion.noPractitionerInFhirDirectory;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertCorrectEndpointNameAndMxid;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.NoSuchElementException;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

public class FhirPractitionerSearchGlue {

  @When("{string} can not find {string} in FHIR [Retry {long} - {long}]")
  @Wenn("{string} kann {string} nicht finden in FHIR [Retry {long} - {long}]")
  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche im Practitioner-Verzeichnis im VZD NICHT [Retry {long} - {long}]")
  public static void dontFindPractitionerInFhir(
      String actorName, String userName, Long timeout, Long pollInterval) {
    dontFindPractitioner(actorName, userName, timeout, pollInterval);
  }

  private static void dontFindPractitioner(
      String actorName, String userName, Long timeout, Long pollInterval) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    Serenity.recordReportData();
    actor.asksFor(
        noPractitionerInFhirDirectory()
            .withMxId(user.recall(MX_ID))
            .withCustomInterval(timeout, pollInterval));
  }

  @Then("{string} can not find {string} in FHIR anymore [Retry {long} - {long}]")
  @Dann("{string} kann {string} nicht mehr finden in FHIR [Retry {long} - {long}]")
  public void cantFindPractitionerInFhirAnymore(
      String actorName, String userName, Long timeout, Long pollInterval) {
    dontFindPractitioner(actorName, userName, timeout, pollInterval);
  }

  @When("{string} can find {listOfStrings} in FHIR")
  @Wenn("{string} findet {listOfStrings} in FHIR")
  public static void findUser(String searchingActorName, List<String> searchedActorNames) {
    Actor actor = theActorCalled(searchingActorName);
    searchedActorNames.forEach(
        searchedActorName -> {
          Actor searchedActor = theActorCalled(searchedActorName);
          FhirSearchResultDTO result =
              actor.asksFor(practitionerInFhirDirectory().withMxId(searchedActor.recall(MX_ID)));
          List<FhirEndpointDTO> endpoints =
              getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
          assertThat(endpoints).hasSize(1);
          assertCorrectEndpointNameAndMxid(endpoints, searchedActor);
        });
  }

  @When(
      "{string} can find {string} by searching by name with cut {int}-{int} \\(amount front-back) char\\(s)")
  @Wenn(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten")
  public static void findUserWithSearchParam(
      String actorName, String searchedActorName, int begin, int end) {
    Actor actor = theActorCalled(actorName);
    Actor searchedActor = theActorCalled(searchedActorName);
    String displayName = requireNonNull(searchedActor.asksFor(ownAccountInfos()).getDisplayName());

    assertThat(begin + end)
        .as("You cut to much of the displayname: " + displayName)
        .isLessThan(displayName.length());
    String cutName = displayName.substring(begin, displayName.length() - end);

    FhirSearchResultDTO result = actor.asksFor(practitionerInFhirDirectory().withName(cutName));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    String searchedEndpointId = searchedActor.recall(OWN_ENDPOINT_ID);
    FhirEndpointDTO searchedEndpoint =
        endpoints.stream()
            .filter(endpoint -> endpoint.getId().equals(searchedEndpointId))
            .findFirst()
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Delivered search set does not provide the search endpoint with id -> "
                            + searchedEndpointId));
    assertCorrectEndpointNameAndMxid(List.of(searchedEndpoint), searchedActor);
  }
}
