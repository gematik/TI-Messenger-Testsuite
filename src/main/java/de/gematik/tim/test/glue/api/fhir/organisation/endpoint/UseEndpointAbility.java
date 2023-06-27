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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.ENDPOINT_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointTask.deleteEndPoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.EndpointInfo;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class UseEndpointAbility extends MultiTargetAbility<String, EndpointInfo> implements
    TestdriverApiAbility {

  public static <T extends Actor> void addEndpointToActorForHS(T actor, String endpointName,
                                                               String endpointId, String hsName) {
    UseEndpointAbility ability = actor.abilityTo(UseEndpointAbility.class);
    if (isNull(ability)) {
      actor.can(ability = new UseEndpointAbility());
    }
    ability.addAndSetActive(endpointName, new EndpointInfo(endpointId, hsName));
  }

  public static <T extends Actor> void removeEndpointFromActor(String endpointName, T actor) {
    UseEndpointAbility ability = actor.abilityTo(UseEndpointAbility.class);
    if (ability != null) {
      ability.remove(endpointName);
    }
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String endpointId = getActive().endpointId;
    actor.abilityTo(UseHealthcareServiceAbility.class).setActive(getActive().hsName);
    requireNonNull(endpointId);
    return requestSpecification.pathParam(ENDPOINT_ID_VARIABLE, endpointId);
  }

  @Override
  protected Task tearDownPerTarget(String endpointName) {
    setActive(endpointName);
    requireNonNull(actor.abilityTo(UseHealthcareServiceAbility.class))
        .setActive(requireNonNull(getActive().hsName));
    return deleteEndPoint().withName(endpointName);
  }

  protected record EndpointInfo(String endpointId, String hsName) {
  }
}
