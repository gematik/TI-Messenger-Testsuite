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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.ENDPOINT_ID_VARIABLE;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;

public class UseEndpointAbility extends MultiTargetAbility<String, String> implements
    TestdriverApiAbility {

  private UseEndpointAbility(String endpointName, String endpointId) {
    addAndSetActive(endpointName, endpointId);
  }

  private static UseEndpointAbility useEndpoint(String endpointName, String endpointId) {
    return new UseEndpointAbility(endpointName, endpointId);
  }

  public static <T extends Actor> void addEndpointToActor(String endpointName, String endpointId,
      T actor) {
    UseEndpointAbility ability = actor.abilityTo(UseEndpointAbility.class);
    if (isNull(ability)) {
      actor.can(useEndpoint(endpointName, endpointId));
      return;
    }
    ability.addAndSetActive(endpointName, endpointId);
  }

  public static <T extends Actor> void removeEndpointFromActor(String endpointName, T actor) {
    UseEndpointAbility ability = actor.abilityTo(UseEndpointAbility.class);
    if (ability != null) {
      ability.remove(endpointName);
    }
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String endpointId = getActive();
    requireNonNull(endpointId);
    return requestSpecification.pathParam(ENDPOINT_ID_VARIABLE, endpointId);
  }
}
