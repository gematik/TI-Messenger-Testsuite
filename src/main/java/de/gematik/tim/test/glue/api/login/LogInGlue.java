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

package de.gematik.tim.test.glue.api.login;

import static de.gematik.tim.test.glue.api.devices.ClientKind.MESSENGER_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.ORG_ADMIN;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRACTITIONER;
import static de.gematik.tim.test.glue.api.devices.DevicesControllerGlue.checkIs;
import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.login.LogoutTask.logout;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;

public class LogInGlue {

  @Before
  public void setup() {
    setTheStage(Cast.ofStandardActors());
  }

  @After
  public void teardown() {
    stage().drawTheCurtain();
  }

  @When("{string} logs out")
  @Wenn("{string} loggt sich im TI-Messenger aus")
  public void logsOut(String actorName) {
    theActorCalled(actorName).attemptsTo(logout());
  }

  @When("{string} logs in")
  @Wenn("{string} loggt sich im TI-Messenger ein")
  public static void logsIn(String actorName) {
    theActorCalled(actorName).attemptsTo(login());
  }

  @Wenn("{string} sich als HBA-User einloggt")
  public void logsInAsHbaUser(String actorName) {
    Actor actor = theActorCalled(actorName);
    logsIn(actorName);
    checkIs(actor, List.of(MESSENGER_CLIENT, PRACTITIONER));
  }

  @Wenn("{string} sich als Org-User einloggt")
  public void logsInAsOrgUser(String actorName) {
    Actor actor = theActorCalled(actorName);
    logsIn(actorName);
    checkIs(actor, List.of(MESSENGER_CLIENT));
  }

  @Wenn("{string} sich als OrgAdmin einloggt")
  public void sichAlsOrgAdminRegistriert(String actorName) {
    Actor actor = theActorCalled(actorName);
    logsIn(actorName);
    checkIs(actor, List.of(ORG_ADMIN));
  }

  @Then("registration successful for {string}")
  @Dann("ist das Login für {string} erfolgreich")
  public static void loginSuccess(String actorName) {
    theActorCalled(actorName).should(seeThatResponse(res -> res.statusCode(200)));
  }

  @Then("registration failed for {string}")
  @Dann("schlägt das Login für {string} fehl")
  public static void loginFailure(String actorName) {
    theActorCalled(actorName).should(
        seeThatResponse(res -> res.statusCode(greaterThanOrEqualTo(400))));
  }
}
