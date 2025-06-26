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

package de.gematik.tim.test.glue.api.devices;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UNCLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DEVICE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import de.gematik.tim.test.glue.api.login.IsLoggedInAbility;
import de.gematik.tim.test.glue.api.teardown.TeardownAbility;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@Getter
@RequiredArgsConstructor
public class UseDeviceAbility extends TeardownAbility implements TestdriverApiAbility {

  public static final String TEST_CASE_ID_HEADER = "Transaction-Id";
  private final long deviceId;

  public static UseDeviceAbility useDevice(long deviceId) {
    return new UseDeviceAbility(deviceId);
  }

  public static UseDeviceAbility as(Actor actor) {
    return actor.abilityTo(UseDeviceAbility.class);
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    return requestSpecification
        .headers(TEST_CASE_ID_HEADER, getTestcaseId())
        .pathParam(DEVICE_ID_VARIABLE, deviceId);
  }

  @Override
  protected void teardownThis() {
    IsLoggedInAbility loggedInAbility = actor.abilityTo(IsLoggedInAbility.class);
    if (nonNull(loggedInAbility)) {
      loggedInAbility.tearDown();
    }
    actor.attemptsTo(UNCLAIM_DEVICE.request());
  }
}
