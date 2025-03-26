/*
 * Copyright 2024 gematik GmbH
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
 */

package de.gematik.tim.test.glue.api.teardown;

import de.gematik.tim.test.glue.api.authorization.HasBlockAndAllowListAbility;
import de.gematik.tim.test.glue.api.contact.HasContactAbility;
import de.gematik.tim.test.glue.api.devices.UseDeviceAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.fhir.practitioner.CanDeleteOwnMxidAbility;
import de.gematik.tim.test.glue.api.login.IsLoggedInAbility;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TeardownOrder {
  USE_DEVICE(1, UseDeviceAbility.class),
  LOGGED_IN(2, IsLoggedInAbility.class),
  USE_HS(3, UseHealthcareServiceAbility.class),
  USE_ROOM(3, UseRoomAbility.class),
  CAN_DELETE_MXID(3, CanDeleteOwnMxidAbility.class),
  HAS_CONTACT(3, HasContactAbility.class),
  HAS_BLOCK_AND_ALLOW_LIST(3, HasBlockAndAllowListAbility.class),
  USE_ENDPOINT(4, UseEndpointAbility.class);

  private final int orderNumber;
  private final Class<? extends TeardownAbility> abilityClass;

  public static List<Class<? extends TeardownAbility>> before(TeardownAbility ability) {
    int referenceOrderNumber = orderNumberFor(ability);
    return Arrays.stream(values())
        .filter(t -> t.orderNumber == referenceOrderNumber + 1)
        .map(t -> t.abilityClass)
        .collect(Collectors.toList());
  }

  private static int orderNumberFor(TeardownAbility ability) {
    for (TeardownOrder t : values()) {
      if (t.abilityClass.isInstance(ability)) {
        return t.orderNumber;
      }
    }
    throw new UnsupportedOperationException(
        "this ability is not able to perform a teardown: " + ability);
  }
}
