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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.ENDPOINT_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointTask.deleteEndPoint;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForTeardown;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.EndpointInfo;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import io.restassured.specification.RequestSpecification;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.springframework.http.HttpStatus;

public class UseEndpointAbility extends MultiTargetAbility<String, EndpointInfo>
    implements TestdriverApiAbility {

  public static <T extends Actor> void addEndpointToActorForHS(
      T actor, String endpointName, String endpointId, String hsName) {
    UseEndpointAbility ability = actor.abilityTo(UseEndpointAbility.class);
    if (isNull(ability)) {
      ability = new UseEndpointAbility();
      actor.can(ability);
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
    return new Task() {
      @Override
      public <T extends Actor> void performAs(T t) {
        repeatedRequestForTeardown(() -> tearDownEndpoint(endpointName), actor);
      }
    };
  }

  private Optional<Boolean> tearDownEndpoint(String endpointName) {
    setActive(endpointName);
    actor.attemptsTo(deleteEndPoint().withName(endpointName));
    return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
        ? Optional.of(TRUE)
        : Optional.empty();
  }

  protected record EndpointInfo(String endpointId, String hsName) {}
}
