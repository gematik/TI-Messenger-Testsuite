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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UPDATE_LOCATION;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;

import de.gematik.tim.test.models.FhirLocationDTO;
import net.serenitybdd.screenplay.Actor;

public class UpdateLocationTask extends LocationSpecificTask {

  private FhirLocationDTO location;

  private UpdateLocationTask(FhirLocationDTO location) {
    this.location = location;
  }

  public static UpdateLocationTask updateLocation(FhirLocationDTO locationDTO) {
    return new UpdateLocationTask(locationDTO);
  }

  public UpdateLocationTask withDataFrom(FhirLocationDTO update) {
    update.setLocationId(location.getLocationId());
    this.location = update;
    return this;
  }

  public UpdateLocationTask withFile(String jsonFileName) {
    FhirLocationDTO update = readJsonFile(jsonFileName, FhirLocationDTO.class);
    return withDataFrom(update);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(UPDATE_LOCATION.request().with(req -> req.body(location)));
  }
}
