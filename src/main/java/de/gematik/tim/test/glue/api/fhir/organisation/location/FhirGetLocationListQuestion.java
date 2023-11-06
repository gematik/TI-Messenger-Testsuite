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

package de.gematik.tim.test.glue.api.fhir.organisation.location;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_LOCATIONS;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.LOCATION;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirLocationDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetLocationListQuestion extends HealthcareSpecificTask implements
    Question<List<FhirLocationDTO>> {

  private String locationName;

  public static FhirGetLocationListQuestion getLocationList() {
    return new FhirGetLocationListQuestion();
  }

  public FhirGetLocationListQuestion filterForName(String locationName) {
    this.locationName = locationName;
    return this;
  }

  @Override
  public List<FhirLocationDTO> answeredBy(Actor actor) {
    super.performAs(actor);
    Optional<String> locationId = Optional.ofNullable(locationName)
        .map(name -> actor.abilityTo(UseLocationAbility.class).getTarget(name).locationId());

    actor.attemptsTo(GET_LOCATIONS.request());

    FhirSearchResultDTO result = requireNonNull(parseResponse(FhirSearchResultDTO.class, true));

    List<FhirLocationDTO> fhirLocationDTOs = getResourcesFromSearchResult(result, LOCATION, FhirLocationDTO.class);

    return fhirLocationDTOs.stream()
        .filter(location -> locationId
            .map(id -> id.equals(location.getId()))
            .orElse(true))
        .toList();
  }


}
