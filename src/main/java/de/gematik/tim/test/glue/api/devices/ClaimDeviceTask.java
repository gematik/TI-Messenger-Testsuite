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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DEVICE_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.devices.UnclaimedDevicesQuestion.unclaimedDevices;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.parallelClient;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestWithLongerTimeout;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CLAIM_DURATION;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.MAX_RETRY_CLAIM_REQUEST;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.hamcrest.CoreMatchers.equalTo;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.glue.api.threading.Parallel;
import de.gematik.tim.test.glue.api.threading.ParallelExecutor;
import de.gematik.tim.test.models.ClaimDeviceRequestDTO;
import de.gematik.tim.test.models.DeviceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("BusyWait")
@Builder
@AllArgsConstructor(access = PRIVATE)
public class ClaimDeviceTask implements Task, Parallel<ActorsNotes> {

  private final static String FAIL_CLAIM_LOG = "Claiming device at api %s failed!";
  private Long deviceId;
  private Integer claimDuration;
  private String claimerName;

  public static ClaimDeviceTask claimDevice() {
    return claimDeviceFor(CLAIM_DURATION);
  }

  public static ClaimDeviceTask claimDeviceFor(Integer claimDuration) {
    return ClaimDeviceTask.builder().claimDuration(claimDuration).build();
  }

  public ClaimDeviceTask withId(Long deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  public ClaimDeviceTask withClaimerName(String name) {
    this.claimerName = name;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    sendClaimRequest(actor, claimDuration, claimerName);

    actor.can(useDevice(deviceId));
    actor.remember(DEVICE_ID, deviceId);
    actor.remember(IS_LOGGED_IN, false);
  }

  @SneakyThrows
  private <T extends Actor> void sendClaimRequest(T actor, int claimDuration, String claimerName) {
    final String name = isBlank(claimerName) ? UUID.randomUUID().toString() : claimerName;
    ClaimDeviceRequestDTO claimRequest = new ClaimDeviceRequestDTO()
        .claimerName(name)
        .claimFor(claimDuration);

    sendRequest(actor, claimRequest);

    int retryCount = 1;
    while (!HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
        && retryCount < MAX_RETRY_CLAIM_REQUEST) {
      retryCount++;
      individualLog(format(FAIL_CLAIM_LOG, actor.abilityTo(CallAnApi.class).toString()));
      Thread.sleep(5000);
      sendRequest(actor, claimRequest);
    }
    actor.should(
        seeThatResponse("device is successfully claimed",
            res -> res.statusCode(200)
                .body("claimerName", equalTo(name))));
    DeviceInfoDTO device = parseResponse(DeviceInfoDTO.class);
    this.deviceId = device.getDeviceId();
    actor.remember(CLAIMER_NAME, name);
  }

  private <T extends Actor> void sendRequest(T actor, ClaimDeviceRequestDTO claimRequest) {
    final long useDeviceId = findFreeDeviceId(actor);
    actor.attemptsTo(CLAIM_DEVICE.request()
        .with(request -> request
            .pathParam("deviceId", useDeviceId)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header(TEST_CASE_ID_HEADER, getTestcaseId())
            .body(claimRequest)));
  }

  private <T extends Actor> Long findFreeDeviceId(T actor) {
    if (nonNull(deviceId)) {
      return deviceId;
    }
    return repeatedRequestWithLongerTimeout(() -> findDeviceToClaim(actor), "device", 10);
  }

  private Optional<Long> findDeviceToClaim(Actor actor) {
    List<Long> unclaimedDevices = actor.asksFor(unclaimedDevices());
    return unclaimedDevices.stream().findAny();
  }

  @Override
  @SneakyThrows
  public ActorsNotes parallel(ActorsNotes notes) {
    if (deviceId == null) {
      deviceId = repeatedRequestWithLongerTimeout(
          () -> unclaimedDevices().parallel(notes).stream()
              .filter(ParallelExecutor::isClaimable)
              .findAny(), "device", 10);
    }

    final String name = isBlank(claimerName) ? UUID.randomUUID().toString() : claimerName;
    ClaimDeviceRequestDTO claimRequest = new ClaimDeviceRequestDTO()
        .claimerName(name)
        .claimFor(claimDuration);
    notes.remember(DEVICE_ID, deviceId);

    int responseCode = sendRequest(notes, claimRequest);
    int retryCount = 1;
    while (responseCode != 200 && retryCount < MAX_RETRY_CLAIM_REQUEST) {
      retryCount++;
      individualLog(format(FAIL_CLAIM_LOG, notes.getApi()));
      Thread.sleep(5000);
      responseCode = sendRequest(notes, claimRequest);
    }

    if (responseCode != 200) {
      throw new TestRunException(
          "claiming device failed for actor '%s'".formatted(notes.getName()));
    }

    notes.remember(CLAIMER_NAME, name);
    notes.remember(DEVICE_ID, deviceId);
    notes.addAbility(useDevice(deviceId));
    notes.remember(IS_LOGGED_IN, false);
    return notes;
  }

  private int sendRequest(ActorsNotes notes, ClaimDeviceRequestDTO claimRequest) {
    try (Response response = parallelClient().get()
        .newCall(CLAIM_DEVICE.parallelRequest(notes).build(claimRequest))
        .execute()) {
      return response.code();
    } catch (IOException e) {
      throw new TestRunException("error executing claim request", e);
    }
  }
}
