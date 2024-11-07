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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.authorization.AddAllowedUsersTask.addAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.AddBlockedUsersTask.addBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.DeleteAllowedUsersTask.deleteAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.DeleteBlockedUsersTask.deleteBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.GetAuthorizationModeQuestion.getAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.SetAuthorizationModeTask.setAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.UseAuthorizationAbility.resetOwnAuthorization;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.models.AuthorizationModeDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
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
  @Dann("{listOfStrings} setzen den eigenen Authorization Mode auf {string}")
  public void setAuthMode(List<String> actorNames, String authorizationMode) {
    actorNames.forEach(
        actorName -> {
          Actor actor = theActorCalled(actorName);
          actor.can(resetOwnAuthorization());
          actor.attemptsTo(setAuthorizationMode(authorizationMode));
          checkResponseCode(actorName, OK.value());
        });
  }

  @Then("{string} puts the user {listOfStrings} on the block list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Blockliste")
  public void putUserOnBlockList(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedUsersMxids = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(addBlockedUsers(blockedUsersMxids));
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the user {listOfStrings} from the block list")
  @Dann("{string} entfernt die User {listOfStrings} in der Blockliste")
  public void deleteUserFromBlockList(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedUsersMxids = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(deleteBlockedUsers(blockedUsersMxids));
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} puts the user {listOfStrings} on the allow list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Allowliste")
  public void putUserOnAllowList(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedUsersMxids = new ArrayList<>();
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      allowedUsersMxids.add(allowedUser.recall(MX_ID));
    }
    actor.attemptsTo(addAllowedUsers(allowedUsersMxids));
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the user {listOfStrings} from the allow list")
  @Dann("{string} entfernt die User {listOfStrings} in der Allowliste")
  public void deleteUserFromAllowList(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedUsersMxids = new ArrayList<>();
    for (String blockedUserName : allowedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      allowedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(deleteAllowedUsers(allowedUsersMxids));
    checkResponseCode(actorName, NO_CONTENT.value());
  }
}
