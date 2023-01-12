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

package de.gematik.tim.test.glue.api.fhir.organisation.location;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_LOCATION;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.UseLocationAbility.removeLocationFromActor;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;

public class DeleteLocationTask extends LocationSpecificTask {

  public static DeleteLocationTask deleteLocation() {
    return new DeleteLocationTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);

    actor.attemptsTo(DELETE_LOCATION.request());

    Response response = lastResponse();
    if (response.statusCode() == NO_CONTENT.value()) {
      removeLocationFromActor(getLocationName(), actor);
    }
  }
}
