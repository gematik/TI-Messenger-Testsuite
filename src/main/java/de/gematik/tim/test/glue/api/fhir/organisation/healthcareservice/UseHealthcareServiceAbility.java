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

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.HEALTHCARE_SERVICE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.DeleteHealthcareServicesTask.deleteHealthcareService;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class UseHealthcareServiceAbility extends MultiTargetAbility<String, String> implements
    TestdriverApiAbility {

  private UseHealthcareServiceAbility(String hsName, String hsId) {
    addAndSetActive(hsName, hsId);
  }

  public static <T extends Actor> void addHsToActor(String hsName, String hsId, T actor) {
    UseHealthcareServiceAbility ability = actor.abilityTo(UseHealthcareServiceAbility.class);
    if (isNull(ability)) {
      actor.can(useHs(hsName, hsId));
      return;
    }
    ability.addAndSetActive(hsName, hsId);
  }

  public static <T extends Actor> void removeHsFromActor(String hsName, T actor) {
    UseHealthcareServiceAbility ability = actor.abilityTo(UseHealthcareServiceAbility.class);
    if (ability != null) {
      ability.remove(hsName);
    }
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String hsId = getActive();
    requireNonNull(hsId);
    return requestSpecification.pathParam(HEALTHCARE_SERVICE_ID_VARIABLE, hsId);
  }

  private static UseHealthcareServiceAbility useHs(String hsName, String hsId) {
    return new UseHealthcareServiceAbility(hsName, hsId);
  }

  @Override
  protected Task tearDownPerTarget(String hsName) {
    return deleteHealthcareService().withName(hsName);
  }
}
