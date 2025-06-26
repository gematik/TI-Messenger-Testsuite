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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ENDPOINT;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForEmptyResult;
import static java.lang.String.format;

import de.gematik.tim.test.models.FhirSearchResultDTO;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class NoFhirEndpointSearchQuestion implements Question<Boolean> {

  private Long customTimeout;
  private Long customPollInterval;
  private String mxId;

  public static NoFhirEndpointSearchQuestion noEndpoint() {
    return new NoFhirEndpointSearchQuestion();
  }

  public NoFhirEndpointSearchQuestion withMxIdAsUri(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public NoFhirEndpointSearchQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    try {
      repeatedRequestForEmptyResult(() -> filterEndpoint(actor), customTimeout, customPollInterval);
    } catch (ConditionTimeoutException e) {
      log.error("Endpoint was unexpectedly found with mxId: {} ", mxId, e);
      throw new AssertionFailed(format("Endpoint for mxId %s should not be found", mxId));
    }
    return true;
  }

  private Boolean filterEndpoint(Actor actor) {
    actor.attemptsTo(
        SEARCH_ENDPOINT.request().with(request -> request.queryParam("mxId", this.mxId)));
    FhirSearchResultDTO res = parseResponse(FhirSearchResultDTO.class);
    return res.getTotal() == 0;
  }
}
