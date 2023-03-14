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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_DEVICES;
import static de.gematik.tim.test.glue.api.devices.ClaimDeviceTask.claimDevice;
import static de.gematik.tim.test.glue.api.devices.ClientKind.MESSENGER_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.ORG_ADMIN;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRACTITIONER;
import static de.gematik.tim.test.glue.api.info.ApiInfoQuestion.apiInfo;
import static de.gematik.tim.test.glue.api.login.LogInGlue.loginSuccess;
import static de.gematik.tim.test.glue.api.login.LogInGlue.logsIn;
import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.addHostToTigerProxy;
import static de.gematik.tim.test.glue.api.utils.TestcaseIdProvider.startTest;
import static de.gematik.tim.test.glue.api.utils.TestcaseIdProvider.stopTest;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.withCurrentActor;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.InfoObjectDTO;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

@Slf4j
public class DevicesControllerGlue {

  @Before
  public void setup(Scenario scenario) {
    startTest(scenario);
    RawDataStatistics.startTest();
    setTheStage(Cast.ofStandardActors());
  }

  @After
  public void teardown() {
    stage().drawTheCurtain();
    RawDataStatistics.addToReport();
    stopTest();
  }

  // Get devices
  @When("{word} get all devices")
  @Wenn("{word} sich alle devices anschaut")
  public void getDevices(String actor) {
    theActorCalled(actor).attemptsTo(GET_DEVICES.request());
    theActorCalled(actor).remember(LAST_RESPONSE, lastResponse());
  }

  // Claim Device
  @Given("{string} claims a HBA user client at interface {word}")
  @Angenommen("{string} reserviert sich einen Practitioner-Client an Schnittstelle {word}")
  public void reserveClientOnApiAndCreateAccount(String actorName, String apiName) {
    Actor actor = reserveClientOnApi(actorName, apiName);
    checkIs(actor, List.of(MESSENGER_CLIENT, PRACTITIONER));
    logsIn(actorName);
    loginSuccess(actorName);
  }

  @Given("{string} reserves org admin client on api {word}")
  @Angenommen("{string} reserviert sich einen Org-Admin-Client an Schnittstelle {word}")
  public void reserveOrgAdminClientOnApi(String actorName, String apiName) {
    Actor actor = reserveClientOnApi(actorName, apiName);
    checkIs(actor, List.of(ORG_ADMIN));
    logsIn(actorName);
    loginSuccess(actorName);
  }

  @Given("{string} reserves org user client on api {word}")
  @Angenommen("{string} reserviert sich einen Messenger-Client an Schnittstelle {word}")
  public void reserveOrgUserClientOnApi(String actorName, String apiName) {
    Actor actor = reserveClientOnApi(actorName, apiName);
    checkIs(actor, List.of(MESSENGER_CLIENT));
    logsIn(actorName);
    loginSuccess(actorName);
  }

  @Und("{string} meldet sich mit den Daten von {string} an der Schnittstelle {word} an")
  public void reserveClientOnApiAndLoginWithData(String actorName, String userName,
      String apiName) {
    Actor actor = reserveClientOnApi(actorName, apiName);

    String mxId = theActorCalled(userName).recall(MX_ID);
    String password = theActorCalled(userName).recall(ACCOUNT_PASSWORD);
    actor.remember(MX_ID, mxId);
    actor.remember(ACCOUNT_PASSWORD, password);

    actor.attemptsTo(login());
  }

  @Given("{string} claims client on api {word}")
  @Angenommen("{string} reserviert sich einen Test-Client an der Schnittstelle {word}")
  public Actor reserveClientOnApi(String actorName, String apiName) {
    Actor actor = theActorCalled(actorName);
    actor.whoCan(CallAnApi.at(apiName)).entersTheScene();
    addHostToTigerProxy(apiName);
    actor.attemptsTo(claimDevice());
    return actor;
  }

  @When("get all devices")
  @Wenn("die Liste der Geräte abgerufen wird")
  public void getDevicesCurrentActor() {
    withCurrentActor(GET_DEVICES.request());
  }

  @Then("{string} has a claimed device")
  @Dann("prüfe ob {string} ein Gerät reserviert hat")
  public void checkIfDeviceIsClaimedGivenActor(String actorName) {
    Actor actor = theActorCalled(actorName);
    String claimerName = actor.recall(CLAIMER_NAME);
    actor.should(seeThatResponse("Check if a device is claimed",
        res -> res.statusCode(200)
            .body("devices.claimer", hasItem(claimerName))
    ));
  }

  // Assertions
  public static void checkIs(Actor actor, List<ClientKind> kind) {
    InfoObjectDTO info = actor.asksFor(apiInfo());
    if (kind.contains(ORG_ADMIN)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirOrganization())
          .as("Claimed device have no org admin privileges! This information is got from the info endpoint.")
          .isTrue();
    }
    if (kind.contains(MESSENGER_CLIENT)) {
      assertThat(info.getClientInfo().getCanSendMessages())
          .as("Claimed device have no write messages privileges! This information is got from the info endpoint.")
          .isTrue();
    }
    if (kind.contains(PRACTITIONER)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirPractitioner())
          .as("Claimed device have no practitioner privileges! This information is got from the info endpoint.")
          .isTrue();
    }
  }
}
