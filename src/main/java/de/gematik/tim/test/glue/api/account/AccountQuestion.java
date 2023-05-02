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

package de.gematik.tim.test.glue.api.account;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_ACCOUNT;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.AccountDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class AccountQuestion implements Question<AccountDTO> {

  public static AccountQuestion ownAccountInfos() {
    return new AccountQuestion();
  }

  @Override
  public AccountDTO answeredBy(Actor actor) {
    actor.attemptsTo(GET_ACCOUNT.request());
    return lastResponse().body().as(AccountDTO.class);
  }
}