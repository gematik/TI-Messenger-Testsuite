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

package de.gematik.tim.test.glue.api.account;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_ACCOUNT;
import static de.gematik.tim.test.glue.api.account.CanDeleteAccountAbility.deleteHisAccount;
import static de.gematik.tim.test.models.AuthStageNameDTO.BASICAUTH;
import static java.util.UUID.randomUUID;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.AccountDTO;
import de.gematik.tim.test.models.CreateAccountRequestDTO;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.apache.commons.lang3.StringUtils;

public class CreateAccountTask implements Task {

  private String displayName;
  private String organization;
  private String password;

  public static CreateAccountTask createAccount() {
    return new CreateAccountTask();
  }

  public CreateAccountTask withDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public CreateAccountTask withOrganization(String organization) {
    this.organization = organization;
    return this;
  }

  public CreateAccountTask withPassword(String password) {
    this.password = password;
    return this;
  }

  public CreateAccountTask and() {
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    String username = randomUUID().toString().substring(0,6);
    String pw =
        StringUtils.isBlank(this.password) ? randomUUID().toString() : this.password;

    CreateAccountRequestDTO requestAccount = new CreateAccountRequestDTO()
        .authType(BASICAUTH)
        .username(username)
        .password(pw)
        .displayName(displayName)
        .organization(organization);

    actor.attemptsTo(CREATE_ACCOUNT.request()
        .with(req -> req.body(requestAccount)));

    rememberMxid(actor);
    actor.remember(ACCOUNT_PASSWORD, pw);
    actor.can(deleteHisAccount());
  }

  private <T extends Actor> void rememberMxid(T actor) {
    Response response = lastResponse();
    if (response.statusCode() == 201) {
      String mxid = response.body().as(AccountDTO.class).getMxid();
      actor.remember(MX_ID, mxid);
    }
  }
}
