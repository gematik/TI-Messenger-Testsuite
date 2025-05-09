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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_ENDPOINT_VISIBILITY_PRACTITIONER;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class FhirDeleteEndpointVisibilityTask implements Task {

  public static FhirDeleteEndpointVisibilityTask deleteEndpointVisibility() {
    return new FhirDeleteEndpointVisibilityTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(DELETE_ENDPOINT_VISIBILITY_PRACTITIONER.request());
  }
}
