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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LOGIN;
import static de.gematik.tim.test.glue.api.login.IsLoggedInAbility.logOut;
import static de.gematik.tim.test.models.AuthStageNameDTO.BASICAUTH;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.AccountDTO;
import de.gematik.tim.test.models.LoginDTO;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class LoginTask implements Task {

  public static LoginTask login() {
    return new LoginTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    Optional<LoginDTO> loginDto = getLoginDto(actor);
    if (loginDto.isPresent()) {
      actor.attemptsTo(LOGIN.request().with(req -> req.body(loginDto.get())));
    } else {
      actor.attemptsTo(LOGIN.request());
    }
    AccountDTO account = lastResponse().body().as(AccountDTO.class);
    actor.remember(MX_ID, account.getMxid());
    actor.remember(ACCOUNT_PASSWORD, account.getPassword());
    actor.can(logOut());

    RawDataStatistics.login();
  }

  private <T extends Actor> Optional<LoginDTO> getLoginDto(T actor) {
    if (actor.recall(MX_ID) != null) {
      return Optional.of(new LoginDTO()
          .authStage(BASICAUTH)
          .username(actor.recall(MX_ID))
          .password(actor.recall(ACCOUNT_PASSWORD)));
    }
    return Optional.empty();
  }

}
