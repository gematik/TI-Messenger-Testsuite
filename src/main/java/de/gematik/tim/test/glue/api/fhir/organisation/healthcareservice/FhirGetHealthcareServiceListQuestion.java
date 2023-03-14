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

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_HEALTHCARE_SERVICES;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirHealthcareServicesDTO;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetHealthcareServiceListQuestion implements
    Question<List<FhirHealthcareServiceDTO>> {

  private String hsNameFilter;

  public static FhirGetHealthcareServiceListQuestion getHealthcareServiceList() {
    return new FhirGetHealthcareServiceListQuestion();
  }

  public FhirGetHealthcareServiceListQuestion filterForHealthcareService(String hsName) {
    this.hsNameFilter = hsName;
    return this;
  }

  @Override
  public List<FhirHealthcareServiceDTO> answeredBy(Actor actor) {
    Optional<String> hsId = Optional.ofNullable(hsNameFilter)
        .map(filter -> actor.abilityTo(UseHealthcareServiceAbility.class).getTarget(filter));

    actor.attemptsTo(GET_HEALTHCARE_SERVICES.request());

    List<FhirHealthcareServiceDTO> fhirHealthcareServiceDTOs = requireNonNull(
        lastResponse().body().as(FhirHealthcareServicesDTO.class).getHealthcareServices());

    return fhirHealthcareServiceDTOs.stream()
        .filter(hs -> hsId
            .map(id -> id.equals(hs.getHealthcareServiceId()))
            .orElse(true))
        .toList();
  }

}
