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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UNCLAIM_DEVICE;
import static de.gematik.tim.test.glue.api.devices.ClaimDeviceTask.claimDevice;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForTeardown;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CERT_CN;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.SAVE_CONNECTIONS;
import static de.gematik.tim.test.models.DeviceInfoDTO.DeviceStatusEnum.FREE;
import static java.lang.Boolean.FALSE;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import de.gematik.tim.test.models.DeviceInfoDTO;
import de.gematik.tim.test.models.DevicesDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.springframework.http.HttpStatus;

public class DeviceManager {

  public static final Actor UNCLAIMER = theActorCalled("Unclaimer");
  private static final String UNCLAIMED = "unclaimed";
  private static final String FOREIGN_CLAIMED = "foreignClaimed";
  private static final String CLAIMED = "claimed";
  private static final String CLAIMED_OUTDATED = "outdated";
  private static DeviceManager instance;
  Map<String, List<Long>> devicesInUse;

  private DeviceManager() {
    devicesInUse = new HashMap<>();
  }

  public static DeviceManager getInstance() {
    if (instance == null) {
      instance = new DeviceManager();
    }
    return instance;
  }

  public void release(Actor actor) {
    if (FALSE.equals(SAVE_CONNECTIONS)) {
      repeatedRequestForTeardown(() -> runTeardown(actor),actor);
      return;
    }
    String api = actor.abilityTo(CallAnApi.class).resolve("");
    devicesInUse.get(api).remove(actor.abilityTo(UseDeviceAbility.class).getDeviceId());
  }

  private Optional<Boolean> runTeardown(Actor actor) {
    UNCLAIM_DEVICE.request().performAs(actor);
    if (HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()) {
      return Optional.of(Boolean.TRUE);
    }
    return Optional.empty();
  }

  public void orderDeviceToActor(Actor actor, String api) {
    if (FALSE.equals(SAVE_CONNECTIONS)) {
      actor.attemptsTo(claimDevice().withClaimerName(actor.getName()));
      return;
    }
    Map<String, List<DeviceInfoDTO>> orderedIds = getDevicesMap(actor);
    if (isNull(devicesInUse.get(api))) {
      actor.attemptsTo(claimDevice());
    } else {
      Optional<DeviceInfoDTO> device = orderedIds.get(CLAIMED).stream()
          .filter(not(e -> devicesInUse.get(api).contains(e.getDeviceId()))).findAny();
      if (device.isPresent()) {
        actor.can(useDevice(device.get().getDeviceId()));
        actor.remember(CLAIMER_NAME, device.get().getClaimerName());
      } else if (!orderedIds.get(UNCLAIMED).isEmpty()) {
        actor.attemptsTo(claimDevice().withId(orderedIds.get(UNCLAIMED).get(0).getDeviceId()));
      } else {
        actor.attemptsTo(claimDevice());
      }
    }
    devicesInUse.computeIfAbsent(api, k -> new ArrayList<>())
        .add(actor.abilityTo(UseDeviceAbility.class).getDeviceId());
  }

  public void unclaimAll() {
    devicesInUse.keySet().forEach(key -> {
      UNCLAIMER.whoCan(CallAnApi.at(key)).entersTheScene();
      getSelfClaimedDeviceIds().forEach(this::unclaim);
    });
    devicesInUse = new HashMap<>();
  }


  private void unclaim(DeviceInfoDTO device) {
    UseDeviceAbility ability = useDevice(device.getDeviceId());
    ability.setTearedDown(true);
    UNCLAIMER.can(ability);
    UNCLAIM_DEVICE.request().performAs(UNCLAIMER);
  }

  private List<DeviceInfoDTO> getSelfClaimedDeviceIds() {
    return getDevicesMap(UNCLAIMER, 0L).get(CLAIMED);
  }

  private Map<String, List<DeviceInfoDTO>> getDevicesMap(Actor actor) {
    return getDevicesMap(actor, 60L);
  }

  private Map<String, List<DeviceInfoDTO>> getDevicesMap(Actor actor, Long minLeftClaimTime) {
    actor.attemptsTo(GET_DEVICES.request());
    DevicesDTO devicesInfo = parseResponse(DevicesDTO.class);
    Map<String, List<DeviceInfoDTO>> map = requireNonNull(devicesInfo.getDevices()).stream()
        .collect(groupingBy(d -> getUsageStatus(d, minLeftClaimTime)));
    map.computeIfAbsent(CLAIMED, k -> List.of());
    map.computeIfAbsent(UNCLAIMED, k -> List.of());
    map.computeIfAbsent(CLAIMED_OUTDATED, k -> List.of());
    map.computeIfAbsent(FOREIGN_CLAIMED, k -> List.of());
    return map;
  }

  private String getUsageStatus(DeviceInfoDTO device, Long minLeftClaimTime) {
    if (device.getDeviceStatus() == FREE) {
      return UNCLAIMED;
    }
    if (requireNonNull(device.getClaimerCn()).equals(CERT_CN)
        && device.getClaimedUntil()
        .isBefore(now().plus(minLeftClaimTime, SECONDS))) {
      return CLAIMED_OUTDATED;
    }
    if (device.getClaimerCn().equals(CERT_CN)) {
      return CLAIMED;
    }
    return FOREIGN_CLAIMED;
  }

}
