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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_ENDPOINT_VISIBILITY;

import de.gematik.tim.test.models.FhirEndpointDTO;
import net.serenitybdd.screenplay.Actor;

public class DeleteEndpointVisibilityOfHealthcareServiceTask extends EndpointSpecificTask {

  private final FhirEndpointDTO endpoint;

  private DeleteEndpointVisibilityOfHealthcareServiceTask(FhirEndpointDTO endpoint) {
    this.endpoint = endpoint;
  }

  public static DeleteEndpointVisibilityOfHealthcareServiceTask deleteEndpointVisibility(
      FhirEndpointDTO endpointDTO) {
    return new DeleteEndpointVisibilityOfHealthcareServiceTask(endpointDTO);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(
        DELETE_ENDPOINT_VISIBILITY.request().with(req -> req.body(endpoint)));
  }
}
