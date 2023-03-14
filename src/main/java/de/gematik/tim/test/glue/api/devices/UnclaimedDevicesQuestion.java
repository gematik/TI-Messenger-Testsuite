/*
 * Copyright 20023 gematik GmbH
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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.models.DeviceInfoDTO.DeviceStatusEnum.CLAIMED;
import static java.util.Collections.emptyList;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.DeviceInfoDTO;
import de.gematik.tim.test.models.DevicesDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class UnclaimedDevicesQuestion implements Question<List<Long>> {

  public static UnclaimedDevicesQuestion unclaimedDevices() {
    return new UnclaimedDevicesQuestion();
  }

  @Override
  public List<Long> answeredBy(Actor actor) {
    actor.attemptsTo(GET_DEVICES.request());
    DevicesDTO devices = lastResponse().body().as(DevicesDTO.class);

    List<DeviceInfoDTO> devicesList = devices.getDevices();
    if (devicesList == null) {
      return emptyList();
    }

    return devicesList.stream()
        .filter(device -> !CLAIMED.equals(device.getDeviceStatus()))
        .map(DeviceInfoDTO::getDeviceId)
        .toList();
  }
}
