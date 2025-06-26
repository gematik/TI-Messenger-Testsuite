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

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HAS_REG_SERVICE_TOKEN;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_DELETED_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirIsHealthcareServiceDeletedQuestion implements Question<Boolean> {

  private Long customPollInterval;
  private Long customTimeout;

  public static FhirIsHealthcareServiceDeletedQuestion getDeletedHealthcareService() {
    return new FhirIsHealthcareServiceDeletedQuestion();
  }

  public FhirIsHealthcareServiceDeletedQuestion withCustomInterval(
      Long pollInterval, Long timeout) {
    this.customPollInterval = pollInterval;
    this.customTimeout = timeout;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    return repeatedRequest(
        () -> requestIsFhirHealthcareServiceDeleted(actor),
        "already-deleted-healthcare-service",
        customTimeout,
        customPollInterval);
  }

  private Optional<Boolean> requestIsFhirHealthcareServiceDeleted(Actor actor) {
    actor.attemptsTo(SEARCH_DELETED_HEALTHCARE_SERVICE.request());
    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }
    return lastResponse().getStatusCode() == NOT_FOUND.value()
        ? Optional.of(true)
        : Optional.empty();
  }
}
