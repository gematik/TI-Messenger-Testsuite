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

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ORG;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getHealthcareServiceFromInternalName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.commons.lang3.StringUtils;

public class FhirSearchOrgQuestion implements Question<FhirSearchResultDTO> {

  private String hcsName;
  private String mxIdInEndpoint;
  private int minimalSearchResults = 1;
  private Long customTimeout;
  private Long customPollInterval;

  public static FhirSearchOrgQuestion organizationEndpoints() {
    return new FhirSearchOrgQuestion();
  }

  public FhirSearchOrgQuestion withHealthcareServiceName(String hcsName) {
    this.hcsName = getHealthcareServiceFromInternalName(hcsName).name();
    return this;
  }

  public FhirSearchOrgQuestion withUniqueHsName(String hcsName) {
    this.hcsName = hcsName;
    return this;
  }

  public FhirSearchOrgQuestion havingMxIdAsUriInEndpoint(String mxId) {
    this.mxIdInEndpoint = mxId;
    return this;
  }

  public FhirSearchOrgQuestion havingAtLeastXEndpoints(int amount) {
    this.minimalSearchResults = amount;
    return this;
  }

  public FhirSearchOrgQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public FhirSearchResultDTO answeredBy(Actor actor) {
    return repeatedRequest(
        () -> searchForOrganization(actor), "organization", customTimeout, customPollInterval);
  }

  private Optional<FhirSearchResultDTO> searchForOrganization(Actor actor) {
    actor.attemptsTo(SEARCH_ORG.request().with(this::prepareQuery));

    FhirSearchResultDTO resp = parseResponse(FhirSearchResultDTO.class);
    actor.remember(LAST_RESPONSE, lastResponse());

    return checkConditions(resp);
  }

  private Optional<FhirSearchResultDTO> checkConditions(FhirSearchResultDTO searchResult) {
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(searchResult, ENDPOINT, FhirEndpointDTO.class);
    if (mxIdInEndpoint == null && endpoints.size() >= minimalSearchResults) {
      return Optional.of(searchResult);
    }

    List<String> ids = endpoints.stream().map(FhirEndpointDTO::getAddress).toList();
    if (mxIdInEndpoint != null
        && ids.contains(mxIdInEndpoint)
        && searchResult.getTotal() >= minimalSearchResults) {
      return Optional.of(searchResult);
    }
    return Optional.empty();
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (StringUtils.isNotBlank(hcsName)) {
      request.queryParam("healthcareServiceName", hcsName);
    }
    return request;
  }
}
