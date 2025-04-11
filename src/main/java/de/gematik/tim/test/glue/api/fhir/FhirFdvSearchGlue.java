/*
 * Copyright 2025 gematik GmbH
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
 */

package de.gematik.tim.test.glue.api.fhir;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirOrgAdminGlue.findHealthcareServiceWithSearchParam;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirOrgAdminGlue.findsAddressInHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirSearchOrgQuestion.organizationEndpoints;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirPractitionerSearchGlue.dontFindPractitionerInFhir;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirPractitionerSearchGlue.findUser;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirPractitionerSearchGlue.findUserWithSearchParam;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import java.util.List;
import net.serenitybdd.screenplay.Actor;

public class FhirFdvSearchGlue {

  @Then("{string} finds {listOfStrings} in FHIR via the FDV endpoint")
  @Dann("{string} findet {listOfStrings} in FHIR über die FDV-Schnittstelle")
  public void findPractitionerFromEpaClient(String actorName, List<String> searchedNames) {
    findUser(actorName, searchedNames);
  }

  @Then(
      "{string} finds TI-Messenger user {string} while searching by name minus {int}-{int} \\(number of chars front-back)\\ cut via the FDV endpoint")
  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten über die FDV-Schnittstelle")
  public void findPractitionerWithSearchParamFromEpaClient(
      String actorName, String searchedActorName, int begin, int end) {
    findUserWithSearchParam(actorName, searchedActorName, begin, end);
  }

  @Then(
      "{string} CANNOT find {string} by search in FHIR via the FDV endpoint NOT [Retry {long} - {long}]")
  @Dann(
      "{string} findet {string} bei Suche in FHIR über die FDV-Schnittstelle NICHT [Retry {long} - {long}]")
  public void dontFindPractitionerEndpointFromEpaClient(
      String actorName, String searchedActorName, Long customTimeout, Long customPollInterval) {
    dontFindPractitionerInFhir(actorName, searchedActorName, customTimeout, customPollInterval);
  }

  @Then("{string} finds {string} in healthcare service {string} via the FDV endpoint")
  @Dann("{string} findet {string} im Healthcare-Service {string} über die FDV-Schnittstelle")
  public void findHealthcareServiceFromEpaClient(
      String actorName, String searchedName, String healthcareServiceName) {
    findsAddressInHealthcareService(actorName, searchedName, healthcareServiceName);
  }

  @Then(
      "{string} CANNOT find {string} in the healthcare service {string} via the FDV endpoint [Retry {long} - {long}]")
  @Dann(
      "{string} findet {string} im Healthcare-Service {string} über die FDV-Schnittstelle NICHT [Retry {long} - {long}]")
  public void dontFindHealthcareServiceEndpointFromEpaClient(
      String actorName,
      String searchedActorName,
      String healthcareServiceName,
      Long customTimeout,
      Long customPollInterval) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints()
                .withHealthcareServiceName(healthcareServiceName)
                .havingAtLeastXEndpoints(0)
                .withCustomInterval(customTimeout, customPollInterval));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    List<FhirEndpointDTO> filtered =
        endpoints.stream()
            .filter(
                endpoint ->
                    endpoint.getAddress().equals(theActorCalled(searchedActorName).recall(MX_ID)))
            .toList();
    assertThat(filtered).isEmpty();
  }

  @Then(
      "{string} finds healthcare service {string} while searching by name minus {int}-{int} \\(number of chars front-back)\\ cut via the FDV endpoint")
  @Dann(
      "{string} findet Healthcare-Service {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten über die FDV-Schnittstelle")
  public void findHealthcareServiceWithSearchParamFromEpaClient(
      String actorName, String healthcareServiceName, int begin, int end) {
    findHealthcareServiceWithSearchParam(actorName, healthcareServiceName, begin, end);
  }
}
