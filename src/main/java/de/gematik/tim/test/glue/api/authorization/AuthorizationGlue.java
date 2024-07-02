/*
 * Copyright 2024 gematik GmbH
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
 */

package de.gematik.tim.test.glue.api.authorization;

import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.authorization.GetAuthorizationModeQuestion.getAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.SetAuthorizationModeTask.setAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.UseAuthorizationAbility.resetOwnAuthorization;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.models.AuthorizationModeDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import java.util.List;
import net.serenitybdd.screenplay.Actor;

public class AuthorizationGlue {

  @Then("{listOfStrings} checks, whether the authorization mode is set to {string}")
  @Dann("{listOfStrings} prüft, ob der eigene Authorization Mode auf {string} gesetzt ist")
  public void checkAuthorizationMode(List<String> actorNames, String expectedAuthorizationMode) {
    actorNames.forEach(
        actorName -> {
          Actor actor = theActorCalled(actorName);
          AuthorizationModeDTO authorizationMode = actor.asksFor(getAuthorizationMode());
          checkResponseCode(actorName, OK.value());
          assertThat(authorizationMode.toString()).isEqualTo(expectedAuthorizationMode);
        });
  }

  @Then("{listOfStrings} sets the authorization mode to {string}")
  @Dann("{listOfStrings} setzt den eigenen Authorization Mode auf {string}")
  public void setAuthMode(List<String> actorNames, String authorizationMode) {
    actorNames.forEach(
        actorName -> {
          Actor actor = theActorCalled(actorName);
          actor.can(resetOwnAuthorization());
          actor.attemptsTo(setAuthorizationMode(authorizationMode));
          checkResponseCode(actorName, OK.value());
        });
  }
}
