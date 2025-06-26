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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_PRACTITIONER;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.mxidToUri;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirPractitionerDTO;
import de.gematik.tim.test.models.FhirPractitionerRoleDTO;
import de.gematik.tim.test.models.FhirReferenceDTO;
import de.gematik.tim.test.models.FhirResourceTypeDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class FhirSearchQuestion implements Question<FhirSearchResultDTO> {

  private String mxId;
  private String name;
  private Integer atLeastExpectedResults;
  private Long customTimeout;
  private Long customPollInterval;

  public static FhirSearchQuestion practitionerInFhirDirectory() {
    return new FhirSearchQuestion().withAtLeastExpectedEntries(1);
  }

  public FhirSearchQuestion withMxId(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public FhirSearchQuestion withName(String name) {
    this.name = name;
    return this;
  }

  public FhirSearchQuestion withAtLeastExpectedEntries(Integer amount) {
    this.atLeastExpectedResults = amount;
    return this;
  }

  public FhirSearchQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public FhirSearchResultDTO answeredBy(Actor actor) {
    try {
      return repeatedRequest(
          () -> requestFhirPractitioner(actor), "practitioner", customTimeout, customPollInterval);
    } catch (ConditionTimeoutException e) {
      log.error("Practitioner with name {} and mxid {} could not be found", name, mxId, e);
      throw new AssertionFailed(
          format("Practitioner with name %s and mxid %s could not be found", name, mxId));
    }
  }

  private Optional<FhirSearchResultDTO> requestFhirPractitioner(Actor actor) {
    actor.attemptsTo(SEARCH_PRACTITIONER.request().with(this::prepareQuery));
    actor.remember(LAST_RESPONSE, lastResponse());
    FhirSearchResultDTO response = parseResponse(FhirSearchResultDTO.class);
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(response, FhirResourceTypeDTO.ENDPOINT, FhirEndpointDTO.class);
    if (nonNull(name)) {
      List<FhirPractitionerDTO> practitioners =
          getResourcesFromSearchResult(
              response, FhirResourceTypeDTO.PRACTITIONER, FhirPractitionerDTO.class);
      practitioners = filterByNameForEndpoint(practitioners);
      List<String> practitionerEndpointReferences =
          getEndpointsForPractitioners(response, practitioners);
      endpoints = filterByReferencesForEndpoint(practitionerEndpointReferences, endpoints);
    }
    if (nonNull(mxId)) {
      endpoints = filterByMxIdForEndpoint(endpoints);
    }
    return endpoints.size() >= atLeastExpectedResults ? Optional.of(response) : Optional.empty();
  }

  private List<FhirEndpointDTO> filterByReferencesForEndpoint(
      List<String> practitionerEndpointReferences, List<FhirEndpointDTO> endpoints) {
    List<FhirEndpointDTO> filteredEndpoints = new ArrayList<>();
    for (FhirEndpointDTO endpoint : endpoints) {
      for (String practitionerEndpointReference : practitionerEndpointReferences) {
        if (practitionerEndpointReference.contains(endpoint.getId())) {
          filteredEndpoints.add(endpoint);
        }
      }
    }
    return filteredEndpoints;
  }

  private List<String> getEndpointsForPractitioners(
      FhirSearchResultDTO response, List<FhirPractitionerDTO> practitioners) {
    List<FhirPractitionerRoleDTO> practitionerRoles =
        getResourcesFromSearchResult(
            response, FhirResourceTypeDTO.PRACTITIONERROLE, FhirPractitionerRoleDTO.class);
    List<String> endpointReferences = new ArrayList<>();
    for (FhirPractitionerDTO practitioner : practitioners) {
      for (FhirPractitionerRoleDTO practitionerRole : practitionerRoles) {
        if (practitionerRole.getPractitioner().getReference().contains(practitioner.getId())
            && (nonNull(practitionerRole.getEndpoint()))) {
          endpointReferences.addAll(
              practitionerRole.getEndpoint().stream().map(FhirReferenceDTO::getReference).toList());
        }
      }
    }
    return endpointReferences;
  }

  private List<FhirEndpointDTO> filterByMxIdForEndpoint(List<FhirEndpointDTO> endpoints) {
    endpoints =
        endpoints.stream()
            .filter(endpoint -> endpoint.getAddress().equals(mxidToUri(mxId)))
            .toList();
    return endpoints;
  }

  private List<FhirPractitionerDTO> filterByNameForEndpoint(
      List<FhirPractitionerDTO> practitioners) {
    practitioners =
        practitioners.stream()
            .filter(
                practitioner ->
                    practitioner.getName() != null
                        && practitioner.getName().get(0).getText().contains(name))
            .toList();
    return practitioners;
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (StringUtils.isNotBlank(mxId)) {
      request.queryParam("mxid", mxidToUri(mxId));
    }
    if (StringUtils.isNotBlank(name)) {
      request.queryParam("name", name);
    }
    return request;
  }
}
