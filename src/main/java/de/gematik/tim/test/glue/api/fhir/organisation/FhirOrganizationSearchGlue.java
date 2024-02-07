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

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirEndpointSearchQuestion.endpoint;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

import java.util.List;

@AllArgsConstructor(access = PRIVATE)
public class FhirOrganizationSearchGlue {

  @Then("{string} does NOT find TI-Messenger user {string} in the Organisations-Verzeichnis of the VZD")
  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Organisations-Verzeichnis im VZD NICHT")
  public static void dontFindUserWithNameInOrgVzd(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    dontFindUserWithNameInOrgVzd(actor, user, null, null);
  }

  @Then("{string} does NOT find TI-Messenger user {string} in the Organisations-Verzeichnis of the VZD [Retry {long} - {long}]")
  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Organisations-Verzeichnis im VZD NICHT [Retry {long} - {long}]")
  public static void dontFindUserWithNameInOrgVzd(String actorName, String userName,
      Long customTimeout, Long customPollInterval) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    dontFindUserWithNameInOrgVzd(actor, user, customTimeout, customPollInterval);
  }

  private static void dontFindUserWithNameInOrgVzd(Actor actor, Actor user,
      Long customTimeout, Long customPollInterval) {
    Serenity.recordReportData();

    String mxIdOfUserToFind = user.recall(MX_ID);
    FhirSearchResultDTO result = actor.asksFor(endpoint()
        .withMxId(mxIdOfUserToFind)
        .withCustomInterval(customTimeout, customPollInterval));
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertThat(endpoints).isEmpty();
  }
}
