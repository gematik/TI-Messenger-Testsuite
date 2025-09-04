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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.fromJson;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.models.DeviceInfoDTO.DeviceStatusEnum.CLAIMED;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

import de.gematik.tim.test.glue.api.threading.ParallelExecutor;
import de.gematik.tim.test.glue.api.threading.ParallelQuestionRunner;
import de.gematik.tim.test.models.DeviceInfoDTO;
import de.gematik.tim.test.models.DevicesDTO;
import java.util.List;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

public class UnclaimedDevicesQuestion extends ParallelQuestionRunner<List<Long>> {

  private static final String NO_FREE_DEVICE_LOG = "No free device found at %s";

  public static UnclaimedDevicesQuestion unclaimedDevices() {
    return new UnclaimedDevicesQuestion();
  }

  @SneakyThrows
  public List<Long> searchParallel() {
    final CloseableHttpClient client = ParallelExecutor.getParallelClient().get();
    HttpGet request = new HttpGet(GET_DEVICES.getResolvedPath(actor));
    request.addHeader(TEST_CASE_ID_HEADER, getTestcaseId());
    try (CloseableHttpResponse response = client.execute(request)) {
      HttpEntity entity = response.getEntity();
      String jsonString = entity != null ? new String(entity.getContent().readAllBytes()) : "";
      return getUnclaimedDevices(fromJson(jsonString, DevicesDTO.class));
    }
  }

  @Override
  public List<Long> answeredBy(Actor actor) {
    actor.attemptsTo(
        GET_DEVICES.request().with(res -> res.header(TEST_CASE_ID_HEADER, getTestcaseId())));
    DevicesDTO devices = parseResponse(DevicesDTO.class);
    return getUnclaimedDevices(devices);
  }

  private List<Long> getUnclaimedDevices(DevicesDTO devices) {
    List<Long> freeDeviceIds = getResult(devices.getDevices());
    if (freeDeviceIds.isEmpty()) {
      individualLog(format(NO_FREE_DEVICE_LOG, actor.abilityTo(CallAnApi.class).resolve("")));
    }
    return freeDeviceIds;
  }

  private static List<Long> getResult(List<DeviceInfoDTO> devices) {
    if (devices == null) {
      return emptyList();
    }
    return devices.stream()
        .filter(device -> !CLAIMED.equals(device.getDeviceStatus()))
        .map(DeviceInfoDTO::getDeviceId)
        .toList();
  }
}
