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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UPDATE_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirOwnOrganizationQuestion.ownOrgId;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility.addHsToActor;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueHsName;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirOrganizationDTO;
import de.gematik.tim.test.models.FhirReferenceDTO;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@NoArgsConstructor
@AllArgsConstructor
public class UpdateHealthcareServiceTask extends HealthcareSpecificTask {

  private FhirHealthcareServiceDTO healthcareService = new FhirHealthcareServiceDTO();
  private boolean replaceOrganizationRef;

  public static UpdateHealthcareServiceTask updateHealthcareService(
      FhirHealthcareServiceDTO healthcareServiceDTO) {
    requireNonNull(healthcareServiceDTO);
    return new UpdateHealthcareServiceTask(healthcareServiceDTO, false);
  }

  public static UpdateHealthcareServiceTask updateHealthcareService() {
    return new UpdateHealthcareServiceTask();
  }

  public UpdateHealthcareServiceTask withName(String hsName) {
    return forHealthcareService(hsName);
  }

  public UpdateHealthcareServiceTask withFile(String jsonFileName) {
    healthcareService = readJsonFile(jsonFileName, FhirHealthcareServiceDTO.class);
    healthcareService.setName(createUniqueHsName());
    return this;
  }

  public UpdateHealthcareServiceTask withOrganizationOfCurrentDevice() {
    this.replaceOrganizationRef = true;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    if (replaceOrganizationRef) {
      FhirOrganizationDTO organization = actor.asksFor(ownOrgId());
      healthcareService.providedBy(
          new FhirReferenceDTO().reference("Organization/" + organization.getId()));
    }
    actor.attemptsTo(UPDATE_HEALTHCARE_SERVICE.request().with(req -> req.body(healthcareService)));
    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }

    Response response = lastResponse();
    if (response.statusCode() == OK.value()) {
      FhirHealthcareServiceDTO hsDTO = response.body().as(FhirHealthcareServiceDTO.class);
      addHsToActor(hsName, new HealthcareServiceInfo(hsDTO.getName(), hsDTO.getId()), actor);
    }
  }
}
