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

import static de.gematik.tim.test.glue.api.TestdriverApiPath.LOCATION_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.DeleteLocationTask.deleteLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.UseLocationAbility.LocationInfo;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class UseLocationAbility extends MultiTargetAbility<String, LocationInfo> implements
    TestdriverApiAbility {

  public static <T extends Actor> void addLocationToActorForHS(String locationName, String locationId,
      T actor, String hsName) {
    UseLocationAbility ability = actor.abilityTo(UseLocationAbility.class);
    if (isNull(ability)) {
      ability = new UseLocationAbility();
      actor.can(ability);
    }
    ability.addAndSetActive(locationName, new LocationInfo(locationId, hsName));
  }

  public static <T extends Actor> void removeLocationFromActor(String locationName, T actor) {
    UseLocationAbility ability = actor.abilityTo(UseLocationAbility.class);
    if (ability != null) {
      ability.remove(locationName);
    }
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String locationId = getActive().locationId();
    requireNonNull(locationId);
    return requestSpecification.pathParam(LOCATION_ID_VARIABLE, locationId);
  }

  @Override
  protected Task tearDownPerTarget(String locationName) {
    setActive(locationName);
    requireNonNull(actor.abilityTo(UseHealthcareServiceAbility.class))
        .setActive(requireNonNull(getActive().hsName));
    return deleteLocation().withName(locationName);
  }

  protected record LocationInfo(String locationId, String hsName) {

  }
}
