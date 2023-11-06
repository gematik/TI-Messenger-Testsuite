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
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceQuestion.getHealthcareService;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getEndpointIdsOrLocationIdsOfHealthcareService;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetEndpointListQuestion extends HealthcareSpecificTask implements
    Question<List<FhirEndpointDTO>> {

  private String endpointName;
  private String hsId;

  public static FhirGetEndpointListQuestion getEndpointList() {
    return new FhirGetEndpointListQuestion();
  }

  public FhirGetEndpointListQuestion withHsId(String hsId) {
    this.hsId = hsId;
    return this;
  }

  public FhirGetEndpointListQuestion filterForName(String endpointName) {
    this.endpointName = endpointName;
    return this;
  }

  @Override
  public List<FhirEndpointDTO> answeredBy(Actor actor) {
    super.performAs(actor);
    actor.attemptsTo(GET_ENDPOINTS.request());

    FhirSearchResultDTO res = parseResponse(FhirSearchResultDTO.class, true);
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(res, ENDPOINT, FhirEndpointDTO.class);
    if (nonNull(endpointName)) {
      endpoints = endpoints.stream().filter(e -> e.getName().equals(endpointName)).toList();
    }
    if (nonNull(hsId)) {
      FhirHealthcareServiceDTO hs = getResourcesFromSearchResult(res, ENDPOINT, FhirEndpointDTO.class).stream()
          .filter(e -> requireNonNull(e.getId()).equals(hsId)).map(FhirHealthcareServiceDTO.class::cast).findAny()
          .orElseGet(() -> actor.asksFor(getHealthcareService()));
      List<String> ids = getEndpointIdsOrLocationIdsOfHealthcareService(hs, ENDPOINT);
      endpoints = endpoints.stream().filter(e -> ids.contains(e.getId())).toList();
    }

    return endpoints;
  }


}
