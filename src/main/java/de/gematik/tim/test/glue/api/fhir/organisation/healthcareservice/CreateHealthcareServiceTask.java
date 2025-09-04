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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility.addHsToActor;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueHsName;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.glue.api.utils.TestsuiteInitializer;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class CreateHealthcareServiceTask implements Task {

  private final String hsName;
  private final FhirHealthcareServiceDTO healthcareServiceDTO = new FhirHealthcareServiceDTO();

  public static CreateHealthcareServiceTask createHealthcareService(String hsName) {
    CreateHealthcareServiceTask createHealthcareServiceTask =
        new CreateHealthcareServiceTask(hsName);
    createHealthcareServiceTask.healthcareServiceDTO.name(createUniqueHsName());
    return createHealthcareServiceTask;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(
        CREATE_HEALTHCARE_SERVICE.request().with(req -> req.body(healthcareServiceDTO)));

    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }

    Response response = lastResponse();
    if (response.statusCode() == CREATED.value()) {
      FhirHealthcareServiceDTO createdHealthcareServiceDTO =
          parseResponse(FhirHealthcareServiceDTO.class);
      assertThat(createdHealthcareServiceDTO.getId())
          .as("Healthcare-Service %s id is empty".formatted(createdHealthcareServiceDTO.getName()))
          .isNotBlank();
      addHsToActor(
          hsName,
          new HealthcareServiceInfo(
              createdHealthcareServiceDTO.getName(), createdHealthcareServiceDTO.getId()),
          actor);
    }
  }
}
