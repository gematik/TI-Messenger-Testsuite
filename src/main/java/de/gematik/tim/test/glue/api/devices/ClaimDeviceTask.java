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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DEVICE_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.devices.UnclaimedDevicesQuestion.unclaimedDevices;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.getParallelClient;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.isClaimable;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.saveLastResponseCode;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.toJson;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestWithLongerTimeout;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CERT_CN;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CLAIM_DURATION;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.MAX_RETRY_CLAIM_REQUEST;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.threading.ParallelTaskRunner;
import de.gematik.tim.test.models.ClaimDeviceRequestDTO;
import de.gematik.tim.test.models.DeviceInfoDTO;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;

@SuppressWarnings("BusyWait")
@Builder
@AllArgsConstructor(access = PRIVATE)
public class ClaimDeviceTask extends ParallelTaskRunner {
  private static final String FAIL_CLAIM_LOG = "Claiming device at api %s failed!";
  private static final String APPLICATION_JSON = "application/json";

  private static final int FACTOR_WAIT_FOR_FREE_DEVICE = 2;

  private Long deviceId;
  private Integer claimDuration;

  public static ClaimDeviceTask claimDevice() {
    return claimDeviceFor(CLAIM_DURATION);
  }

  public static ClaimDeviceTask claimDeviceFor(Integer claimDuration) {
    return ClaimDeviceTask.builder().claimDuration(claimDuration).build();
  }

  @Override
  @SneakyThrows
  public void runParallel() {
    String api = actor.abilityTo(CallAnApi.class).resolve("");
    if (deviceId == null) {
      deviceId =
          repeatedRequestWithLongerTimeout(
              () ->
                  unclaimedDevices().withActor(actor).run().stream()
                      .filter(id -> isClaimable(api, id))
                      .findAny(),
              "device",
              FACTOR_WAIT_FOR_FREE_DEVICE);
    }
    actor.can(UseDeviceAbility.useDevice(deviceId));
    int retryCount = 1;
    int responseCode;
    while ((responseCode = getParallelClientStatus()) != 200
        && retryCount < MAX_RETRY_CLAIM_REQUEST) {
      saveLastResponseCode(actor.getName(), responseCode);
      retryCount++;
      individualLog(FAIL_CLAIM_LOG.formatted(api));
      Thread.sleep(5000);
    }
    if (responseCode != 200) {
      throw new TestRunException(
          "claiming device failed for actor '%s'".formatted(actor.getName()));
    }
  }

  @SneakyThrows
  private int getParallelClientStatus() {
    final ClaimDeviceRequestDTO claimRequest = getClaimDeviceRequestDTO();
    actor.remember(CLAIMER_NAME, claimRequest.getClaimerName());
    final CloseableHttpClient client = getParallelClient().get();
    final HttpPost post = new HttpPost(CLAIM_DEVICE.getResolvedPath(actor));
    post.addHeader(TEST_CASE_ID_HEADER, getTestcaseId());
    post.addHeader("Content-Type", APPLICATION_JSON);
    post.addHeader("Accept", APPLICATION_JSON);
    final StringEntity entity = new StringEntity(toJson(claimRequest));
    post.setEntity(entity);
    try (final CloseableHttpResponse response = client.execute(post)) {
      return response.getStatusLine().getStatusCode();
    }
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    sendClaimRequest(actor);
    actor.remember(DEVICE_ID, deviceId);
    actor.remember(IS_LOGGED_IN, false);
  }

  @SneakyThrows
  private <T extends Actor> void sendClaimRequest(T actor) {
    ClaimDeviceRequestDTO claimRequest = getClaimDeviceRequestDTO();

    sendRequest(actor, claimRequest);

    int retryCount = 1;
    while (!HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
        && retryCount < MAX_RETRY_CLAIM_REQUEST) {
      retryCount++;
      individualLog(format(FAIL_CLAIM_LOG, actor.abilityTo(CallAnApi.class).toString()));
      Thread.sleep(5000);
      sendRequest(actor, claimRequest);
    }

    checkResponseCode(actor.getName(), OK.value());
    lastResponse().then().assertThat().body("claimerName", is(claimRequest.getClaimerName()));
    DeviceInfoDTO device = parseResponse(DeviceInfoDTO.class);
    this.deviceId = device.getDeviceId();
    actor.remember(CLAIMER_NAME, claimRequest.getClaimerName());
  }

  private <T extends Actor> Long findFreeDeviceId(T actor) {
    if (nonNull(deviceId)) {
      return deviceId;
    }
    return repeatedRequestWithLongerTimeout(
        () -> findDeviceToClaim(actor), "device", FACTOR_WAIT_FOR_FREE_DEVICE);
  }

  private Optional<Long> findDeviceToClaim(Actor actor) {
    return unclaimedDevices().withActor(actor).run().stream().findAny();
  }

  private <T extends Actor> void sendRequest(T actor, ClaimDeviceRequestDTO claimRequest) {
    final long useDeviceId = findFreeDeviceId(actor);
    actor.can(useDevice(useDeviceId));
    actor.attemptsTo(
        CLAIM_DEVICE
            .request()
            .with(
                request ->
                    request
                        .pathParam("deviceId", useDeviceId)
                        .header("Accept", APPLICATION_JSON)
                        .header("Content-Type", APPLICATION_JSON)
                        .body(claimRequest)));
  }

  private ClaimDeviceRequestDTO getClaimDeviceRequestDTO() {
    return new ClaimDeviceRequestDTO().claimerName(CERT_CN).claimFor(claimDuration);
  }
}
