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

package de.gematik.tim.test.glue.api.login;

import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.login.LogoutTask.logout;
import static de.gematik.tim.test.glue.api.login.LogoutWithSyncTask.logoutWithSync;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.CLAIM_PARALLEL;
import static java.lang.Boolean.FALSE;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.devices.DevicesControllerGlue;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;

public class LoginGlue {

  @When("{string} logs in")
  @Wenn("{string} loggt sich im TI-Messenger ein")
  public static void logsIn(String actorName) {
    Actor actor = theActorCalled(actorName);
    logsIn(actor);
  }

  public static void logsIn(Actor actor) {
    login().withActor(actor).run();
    checkResponseCode(actor.getName(), OK.value());
  }

  @Then("registration successful for {string}")
  @Dann("ist das Login für {string} erfolgreich")
  public static void loginSuccess(String actorName) {
    if (FALSE.equals(CLAIM_PARALLEL) || !DevicesControllerGlue.isAllowParallelClaim()) {
      checkResponseCode(actorName, OK.value());
    }
  }

  @When("{string} logs out")
  @Wenn("{string} loggt sich im TI-Messenger aus")
  public void logsOut(String actorName) {
    theActorCalled(actorName).attemptsTo(logout());
    checkResponseCode(actorName, OK.value());
  }

  @When("{string} logs out including and waits for full sync")
  @Wenn("{string} loggt sich im TI-Messenger aus inkl. erfolgreichem Sync")
  public void fullySyncedLogOut(String actorName) {
    theActorCalled(actorName).attemptsTo(logoutWithSync());
    checkResponseCode(actorName, OK.value());
  }
}
