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
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForEmptyResult;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirResourceTypeDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.List;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class NoFhirSearchQuestion implements Question<Boolean> {

  private String mxId;
  private Long customTimeout;
  private Long customPollInterval;

  public static NoFhirSearchQuestion noPractitionerInFhirDirectory() {
    return new NoFhirSearchQuestion();
  }

  public NoFhirSearchQuestion withMxId(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public NoFhirSearchQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    try {
      repeatedRequestForEmptyResult(
          () -> requestFhirPractitioner(actor), customTimeout, customPollInterval);
    } catch (ConditionTimeoutException e) {
      log.error("Practitioner with mxid {} should not be found", mxId, e);
      throw new AssertionFailed(format("Practitioner with mxid %s should not be found", mxId));
    }
    return true;
  }

  private Boolean requestFhirPractitioner(Actor actor) {
    actor.attemptsTo(
        SEARCH_PRACTITIONER.request().with(request -> request.queryParam("mxid", mxidToUri(mxId))));
    actor.remember(LAST_RESPONSE, lastResponse());
    FhirSearchResultDTO response = parseResponse(FhirSearchResultDTO.class);
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(response, FhirResourceTypeDTO.ENDPOINT, FhirEndpointDTO.class);
    endpoints = filterByMxIdForEndpoint(endpoints);
    return endpoints.isEmpty();
  }

  private List<FhirEndpointDTO> filterByMxIdForEndpoint(List<FhirEndpointDTO> endpoints) {
    endpoints =
        endpoints.stream()
            .filter(
                endpoint ->
                    endpoint.getAddress() != null && endpoint.getAddress().equals(mxidToUri(mxId)))
            .toList();
    return endpoints;
  }
}
