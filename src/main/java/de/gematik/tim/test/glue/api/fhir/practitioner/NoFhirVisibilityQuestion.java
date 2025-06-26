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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.fhir.practitioner.OwnFhirResourceQuestion.ownFhirResource;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForEmptyResult;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static java.lang.String.format;

import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirExtensionDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class NoFhirVisibilityQuestion implements Question<Boolean> {

  private Long customTimeout;
  private Long customPollInterval;

  private static final String ENDPOINT_VISIBILITY_URL =
      "https://gematik.de/fhir/directory/StructureDefinition/EndpointVisibility";

  public static NoFhirVisibilityQuestion practitionerVisibleInFhirDirectory() {
    return new NoFhirVisibilityQuestion();
  }

  public NoFhirVisibilityQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    try {
      repeatedRequestForEmptyResult(
          () -> requestVisibleFhirPractitioner(actor), customTimeout, customPollInterval);
    } catch (ConditionTimeoutException e) {
      log.error("Practitioner with name {} is still hidden", actor.getName(), e);
      throw new AssertionFailed(
          format("Practitioner with name %s is still hidden", actor.getName()));
    }
    return true;
  }

  private boolean requestVisibleFhirPractitioner(Actor practitioner) {
    FhirSearchResultDTO result =
        practitioner.asksFor(ownFhirResource().withAtLeastAmountEndpoints(1));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    return isCorrectEndpointNameAndVisible(endpoints, practitioner);
  }

  private boolean isCorrectEndpointNameAndVisible(
      List<FhirEndpointDTO> endpoints, Actor searchedActor) {
    String endpointName = getEndpointFromInternalName(searchedActor.recall(DISPLAY_NAME)).getName();
    endpoints =
        endpoints.stream().filter(endpoint -> endpoint.getName().equals(endpointName)).toList();
    if (endpoints.isEmpty()) {
      log.error("Practitioner endpoint with name {} could not be found", endpointName);
      return false;
    }

    List<FhirExtensionDTO> extensions =
        endpoints.stream()
            .map(FhirEndpointDTO::getExtension)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .toList();
    if (extensions.isEmpty()) {
      return true;
    }

    for (FhirExtensionDTO extension : extensions) {
      if (extension.getUrl().equals(ENDPOINT_VISIBILITY_URL)) {
        log.error("Practitioner endpoint with name {} is still hidden", endpointName);
        return false;
      }
    }
    return true;
  }
}
