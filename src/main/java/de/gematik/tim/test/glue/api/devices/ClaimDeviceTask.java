/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.devices;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DEVICE_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.devices.UnclaimedDevicesQuestion.unclaimedDevices;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.ONE_SECOND;
import static org.awaitility.Durations.TEN_SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;

import de.gematik.tim.test.models.ClaimDeviceRequestDTO;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class ClaimDeviceTask implements Task {

  private Long deviceId;
  private int claimDuration = 180;
  private Duration noFreeDeviceTimeout = TEN_SECONDS;
  private Duration freeDevicesPollInterval = ONE_SECOND;

  public static ClaimDeviceTask claimDevice() {
    return new ClaimDeviceTask();
  }

  public ClaimDeviceTask withId(Long deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  public ClaimDeviceTask forSeconds(int claimDuration) {
    this.claimDuration = claimDuration;
    return this;
  }

  public ClaimDeviceTask withDevicePollInterval(Duration freeDevicesPollInterval) {
    this.freeDevicesPollInterval = freeDevicesPollInterval;
    return this;
  }

  public ClaimDeviceTask timeout(Duration timeout) {
    this.noFreeDeviceTimeout = timeout;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    if (deviceId == null) {
      deviceId = await()
          .atMost(noFreeDeviceTimeout)
          .pollInterval(freeDevicesPollInterval)
          .until(() -> findDeviceToClaim(actor), Optional::isPresent)
          .orElseThrow();
    }

    sendClaimRequest(actor, deviceId, claimDuration);

    actor.can(useDevice(deviceId));
    actor.remember(DEVICE_ID, deviceId);
  }

  private Optional<Long> findDeviceToClaim(Actor actor) {
    List<Long> unclaimedDevices = actor.asksFor(unclaimedDevices());
    return unclaimedDevices.stream().findAny();
  }

  private static <T extends Actor> void sendClaimRequest(T actor, long deviceId,
      int claimDuration) {

    ClaimDeviceRequestDTO claimRequest = new ClaimDeviceRequestDTO()
        .claimerName(actor.getName())
        .claimFor(claimDuration);

    actor.attemptsTo(CLAIM_DEVICE.request()
        .with(request -> request
            .pathParam("deviceId", deviceId)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .body(claimRequest)));

    actor.should(
        seeThatResponse("device is successfully claimed",
            res -> res.statusCode(200)
                .body("claimer", equalTo(actor.getName()))));

    actor.remember(CLAIMER_NAME, actor.getName());
  }
}
