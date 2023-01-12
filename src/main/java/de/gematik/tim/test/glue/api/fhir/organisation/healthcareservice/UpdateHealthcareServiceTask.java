/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UPDATE_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@NoArgsConstructor
@AllArgsConstructor
public class UpdateHealthcareServiceTask extends HealthcareSpecificTask {

  private FhirHealthcareServiceDTO healthcareService = new FhirHealthcareServiceDTO();

  public static UpdateHealthcareServiceTask updateHealthcareService(
      FhirHealthcareServiceDTO healthcareServiceDTO) {
    requireNonNull(healthcareServiceDTO);
    return new UpdateHealthcareServiceTask(healthcareServiceDTO);
  }

  public static UpdateHealthcareServiceTask updateHealthcareService() {
    return new UpdateHealthcareServiceTask();
  }

  public UpdateHealthcareServiceTask withName(String hsName) {
    return forHealthcareService(hsName);
  }

  public UpdateHealthcareServiceTask withFile(String jsonFileName) {
    healthcareService = readJsonFile(jsonFileName, FhirHealthcareServiceDTO.class);
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(UPDATE_HEALTHCARE_SERVICE.request()
        .with(req -> req.body(healthcareService)));
  }
}
