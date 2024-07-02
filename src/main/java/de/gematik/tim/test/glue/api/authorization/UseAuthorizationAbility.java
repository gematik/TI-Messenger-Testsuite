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

import static de.gematik.tim.test.glue.api.authorization.SetAuthorizationModeTask.setAuthorizationMode;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForTeardown;
import static java.lang.Boolean.TRUE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.teardown.TeardownAbility;
import de.gematik.tim.test.models.AuthorizationModeDTO;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class UseAuthorizationAbility extends TeardownAbility {

  public static UseAuthorizationAbility resetOwnAuthorization() {
    return new UseAuthorizationAbility();
  }

  @Override
  protected void teardownThis() {
    repeatedRequestForTeardown(this::resetAuthorization, actor);
  }

  private Optional<Boolean> resetAuthorization() {
    this.actor.attemptsTo(setAuthorizationMode(AuthorizationModeDTO.ALLOWALL.toString()));
    return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
        ? Optional.of(TRUE)
        : Optional.empty();
  }
}
