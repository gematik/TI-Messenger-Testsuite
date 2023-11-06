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

package de.gematik.tim.test.glue.api.fhir.organisation.location;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_LOCATION;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.UseLocationAbility.addLocationToActorForHS;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.CREATED;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirLocationDTO;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class CreateLocationTask extends HealthcareSpecificTask implements Task {

  private final String locationName;

  private FhirLocationDTO locationDTO = new FhirLocationDTO();

  public static CreateLocationTask addHealthcareServiceLocation(String locationName) {
    return new CreateLocationTask(locationName);
  }

  public CreateLocationTask fromFile(String jsonFileName) {
    this.locationDTO = readJsonFile(jsonFileName, FhirLocationDTO.class);
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);

    actor.attemptsTo(CREATE_LOCATION.request().with(req -> req.body(locationDTO)));

    Response response = lastResponse();
    if (response.statusCode() == CREATED.value()) {
      FhirLocationDTO location = parseResponse(FhirLocationDTO.class,true);
      addLocationToActorForHS(locationName, location.getId(), actor, hsName);
    }
  }
}
