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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DEVICE_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.devices.UnclaimedDevicesQuestion.unclaimedDevices;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.CLAIM_DURATION;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.repeatedRequestWithLongerTimeout;
import static de.gematik.tim.test.glue.api.utils.TestcaseIdProvider.getTestcaseId;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.hamcrest.CoreMatchers.equalTo;

import de.gematik.tim.test.models.ClaimDeviceRequestDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@Builder
@AllArgsConstructor(access = PRIVATE)
public class ClaimDeviceTask implements Task {


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

    if (deviceId == null) {
      deviceId = repeatedRequestWithLongerTimeout(() -> findDeviceToClaim(actor), "device", 10);
    }

    sendClaimRequest(actor, deviceId, claimDuration, claimerName);

    actor.can(useDevice(deviceId));
    actor.remember(DEVICE_ID, deviceId);
  }

  private Optional<Long> findDeviceToClaim(Actor actor) {
    List<Long> unclaimedDevices = actor.asksFor(unclaimedDevices());
    return unclaimedDevices.stream().findAny();
  }

  private static <T extends Actor> void sendClaimRequest(T actor, long deviceId,
      int claimDuration, String claimerName) {
    final String name = isBlank(claimerName) ? UUID.randomUUID().toString() : claimerName;
    ClaimDeviceRequestDTO claimRequest = new ClaimDeviceRequestDTO()
        .claimerName(name)
        .claimFor(claimDuration);

    actor.attemptsTo(CLAIM_DEVICE.request()
        .with(request -> request
            .pathParam("deviceId", deviceId)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header(TEST_CASE_ID_HEADER, getTestcaseId())
            .body(claimRequest)));

    actor.should(
        seeThatResponse("device is successfully claimed",
            res -> res.statusCode(200)
                .body("claimer", equalTo(name))));

    actor.remember(CLAIMER_NAME, name);
  }
}
