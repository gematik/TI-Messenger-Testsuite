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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_PRACTITIONER;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.mxidToUri;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static java.util.Objects.nonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirResourceTypeDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.core.ConditionTimeoutException;

import java.util.List;
import java.util.Optional;

public class FhirSearchQuestion implements Question<FhirSearchResultDTO> {

  private String mxid;
  private String name;
  private Integer atLeastExpectedResults;
  private Long customTimeout;
  private Long customPollInterval;
  private boolean shouldWaitTillDeleted;

  public static FhirSearchQuestion practitionerInFhirDirectory() {
    return new FhirSearchQuestion().withAtLeastExpectedEntries(1);
  }

  public FhirSearchQuestion withMxid(String mxid) {
    this.mxid = mxid;
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

  public FhirSearchQuestion shouldWaitTillDeleted(boolean shouldWait) {
    this.shouldWaitTillDeleted = shouldWait;
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
      return repeatedRequest(() -> requestFhirPractitioner(actor), "practitioner", customTimeout,
          customPollInterval);
    } catch (ConditionTimeoutException ex) {
      return ((Response) actor.recall(LAST_RESPONSE)).body()
          .as(FhirSearchResultDTO.class);
    }
  }

  private Optional<FhirSearchResultDTO> requestFhirPractitioner(Actor actor) {
    actor.attemptsTo(SEARCH_PRACTITIONER.request().with(this::prepareQuery));
    actor.remember(LAST_RESPONSE, lastResponse());
    if (shouldWaitTillDeleted) {
      return dontFindPractitioner();
    }
    return findPractitioner();
  }

  private Optional<FhirSearchResultDTO> findPractitioner() {
    FhirSearchResultDTO response = parseResponse(FhirSearchResultDTO.class, true);
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(response, FhirResourceTypeDTO.ENDPOINT,
        FhirEndpointDTO.class);
    endpoints = filterForEndpoint(endpoints);
    return endpoints.size() >= atLeastExpectedResults ? Optional.of(response) : Optional.empty();
  }

  private Optional<FhirSearchResultDTO> dontFindPractitioner() {
    FhirSearchResultDTO response = parseResponse(FhirSearchResultDTO.class, true);
    List<FhirEndpointDTO> endpoints = getResourcesFromSearchResult(response, FhirResourceTypeDTO.ENDPOINT,
        FhirEndpointDTO.class);
    endpoints = filterForEndpoint(endpoints);
    return endpoints.isEmpty() ? Optional.of(response) : Optional.empty();
  }

  private List<FhirEndpointDTO> filterForEndpoint(List<FhirEndpointDTO> endpoints) {
    if (nonNull(mxid)) {
      endpoints = endpoints.stream().filter(
              e -> e.getAddress() != null
                  && e.getAddress().equals(mxidToUri(mxid)))
          .toList();
    }
    if (nonNull(name)) {
      endpoints = endpoints.stream().filter(
              e -> e.getName() != null
                  && e.getName().contains(name))
          .toList();
    }
    return endpoints;
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (StringUtils.isNotBlank(mxid)) {
      request.queryParam("mxid", mxidToUri(mxid));
    }
    if (StringUtils.isNotBlank(name)) {
      request.queryParam("name", name);
    }
    return request;
  }
}
