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

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.NoFhirEndpointSearchQuestion.noEndpoint;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.mxidToUri;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;

@AllArgsConstructor(access = PRIVATE)
public class FhirOrganizationSearchGlue {

  @Then(
      "{string} does NOT find TI-Messenger user {string} in the Organisations-Verzeichnis of the VZD [Retry {long} - {long}]")
  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche im Organisations-Verzeichnis im VZD NICHT [Retry {long} - {long}]")
  public static void dontFindUserWithNameInOrgVzd(
      String actorName, String userName, Long customTimeout, Long customPollInterval) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    dontFindUserWithNameInOrgVzd(actor, user, customTimeout, customPollInterval);
  }

  private static void dontFindUserWithNameInOrgVzd(
      Actor actor, Actor user, Long customTimeout, Long customPollInterval) {
    Serenity.recordReportData();
    String mxIdOfUserToFind = user.recall(MX_ID);
    actor.asksFor(
        noEndpoint()
            .withMxIdAsUri(mxidToUri(mxIdOfUserToFind))
            .withCustomInterval(customTimeout, customPollInterval));
  }
}
