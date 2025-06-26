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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_HEALTHCARE_SERVICE;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.LookForDeletedHealthcareServiceAbility.lookUpDeleted;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import org.springframework.http.HttpStatus;

public class DeleteHealthcareServicesTask extends HealthcareSpecificTask {

  public static DeleteHealthcareServicesTask deleteHealthcareService() {
    return new DeleteHealthcareServicesTask();
  }

  public DeleteHealthcareServicesTask withName(String hsName) {
    return forHealthcareService(hsName);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(DELETE_HEALTHCARE_SERVICE.request());

    Response response = lastResponse();
    if (response.statusCode() == HttpStatus.NO_CONTENT.value()) {
      UseHealthcareServiceAbility ability = actor.abilityTo(UseHealthcareServiceAbility.class);
      actor.can(lookUpDeleted(ability.getActiveValue().id()));
      ability.removeCurrent();
    }
    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }
  }
}
