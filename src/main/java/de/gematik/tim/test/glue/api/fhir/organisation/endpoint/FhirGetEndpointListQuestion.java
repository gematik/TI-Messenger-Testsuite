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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ENDPOINTS;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirEndpointsDTO;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetEndpointListQuestion extends HealthcareSpecificTask implements
    Question<List<FhirEndpointDTO>> {

  private String endpointNameFilter;

  public static FhirGetEndpointListQuestion getEndpointList() {
    return new FhirGetEndpointListQuestion();
  }

  public FhirGetEndpointListQuestion filterForName(String endpointName) {
    this.endpointNameFilter = endpointName;
    return this;
  }

  @Override
  public List<FhirEndpointDTO> answeredBy(Actor actor) {
    super.performAs(actor);
    Optional<String> endpointId = Optional.ofNullable(endpointNameFilter)
        .map(name -> actor.abilityTo(UseEndpointAbility.class).getTarget(name).endpointId());

    actor.attemptsTo(GET_ENDPOINTS.request());

    List<FhirEndpointDTO> fhirEndpointDTOs = requireNonNull(
        lastResponse().body().as(FhirEndpointsDTO.class).getEndpoints());
    return fhirEndpointDTOs.stream()
        .filter(ep -> endpointId
            .map(id -> id.equals(ep.getEndpointId()))
            .orElse(true))
        .toList();
  }

}
