/*
 * Copyright (Change Date see Readme), gematik GmbH
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
 *
 * ******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.tim.test.glue.api.login;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LOGOUT;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class LogoutTask implements Task {

  public static LogoutTask logout() {
    return new LogoutTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    if ((boolean) actor.recall(IS_LOGGED_IN)) {
      actor.attemptsTo(LOGOUT.request());
      actor.remember(IS_LOGGED_IN, false);
    }
  }
}
