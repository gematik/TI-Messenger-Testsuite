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

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HAS_REG_SERVICE_TOKEN;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirGetHealthcareServiceQuestion extends HealthcareSpecificTask implements
    Question<FhirHealthcareServiceDTO> {

  public static FhirGetHealthcareServiceQuestion getHealthcareService() {
    return new FhirGetHealthcareServiceQuestion();
  }

  public FhirGetHealthcareServiceQuestion withName(String hsName) {
    return forHealthcareService(hsName);
  }

  @Override
  public FhirHealthcareServiceDTO answeredBy(Actor actor) {
    super.performAs(actor);
    actor.attemptsTo(GET_HEALTHCARE_SERVICE.request());
    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }
    return parseResponse(FhirHealthcareServiceDTO.class);
  }
}
