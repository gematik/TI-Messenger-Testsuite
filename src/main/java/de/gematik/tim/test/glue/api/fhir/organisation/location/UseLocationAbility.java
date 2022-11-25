/*
 * Copyright (c) 2022 gematik GmbH
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

import static de.gematik.tim.test.glue.api.TestdriverApiPath.LOCATION_ID_VARIABLE;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@RequiredArgsConstructor
public class UseLocationAbility extends MultiTargetAbility<String, String> implements
    TestdriverApiAbility {

  private UseLocationAbility(String locationName, String locationId) {
    addAndSetActive(locationName, locationId);
  }

  private static UseLocationAbility useLocation(String locationName, String locationId) {
    return new UseLocationAbility(locationName, locationId);
  }

  public static <T extends Actor> void addLocationToActor(String locationName, String locationId,
      T actor) {
    UseLocationAbility ability = actor.abilityTo(UseLocationAbility.class);
    if (isNull(ability)) {
      actor.can(useLocation(locationName, locationId));
      return;
    }
    ability.addAndSetActive(locationName, locationId);
  }

  public static <T extends Actor> void removeLocationFromActor(String locationName, T actor) {
    UseLocationAbility ability = actor.abilityTo(UseLocationAbility.class);
    if (ability != null) {
      ability.remove(locationName);
    }
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String locationId = getActive();
    requireNonNull(locationId);
    return requestSpecification.pathParam(LOCATION_ID_VARIABLE, locationId);
  }
}

