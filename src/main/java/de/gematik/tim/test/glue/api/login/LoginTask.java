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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LOGIN;
import static de.gematik.tim.test.models.AuthStageNameDTO.BASICAUTH;

import de.gematik.tim.test.models.LoginDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.apache.commons.lang3.StringUtils;

public class LoginTask implements Task {

  private String mxid;
  private String password;

  public static LoginTask login() {
    return new LoginTask();
  }

  public LoginTask withMxid(String mxid) {
    this.mxid = mxid;
    return this;
  }

  public LoginTask withPassword(String password) {
    this.password = password;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    String finalMxid = StringUtils.isEmpty(this.mxid) ? actor.recall(MX_ID) : this.mxid;
    String finalPassword = StringUtils.isEmpty(this.password) ? ACCOUNT_PASSWORD : this.password;
    LoginDTO loginDTO = new LoginDTO()
        .authStage(BASICAUTH)
        .username(finalMxid)
        .password(finalPassword);
    actor.attemptsTo(LOGIN.request().with(req -> req.body(loginDTO)));

  }

}
