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

package de.gematik.tim.test.glue.api.fhir;

import static de.gematik.tim.test.glue.api.fhir.organisation.FhirOrganizationSearchGlue.dontFindUserWithNameInOrgVzd;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirPractitionerSearchGlue.dontFindPractitionerInFhir;

import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;

public class FhirSearchGlue {

  @Then("{string} does NOT find user {string} when searching VZD [Retry {long} - {long}]")
  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche im VZD NICHT [Retry {long} - {long}]")
  public static void dontFindUserWithNameInAllVzd(
      String actorName, String userName, Long customTimeout, Long customPollInterval) {
    dontFindUserWithNameInOrgVzd(actorName, userName, customTimeout, customPollInterval);
    dontFindPractitionerInFhir(actorName, userName, customTimeout, customPollInterval);
  }
}
