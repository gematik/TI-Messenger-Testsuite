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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_ORG_ADMIN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.glue.api.cleanup.CleanupTrigger.sendCleanupRequest;
import static de.gematik.tim.test.glue.api.devices.CheckClientKindTask.checkIs;
import static de.gematik.tim.test.glue.api.devices.ClaimDeviceTask.claimDevice;
import static de.gematik.tim.test.glue.api.devices.ClientKind.CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.EPA_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.ORG_ADMIN;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRACTITIONER;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRO_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRO_PRACTITIONER;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.login.LoginGlue.logsIn;
import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.prepareApiNameForHttp;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.registerActor;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.setParallelFlag;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.startTest;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CLAIM_PARALLEL;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.NO_PARALLEL_TAG;
import static java.lang.Boolean.TRUE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.withCurrentActor;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.cleanup.TestCaseContext;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.glue.api.threading.ParallelExecutor;
import de.gematik.tim.test.glue.api.utils.IndividualLogger;
import de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.plugin.event.TestCase;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

@Slf4j
@CucumberOptions(plugin = {"de.gematik.tim.test.glue.api.utils.cleaning.CucumberListener"})
public class DevicesControllerGlue {

  @Getter @Setter private static boolean allowParallelClaim = true;

  @Before
  public void setup(Scenario scenario) {
    if (TestcasePropertiesManager.isDryRun()) {
      return;
    }
    startTest(scenario);
    RawDataStatistics.startTest();
    IndividualLogger.startTest();
    setAllowParallelClaim(!scenario.getSourceTagNames().contains(NO_PARALLEL_TAG));
    if (TRUE.equals(CLAIM_PARALLEL) && allowParallelClaim) {
      setParallelFlag(true);
    }
    TestCase testCase = TestCaseContext.getTestCase();
    boolean cleanUpSuccess = sendCleanupRequest(testCase.getTestSteps());
    if (!cleanUpSuccess) {
      throw new TestRunException("Cleanup failed - scenario will be skipped");
    }
  }

  @After
  public void teardown() {
    if (TestcasePropertiesManager.isDryRun()) {
      return;
    }
    stage().drawTheCurtain();
    RawDataStatistics.addToReport();
    IndividualLogger.addToReport();
    if (TRUE.equals(CLAIM_PARALLEL)) {
      ParallelExecutor.reset();
    }
    setAllowParallelClaim(true);
  }

  @Given("Following clients are claimed:")
  @Angenommen("Es werden folgende Clients reserviert:")
  public void followingClientsWillBeClaimed(DataTable data) {
    List<ClaimInfo> claimInfos = data.asLists().stream().map(this::toClaimInfo).toList();
    if (allowParallelClaim && TRUE.equals(CLAIM_PARALLEL)) {
      handleParallel(claimInfos);
      setParallelFlag(false);
    } else {
      claimInfos.forEach(this::claimSpecificDevice);
    }
  }

  private void handleParallel(List<ClaimInfo> claimInfos) {
    List<Callable<Void>> calls =
        claimInfos.stream()
            .map(
                claimInfo ->
                    (Callable<Void>)
                        () -> {
                          claimSpecificDevice(claimInfo);
                          return null;
                        })
            .toList();
    ParallelExecutor.run(calls);
  }

  private void claimSpecificDevice(ClaimInfo claimInfo) {
    switch (claimInfo.kind) {
      case PRACTITIONER -> reserveClient(claimInfo.actor, claimInfo.api, CLIENT, PRACTITIONER);
      case ORG_ADMIN -> reserveClient(claimInfo.actor, claimInfo.api, ORG_ADMIN);
      case CLIENT -> reserveClient(claimInfo.actor, claimInfo.api, CLIENT);
      case EPA_CLIENT -> reserveClient(claimInfo.actor, claimInfo.api, CLIENT, EPA_CLIENT);
      case PRO_CLIENT -> reserveClient(claimInfo.actor, claimInfo.api, CLIENT, PRO_CLIENT);
      case PRO_PRACTITIONER ->
          reserveClient(claimInfo.actor, claimInfo.api, CLIENT, PRACTITIONER, PRO_PRACTITIONER);
    }
  }

  private void reserveClient(Actor actor, String apiName, ClientKind... neededKinds) {
    reserveClientOnApi(actor, apiName);
    checkIs(List.of(neededKinds)).withActor(actor).run();
    if (Arrays.asList(neededKinds).contains(ORG_ADMIN)) {
      actor.remember(IS_ORG_ADMIN, true);
    }
    logsIn(actor);
  }

  @And("{string} claims a client and uses the data of {string} to log into api {word}")
  @Und(
      "{string} reserviert ein Client und meldet sich mit den Daten von {string} an der Schnittstelle {word} an")
  public void reserveClientOnApiAndLoginWithData(
      String actorName, String userName, String apiName) {
    Actor actor = reserveClientOnApi(actorName, apiName);

    String mxId = theActorCalled(userName).recall(MX_ID);
    String password = theActorCalled(userName).recall(ACCOUNT_PASSWORD);
    String homeServer = theActorCalled(userName).recall(HOME_SERVER);
    actor.remember(MX_ID, mxId);
    actor.remember(ACCOUNT_PASSWORD, password);
    actor.remember(HOME_SERVER, homeServer);

    actor.attemptsTo(login().withoutClearingRooms());
    actor.asksFor(ownRooms());
  }

  private Actor reserveClientOnApi(String actorName, String apiName) {
    return reserveClientOnApi(theActorCalled(actorName), apiName);
  }

  public Actor reserveClientOnApi(Actor actor, String apiName) {
    String apiUrl = prepareApiNameForHttp(apiName);
    actor.whoCan(CallAnApi.at(apiUrl)).entersTheScene();
    registerActor(actor);
    claimDevice().withActor(actor).run();
    return actor;
  }

  @When("get all devices")
  @Wenn("die Liste der Geräte abgerufen wird")
  public void getDevicesCurrentActor() {
    withCurrentActor(
        GET_DEVICES.request().with(res -> res.header(TEST_CASE_ID_HEADER, getTestcaseId())));
  }

  @Then("{string} has claimed a device")
  @Dann("prüfe ob {string} ein Gerät reserviert hat")
  public void checkIfDeviceIsClaimedGivenActor(String actorName) {
    Actor actor = theActorCalled(actorName);
    checkResponseCode(actor.getName(), OK.value());
    String claimerName = actor.recall(CLAIMER_NAME);
    lastResponse().then().assertThat().body("devices.claimerName", hasItem(claimerName));
  }

  private ClaimInfo toClaimInfo(List<String> data) {
    return new ClaimInfo(theActorCalled(data.get(0)), ClientKind.valueOf(data.get(1)), data.get(2));
  }

  private record ClaimInfo(Actor actor, ClientKind kind, String api) {}
}
