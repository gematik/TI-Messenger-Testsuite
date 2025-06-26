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

import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointQuestion.getEndpoint;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForEmptyResult;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirExtensionDTO;
import java.util.List;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class NoFhirVisibilityOfHealthcareServiceQuestion implements Question<Boolean> {

  private Long customTimeout;
  private Long customPollInterval;

  private static final String ENDPOINT_VISIBILITY_URL =
      "https://gematik.de/fhir/directory/StructureDefinition/EndpointVisibility";

  public static NoFhirVisibilityOfHealthcareServiceQuestion endpointVisibleInFhirDirectory() {
    return new NoFhirVisibilityOfHealthcareServiceQuestion();
  }

  public NoFhirVisibilityOfHealthcareServiceQuestion withCustomInterval(
      Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    try {
      repeatedRequestForEmptyResult(
          () -> requestVisibleFhirEndpoint(actor), customTimeout, customPollInterval);
    } catch (ConditionTimeoutException e) {
      log.error("Endpoint is still hidden", e);
      throw new AssertionFailed("Endpoint is still hidden");
    }
    return true;
  }

  private boolean requestVisibleFhirEndpoint(Actor orgAdmin) {
    FhirEndpointDTO endpoint = orgAdmin.asksFor(getEndpoint());
    return isCorrectEndpointNameAndVisible(endpoint);
  }

  private boolean isCorrectEndpointNameAndVisible(FhirEndpointDTO endpoint) {
    List<FhirExtensionDTO> extensions = endpoint.getExtension();
    if (extensions == null || extensions.isEmpty()) {
      return true;
    }

    for (FhirExtensionDTO extension : extensions) {
      if (extension.getUrl().equals(ENDPOINT_VISIBILITY_URL)) {
        log.error("Endpoint endpoint with name {} is still hidden", endpoint.getName());
        return false;
      }
    }
    return true;
  }
}
