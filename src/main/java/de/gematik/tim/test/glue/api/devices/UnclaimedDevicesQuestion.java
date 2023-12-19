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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.parallelClient;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.DeviceInfoDTO.DeviceStatusEnum.CLAIMED;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.glue.api.threading.Parallel;
import de.gematik.tim.test.glue.api.utils.ParallelUtils;
import de.gematik.tim.test.models.DeviceInfoDTO;
import de.gematik.tim.test.models.DevicesDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class UnclaimedDevicesQuestion implements Question<List<Long>>, Parallel<List<Long>> {

  public static final String NO_FREE_DEVICE_LOG = "No free device found at %s";

  public static UnclaimedDevicesQuestion unclaimedDevices() {
    return new UnclaimedDevicesQuestion();
  }

  @Override
  public List<Long> answeredBy(Actor actor) {
    actor.attemptsTo(GET_DEVICES.request());
    DevicesDTO res = parseResponse(DevicesDTO.class);
    return getUnclaimedDevices(res, actor.abilityTo(CallAnApi.class).toString());

  }

  @Override
  public List<Long> parallel(ActorsNotes notes) {
    Call call = parallelClient().get().newCall(GET_DEVICES.parallelRequest(notes).build());
    try (Response response = call.execute()) {
      DevicesDTO devicesDTO = ParallelUtils.fromJson(response.body().string(), DevicesDTO.class);
      return getUnclaimedDevices(devicesDTO, notes.getApi());
    } catch (IOException e) {
      throw new TestRunException(e);
    }
  }

  private List<Long> getUnclaimedDevices(DevicesDTO res, String api) {
    List<Long> freeDeviceIds = getResult(res.getDevices());
    if (freeDeviceIds.isEmpty()) {
      individualLog(format(NO_FREE_DEVICE_LOG, api));
    }
    return freeDeviceIds;
  }

  @NotNull
  private static List<Long> getResult(List<DeviceInfoDTO> devices) {
    if (devices == null) {
      return emptyList();
    }
    return devices.stream().filter(device -> !CLAIMED.equals(device.getDeviceStatus())).map(DeviceInfoDTO::getDeviceId).toList();
  }
}
