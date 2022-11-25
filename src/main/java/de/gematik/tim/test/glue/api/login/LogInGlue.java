/*
 * Copyright (c) 2022 gematik GmbH
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

import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.login.LogoutTask.logout;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.When;
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
  public void logsIn(String actorName) {
    theActorCalled(actorName).attemptsTo(login());
  }
}
