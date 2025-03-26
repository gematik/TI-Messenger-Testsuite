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
import static de.gematik.tim.test.glue.api.authorization.GetAllowedServerQuestion.getAllowedServerName;
import static de.gematik.tim.test.glue.api.authorization.GetAllowedUserQuestion.getAllowedUser;
import static de.gematik.tim.test.glue.api.authorization.GetBlockedServerQuestion.getBlockedServerName;
import static de.gematik.tim.test.glue.api.authorization.GetBlockedUserQuestion.getBlockedUser;
import static de.gematik.tim.test.glue.api.authorization.questions.GetAuthorizationModeQuestion.getAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteAllowedServerNamesTask.deleteAllowedServerNames;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteAllowedUsersTask.deleteAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteBlockedServerNamesTask.deleteBlockedServerNames;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteBlockedUsersTask.deleteBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.SetAuthorizationModeTask.setAuthorizationMode;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.authorization.tasks.AddAllowedSeverNamesTask;
import de.gematik.tim.test.glue.api.authorization.tasks.AddAllowedUsersTask;
import de.gematik.tim.test.glue.api.authorization.tasks.AddBlockedServerNamesTask;
import de.gematik.tim.test.glue.api.authorization.tasks.AddBlockedUsersTask;
import de.gematik.tim.test.models.AuthorizationModeDTO;
import de.gematik.tim.test.models.MxIdDTO;
import de.gematik.tim.test.models.ServerNameDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
          actor.attemptsTo(setAuthorizationMode(authorizationMode));
          checkResponseCode(actorName, OK.value());
        });
  }

  @Then("{string} puts the user {listOfStrings} on the block list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Blockliste")
  public void putUserOnBlockList(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    addBlockedUsers(actor, blockedUserNames);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the user {listOfStrings} from the block list")
  @Dann("{string} entfernt die User {listOfStrings} in der Blockliste")
  public void deleteUserFromBlockList(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    removeBlockedUsers(actor, blockedUserNames);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} puts the user {listOfStrings} on the allow list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Allowliste")
  public void putUserOnAllowList(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    addAllowedUsers(actor, allowedUserNames);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the user {listOfStrings} from the allow list")
  @Dann("{string} entfernt die User {listOfStrings} in der Allowliste")
  public void deleteUserFromAllowList(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    removeAllowedUsers(actor, allowedUserNames);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} puts the server-name of {listOfStrings} on the block list")
  @Dann("{string} hinterlegt den Server-Namen von {listOfStrings} in der Blockliste")
  public void putServerNameInBlockList(String actorName, List<String> blockedUsers) {
    Actor actor = theActorCalled(actorName);
    addBlockedServerNames(actor, blockedUsers);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the server-name of {listOfStrings} from the block list")
  @Dann("{string} entfernt den Server-Namen von {listOfStrings} in der Blockliste")
  public void deleteServerNameInBlockList(String actorName, List<String> blockedUsers) {
    Actor actor = theActorCalled(actorName);
    removeBlockedServerNames(actor, blockedUsers);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} puts the server-name of {listOfStrings} on the allow list")
  @Dann("{string} hinterlegt den Server-Namen von {listOfStrings} in der Allowliste")
  public void putServerNameInAllowList(String actorName, List<String> allowedUsers) {
    Actor actor = theActorCalled(actorName);
    addAllowedServerNames(actor, allowedUsers);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} removes the server-name of {listOfStrings} from the allow list")
  @Dann("{string} entfernt den Server-Namen von {listOfStrings} in der Allowliste")
  public void deleteServerNameInAllowList(String actorName, List<String> allowedUsers) {
    Actor actor = theActorCalled(actorName);
    removeAllowedServerNames(actor, allowedUsers);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} tries to put the user {listOfStrings} on the allow list")
  @Then("{string} versucht User {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutUserOnAllowlist(String actorName, List<String> userNames) {
    Actor actor = theActorCalled(actorName);
    addAllowedUsers(actor, userNames);
  }

  @Then("{string} tries to put the user {listOfStrings} on the block list")
  @Then("{string} versucht User {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutUserOnBlocklist(String actorName, List<String> userNames) {
    Actor actor = theActorCalled(actorName);
    addBlockedUsers(actor, userNames);
  }

  @Then("{string} tries to put the server-name of {listOfStrings} on the allow list")
  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutServerNameOnAllowlist(String actorName, List<String> serverNames) {
    Actor actor = theActorCalled(actorName);
    addAllowedServerNames(actor, serverNames);
  }

  @Then("{string} tries to put the server-name of {listOfStrings} on the block list")
  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutServerNameOnBlocklist(String actorName, List<String> serverNames) {
    Actor actor = theActorCalled(actorName);
    addBlockedServerNames(actor, serverNames);
  }

  @Then("{string} tries to remove the user {listOfStrings} from the allow list")
  @Dann("{string} versucht die User {listOfStrings} in der Allowliste zu entfernen")
  public void tryToDeleteUsersFromAllowlist(String actorName, List<String> userNames) {
    Actor actor = theActorCalled(actorName);
    removeAllowedUsers(actor, userNames);
  }

  @Then("{string} tries to remove the user {listOfStrings} from the block list")
  @Dann("{string} versucht die User {listOfStrings} in der Blockliste zu entfernen")
  public void tryToDeleteUserFromBlockList(String actorName, List<String> userNames) {
    Actor actor = theActorCalled(actorName);
    removeBlockedUsers(actor, userNames);
  }

  @Then("{string} tries to remove the server-name of {listOfStrings} from the block list")
  @Dann("{string} versucht den Server-Namen von {listOfStrings} in der Blockliste zu entfernen")
  public void tryToDeleteServerNameInBlockList(String actorName, List<String> serverNames) {
    Actor actor = theActorCalled(actorName);
    removeBlockedServerNames(actor, serverNames);
  }

  @Then("{string} tries to remove the server-name of {listOfStrings} from the allow list")
  @Dann("{string} versucht den Server-Namen von {listOfStrings} in der Allowliste zu entfernen")
  public void tryToDeleteServerNameInAllowList(String actorName, List<String> serverNames) {
    Actor actor = theActorCalled(actorName);
    removeAllowedServerNames(actor, serverNames);
  }

  private static void addBlockedUsers(Actor actor, List<String> blockedUserNames) {
    List<String> blockedUsersMxids = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(AddBlockedUsersTask.addBlockedUsers(blockedUsersMxids));
  }

  private void addBlockedServerNames(Actor actor, List<String> blockedUsers) {
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUsers);
    actor.attemptsTo(AddBlockedServerNamesTask.addBlockedServerNames(blockedServerNames));
  }

  private static void removeBlockedUsers(Actor actor, List<String> blockedUserNames) {
    List<String> blockedUsersMxids = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(deleteBlockedUsers(blockedUsersMxids));
  }

  private void removeBlockedServerNames(Actor actor, List<String> blockedUsers) {
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUsers);
    actor.attemptsTo(deleteBlockedServerNames(blockedServerNames));
  }

  private static void addAllowedUsers(Actor actor, List<String> allowedUserNames) {
    List<String> allowedUsersMxids = new ArrayList<>();
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      allowedUsersMxids.add(allowedUser.recall(MX_ID));
    }
    actor.attemptsTo(AddAllowedUsersTask.addAllowedUsers(allowedUsersMxids));
  }

  private void addAllowedServerNames(Actor actor, List<String> allowedUsers) {
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUsers);
    actor.attemptsTo(AddAllowedSeverNamesTask.addAllowedServerNames(allowedServerNames));
  }

  private static void removeAllowedUsers(Actor actor, List<String> allowedUserNames) {
    List<String> allowedUsersMxids = new ArrayList<>();
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      allowedUsersMxids.add(allowedUser.recall(MX_ID));
    }
    actor.attemptsTo(deleteAllowedUsers(allowedUsersMxids));
  }

  private void removeAllowedServerNames(Actor actor, List<String> allowedUsers) {
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUsers);
    actor.attemptsTo(deleteAllowedServerNames(allowedServerNames));
  }

  private List<String> getServerNamesFromUsers(List<String> authorizationUsers) {
    List<String> serverNames = new ArrayList<>();
    for (String authorizationUser : authorizationUsers) {
      Actor authorizedActor = theActorCalled(authorizationUser);
      String mxId = authorizedActor.recall(MX_ID);
      String serverName = extractServerNameFromMxId(mxId);
      serverNames.add(serverName);
    }
    return serverNames;
  }

  private String extractServerNameFromMxId(String mxId) {
    if (mxId == null || !mxId.contains(":")) {
      throw new IllegalArgumentException(mxId + " is not a valid MxId");
    }
    return mxId.split(":")[1];
  }

  @When("{string} checks, if user {listOfStrings} is in the block list")
  @Wenn("{string} prüft, ob User {listOfStrings} in der Blockliste gesetzt ist")
  public void checkUserOnBlocklist(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      MxIdDTO blockedUserMxid = actor.asksFor(getBlockedUser(blockedUser.recall(MX_ID)));
      checkResponseCode(actorName, OK.value());
      assertThat(blockedUserMxid.getMxid()).isEqualTo(blockedUser.recall(MX_ID));
    }
  }

  @When("{string} checks, if user {listOfStrings} is in the allow list")
  @Wenn("{string} prüft, ob User {listOfStrings} in der Allowliste gesetzt ist")
  public void checkUserOnAllowlist(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      MxIdDTO allowedUserMxid = actor.asksFor(getAllowedUser(allowedUser.recall(MX_ID)));
      checkResponseCode(actorName, OK.value());
      assertThat(allowedUserMxid.getMxid()).isEqualTo(allowedUser.recall(MX_ID));
    }
  }

  @When("{string} checks, if the server name of {listOfStrings} is in the block list")
  @Wenn("{string} prüft, ob der Server-Name von {listOfStrings} in der Blockliste gesetzt ist")
  public void checkServerOnBlocklist(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUserNames);
    for (String blockedServerName : blockedServerNames) {
      ServerNameDTO serverNameResponse = actor.asksFor(getBlockedServerName(blockedServerName));
      checkResponseCode(actorName, OK.value());
      assertThat(serverNameResponse.getServerName()).isEqualTo(blockedServerName);
    }
  }

  @When("{string} checks, if the server name of {listOfStrings} is in the allow list")
  @Wenn("{string} prüft, ob der Server-Name von {listOfStrings} in der Allowliste gesetzt ist")
  public void checkServerOnAllowlist(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUserNames);
    for (String allowedServerName : allowedServerNames) {
      ServerNameDTO serverNameResponse = actor.asksFor(getAllowedServerName(allowedServerName));
      checkResponseCode(actorName, OK.value());
      assertThat(serverNameResponse.getServerName()).isEqualTo(allowedServerName);
    }
  }
}
