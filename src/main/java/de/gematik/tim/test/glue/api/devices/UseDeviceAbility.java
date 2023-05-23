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

package de.gematik.tim.test.glue.api.devices;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.DEVICE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.utils.TestcaseIdProvider.getLastTcId;
import static de.gematik.tim.test.glue.api.utils.TestcaseIdProvider.getTestcaseId;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.fhir.practitioner.CanDeleteOwnMxidAbility;
import de.gematik.tim.test.glue.api.login.IsLoggedInAbility;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;

@Slf4j
@Getter
@RequiredArgsConstructor
public class UseDeviceAbility implements TestdriverApiAbility, HasTeardown, RefersToActor {


  public static final String TEST_CASE_ID_HEADER = "Transaction-Id";
  private final long deviceId;
  private Actor actor;
  @Setter
  private boolean tearedDown = false;

  public static UseDeviceAbility useDevice(long deviceId) {
    return new UseDeviceAbility(deviceId);
  }

  public static UseDeviceAbility as(Actor actor) {
    return actor.abilityTo(UseDeviceAbility.class);
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    String tcId = tearedDown ? getLastTcId() : getTestcaseId();
    return requestSpecification
        .headers(TEST_CASE_ID_HEADER, tcId)
        .pathParam(DEVICE_ID_VARIABLE, deviceId);
  }

  @Override
  public void tearDown() {
    if (tearedDown) {
      return;
    }
    tearedDown = true;
    clearAllBeforeLogoutAndUnclaim(actor);
    IsLoggedInAbility loggedInAbility = actor.abilityTo(IsLoggedInAbility.class);
    if (nonNull(loggedInAbility)) {
      loggedInAbility.tearDown();
    }
    DeviceManager.getInstance().release(actor);
  }

  @Override
  public UseDeviceAbility asActor(Actor actor) {
    this.actor = actor;
    return this;
  }

  public static void clearAllBeforeLogoutAndUnclaim(Actor actor) {
    CanDeleteOwnMxidAbility delOwnFhirIdAbility = actor.abilityTo(CanDeleteOwnMxidAbility.class);
    if (nonNull(delOwnFhirIdAbility)) {
      delOwnFhirIdAbility.tearDown();
    }
    UseRoomAbility useRoomAbility = actor.abilityTo(UseRoomAbility.class);
    if (nonNull(useRoomAbility)) {
      useRoomAbility.tearDown();
    }
    UseHealthcareServiceAbility hsAbility = actor.abilityTo(UseHealthcareServiceAbility.class);
    if (nonNull(hsAbility)) {
      hsAbility.tearDown();
    }
  }


}
