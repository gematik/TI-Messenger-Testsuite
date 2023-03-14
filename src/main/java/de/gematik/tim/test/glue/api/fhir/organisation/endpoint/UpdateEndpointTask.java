/*
 * Copyright 20023 gematik GmbH
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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UPDATE_ENDPOINT;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;

import de.gematik.tim.test.models.FhirEndpointDTO;
import net.serenitybdd.screenplay.Actor;

public class UpdateEndpointTask extends EndpointSpecificTask {

  private final FhirEndpointDTO endpoint;

  private UpdateEndpointTask(FhirEndpointDTO endpoint) {
    this.endpoint = endpoint;
  }

  public static UpdateEndpointTask updateEndpoint(FhirEndpointDTO endpointDTO) {
    return new UpdateEndpointTask(endpointDTO);
  }

  public static UpdateEndpointTask updateEndpointFromFile(String jsonFileName) {
    return new UpdateEndpointTask(readJsonFile(jsonFileName, FhirEndpointDTO.class));
  }

  public UpdateEndpointTask withAddress(String address) {
    this.endpoint.setAddress(address);
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(UPDATE_ENDPOINT.request().with(req -> req.body(endpoint)));
  }
}
