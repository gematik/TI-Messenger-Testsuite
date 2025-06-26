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

package de.gematik.tim.test.glue.api.authorization.tasks;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.ADD_ALLOWED_USERS;
import static de.gematik.tim.test.glue.api.authorization.HasBlockAndAllowListAbility.actorHasAllowList;

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class AddAllowedUsersTask extends AuthorizationListTask implements Task {

  public static AddAllowedUsersTask addAllowedUsers() {
    return new AddAllowedUsersTask(ADD_ALLOWED_USERS);
  }

  public AddAllowedUsersTask(TestdriverApiEndpoint endpoint) {
    super(endpoint);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actorHasAllowList(actor.recall(MX_ID), actor);
  }
}
