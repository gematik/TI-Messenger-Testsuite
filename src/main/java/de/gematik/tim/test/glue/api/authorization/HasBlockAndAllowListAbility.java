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

import static de.gematik.tim.test.glue.api.authorization.DeleteAllowedUsersTask.deleteAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.DeleteBlockedUsersTask.deleteBlockedUsers;
import static java.util.Objects.isNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import java.util.Collections;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class HasBlockAndAllowListAbility extends MultiTargetAbility<String, AuthorizationListEnum> {

  private HasBlockAndAllowListAbility(
      List<String> userMxIds, AuthorizationListEnum authorizationList) {
    for (String userMxId : userMxIds) {
      addAndSetActive(userMxId, authorizationList);
    }
  }

  public static <T extends Actor> void addBlockedUsersToActor(List<String> userMxIds, T actor) {
    HasBlockAndAllowListAbility ability = actor.abilityTo(HasBlockAndAllowListAbility.class);
    if (isNull(ability)) {
      ability = new HasBlockAndAllowListAbility(userMxIds, AuthorizationListEnum.BLOCK);
      actor.can(ability);
    }
    for (String userMxId : userMxIds) {
      ability.addAndSetActive(userMxId, AuthorizationListEnum.BLOCK);
    }
  }

  public static <T extends Actor> void addAllowedUsersToActor(List<String> userMxIds, T actor) {
    HasBlockAndAllowListAbility ability = actor.abilityTo(HasBlockAndAllowListAbility.class);
    if (isNull(ability)) {
      ability = new HasBlockAndAllowListAbility(userMxIds, AuthorizationListEnum.ALLOW);
      actor.can(ability);
    }
    for (String userMxId : userMxIds) {
      ability.addAndSetActive(userMxId, AuthorizationListEnum.ALLOW);
    }
  }

  @Override
  protected Task tearDownPerTarget(String userMxId) {
    setActive(userMxId);
    AuthorizationListEnum authorizationList =
        actor.abilityTo(HasBlockAndAllowListAbility.class).getActiveValue();
    if (authorizationList.equals(AuthorizationListEnum.ALLOW)) {
      return deleteAllowedUsers(Collections.singletonList(userMxId));
    } else {
      return deleteBlockedUsers(Collections.singletonList(userMxId));
    }
  }
}
