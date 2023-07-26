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

package de.gematik.tim.test.glue.api;

import static lombok.AccessLevel.PRIVATE;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.Then;
import lombok.AllArgsConstructor;


@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

  @Then("existiert kein Endpoint von {string} für den Healthcare-Service {string} [Retry {long} - {long}]")
  public void noHealthcareServiceEndpointWithNameTiming(String actorName, String hsName, Long timeout, Long pollInterval) {
    throw new PendingException("Have to be implemented");
  }

  @Then("existiert der zuletzt gelöschte Healthcare-Service nicht mehr [Retry {long} - {long}]")
  public void lastDeletedHsDoesNotExistAnymoreTiming(Long timeout, Long pollInterval) {
    throw new PendingException("Have to be implemented");
  }

  @Then("{string} versucht der MXID {string} direkt {string} zu schreiben")
  public void directMessageMxid(String actor, String mxid, String message) {
    throw new PendingException("Have to be implemented");
  }

  @Then("{string} versucht die MXID {string} über den HealthcareService {string} in den Chat-Raum {string} einzuladen")
  public void inviteMxid(String actor, String mxid, String bcsName, String roomName) {
    throw new PendingException("Have to be implemented");
  }


}


