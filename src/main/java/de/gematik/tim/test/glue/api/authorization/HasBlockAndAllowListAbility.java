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

import static de.gematik.tim.test.glue.api.authorization.questions.GetAllowListQuestion.getAllowList;
import static de.gematik.tim.test.glue.api.authorization.questions.GetAuthorizationModeQuestion.getAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.questions.GetBlockListQuestion.getBlockList;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteAllowedUsersTask.deleteAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteBlockedUsersTask.deleteBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.SetAuthorizationModeTask.setAuthorizationMode;
import static java.util.Objects.isNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.models.AuthorizationListDTO;
import de.gematik.tim.test.models.AuthorizationModeDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class HasBlockAndAllowListAbility extends MultiTargetAbility<String, AuthorizationListEnum> {

  private HasBlockAndAllowListAbility(String actorMxId, AuthorizationListEnum authorizationList) {
    addAndSetActive(actorMxId, authorizationList);
  }

  public static <T extends Actor> void actorHasBlockList(String actorMxId, T actor) {
    HasBlockAndAllowListAbility ability = actor.abilityTo(HasBlockAndAllowListAbility.class);
    if (isNull(ability)) {
      ability = new HasBlockAndAllowListAbility(actorMxId, AuthorizationListEnum.BLOCK);
      actor.can(ability);
    }
    ability.addAndSetActive(actorMxId, AuthorizationListEnum.BLOCK);
  }

  public static <T extends Actor> void actorHasAllowList(String actorMxId, T actor) {
    HasBlockAndAllowListAbility ability = actor.abilityTo(HasBlockAndAllowListAbility.class);
    if (isNull(ability)) {
      ability = new HasBlockAndAllowListAbility(actorMxId, AuthorizationListEnum.ALLOW);
      actor.can(ability);
    }
    ability.addAndSetActive(actorMxId, AuthorizationListEnum.ALLOW);
  }

  @Override
  protected Task tearDownPerTarget(String actorMxId) {
    setActive(actorMxId);
    AuthorizationModeDTO authorizationModeDTO = actor.asksFor(getAuthorizationMode());
    AuthorizationListEnum authorizationListEnum =
        actor.abilityTo(HasBlockAndAllowListAbility.class).getActiveValue();
    if (authorizationListEnum == AuthorizationListEnum.ALLOW) {
      if (authorizationModeDTO.getValue().equals("AllowAll")) {
        actor.attemptsTo(setAuthorizationMode("BlockAll"));
      }
      AuthorizationListDTO allowList = actor.asksFor(getAllowList());
      return deleteAllowedUsers()
          .withMxIdsAsDTO(allowList.getMxids())
          .withServerNamesAsDTO(allowList.getServerNames())
          .withGroups(allowList.getGroupNames());
    } else {
      if (authorizationModeDTO.getValue().equals("BlockAll")) {
        actor.attemptsTo(setAuthorizationMode("AllowAll"));
      }
      AuthorizationListDTO blockList = actor.asksFor(getBlockList());
      return deleteBlockedUsers()
          .withMxIdsAsDTO(blockList.getMxids())
          .withServerNamesAsDTO(blockList.getServerNames())
          .withGroups(blockList.getGroupNames());
    }
  }
}
