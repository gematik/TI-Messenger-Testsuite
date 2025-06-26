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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ENDPOINTS;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceQuestion.getHealthcareService;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getEndpointIdsOrLocationIdsOfHealthcareService;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetEndpointListQuestion extends HealthcareSpecificTask
    implements Question<List<FhirEndpointDTO>> {

  private String hsId;

  public static FhirGetEndpointListQuestion getEndpointList() {
    return new FhirGetEndpointListQuestion();
  }

  public FhirGetEndpointListQuestion withHsId(String hsId) {
    this.hsId = hsId;
    return this;
  }

  @Override
  public List<FhirEndpointDTO> answeredBy(Actor actor) {
    super.performAs(actor);
    actor.attemptsTo(GET_ENDPOINTS.request());

    FhirSearchResultDTO searchResult = parseResponse(FhirSearchResultDTO.class);
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(searchResult, ENDPOINT, FhirEndpointDTO.class);
    if (nonNull(hsId)) {
      FhirHealthcareServiceDTO healthcareService =
          getResourcesFromSearchResult(searchResult, ENDPOINT, FhirEndpointDTO.class).stream()
              .filter(endpoint -> endpoint.getId().equals(hsId))
              .map(FhirHealthcareServiceDTO.class::cast)
              .findAny()
              .orElseGet(() -> actor.asksFor(getHealthcareService()));
      List<String> ids =
          getEndpointIdsOrLocationIdsOfHealthcareService(healthcareService, ENDPOINT);
      endpoints = endpoints.stream().filter(endpoint -> ids.contains(endpoint.getId())).toList();
    }
    return endpoints;
  }
}
