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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ENDPOINT;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.repeatedRequest;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.restassured.specification.RequestSpecification;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FhirEndpointSearchQuestion implements Question<FhirSearchResultDTO> {

  private Long customTimeout;
  private Long customPollInterval;
  private String mxId;
  private String endpointName;
  private int atLeastExpectedResults = 1;

  public static FhirEndpointSearchQuestion endpoint() {
    return new FhirEndpointSearchQuestion();
  }

  public FhirEndpointSearchQuestion withMxId(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public FhirEndpointSearchQuestion withName(String name) {
    this.endpointName = name;
    return this;
  }

  public FhirEndpointSearchQuestion withAtLeastResults(int amount) {
    this.atLeastExpectedResults = amount;
    return this;
  }

  public FhirEndpointSearchQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public FhirSearchResultDTO answeredBy(Actor actor) {
    try {
      return repeatedRequest(() -> filterEndpoint(actor), "room",
          customTimeout, customPollInterval);
    } catch (ConditionTimeoutException ex) {
      log.error(
          "Endpoint could not bee found with requested parameters. MxId: {} endpointName: {}",
          this.mxId, this.endpointName);
    }
    return lastResponse().body()
        .as(FhirSearchResultDTO.class);
  }

  @NotNull
  private Optional<FhirSearchResultDTO> filterEndpoint(Actor actor) {
    actor.attemptsTo(SEARCH_ENDPOINT.request().with(this::prepareQuery));
    FhirSearchResultDTO res = lastResponse().body()
        .as(FhirSearchResultDTO.class);
    if (requireNonNull(res.getTotal()) < atLeastExpectedResults) {
      return Optional.empty();
    }
    return Optional.of(res);
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (nonNull(this.endpointName)) {
      request.queryParam("name", this.endpointName);
    }
    if (nonNull(this.mxId)) {
      request.queryParam("mxId", this.mxId);
    }
    return request;
  }
}
