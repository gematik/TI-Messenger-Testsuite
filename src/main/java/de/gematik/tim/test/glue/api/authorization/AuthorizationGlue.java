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

package de.gematik.tim.test.glue.api.authorization;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.authorization.questions.GetAllowListQuestion.getAllowList;
import static de.gematik.tim.test.glue.api.authorization.questions.GetAuthorizationModeQuestion.getAuthorizationMode;
import static de.gematik.tim.test.glue.api.authorization.questions.GetBlockListQuestion.getBlockList;
import static de.gematik.tim.test.glue.api.authorization.tasks.AddAllowedUsersTask.addAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.AddBlockedUsersTask.addBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteAllowedUsersTask.deleteAllowedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.DeleteBlockedUsersTask.deleteBlockedUsers;
import static de.gematik.tim.test.glue.api.authorization.tasks.SetAuthorizationModeTask.setAuthorizationMode;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.models.AuthorizationListDTO;
import de.gematik.tim.test.models.AuthorizationModeDTO;
import de.gematik.tim.test.models.GroupNameDTO;
import de.gematik.tim.test.models.MxIdDTO;
import de.gematik.tim.test.models.ServerNameDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.lang3.NotImplementedException;

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

  @When("{string} checks, if user {listOfStrings} is in the block list")
  @Wenn("{string} prüft, ob User {listOfStrings} in der Blockliste gesetzt ist")
  public void checkUserOnBlocklist(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO blockList = actor.asksFor(getBlockList());
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      checkResponseCode(actorName, OK.value());
      assertThat(blockList.getMxids()).contains(new MxIdDTO().mxid(blockedUser.recall(MX_ID)));
    }
  }

  @Then("{string} puts the user {listOfStrings} on the block list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Blockliste")
  public void putUserOnBlockList(String actorName, List<String> blockedUserNames) {
    addBlockedMxids(actorName, blockedUserNames);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} tries to put the user {listOfStrings} on the block list")
  @Then("{string} versucht User {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutUserOnBlocklist(String actorName, List<String> userNames) {
    addBlockedMxids(actorName, userNames);
    assertThat(lastResponse().statusCode())
        .as("Operation returned error code " + lastResponse().statusCode())
        .satisfiesAnyOf(
            code -> assertThat(code).isEqualTo(200), code -> assertThat(code).isEqualTo(403));
  }

  @Then("{string} tries to put {listOfStrings} into the block list again")
  @Dann("{string} versucht die User {listOfStrings} in der Blockliste erneut zu hinterlegen")
  public void triesToPutUserOnBlockListAgain(String actorName, List<String> userNames) {
    addBlockedMxids(actorName, userNames);
  }

  private static void addBlockedMxids(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedUsersMxids = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxids.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(addBlockedUsers().withMxIds(blockedUsersMxids));
  }

  @Then("{string} removes the user {listOfStrings} from the block list")
  @Dann("{string} entfernt die User {listOfStrings} in der Blockliste")
  public void deleteUserFromBlockList(String actorName, List<String> blockedUserNames) {
    removeBlockedUsers(actorName, blockedUserNames);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} tries to remove the user {listOfStrings} from the block list")
  @Dann("{string} versucht die User {listOfStrings} in der Blockliste zu entfernen")
  public void tryToDeleteUserFromBlockList(String actorName, List<String> userNames) {
    removeBlockedUsers(actorName, userNames);
  }

  private static void removeBlockedUsers(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedUsersMxIds = new ArrayList<>();
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      blockedUsersMxIds.add(blockedUser.recall(MX_ID));
    }
    actor.attemptsTo(deleteBlockedUsers().withMxIds(blockedUsersMxIds));
  }

  @When("{string} checks, if user {listOfStrings} is in the allow list")
  @Wenn("{string} prüft, ob User {listOfStrings} in der Allowliste gesetzt ist")
  public void checkUserOnAllowlist(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO allowList = actor.asksFor(getAllowList());
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      checkResponseCode(actorName, OK.value());
      assertThat(allowList.getMxids()).contains(new MxIdDTO().mxid(allowedUser.recall(MX_ID)));
    }
  }

  @Then("{string} puts the user {listOfStrings} on the allow list")
  @Dann("{string} hinterlegt die User {listOfStrings} in der Allowliste")
  public void putUserOnAllowList(String actorName, List<String> allowedUserNames) {
    addAllowedMxIds(actorName, allowedUserNames);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} tries to put the user {listOfStrings} on the allow list")
  @Then("{string} versucht User {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutUserOnAllowlist(String actorName, List<String> userNames) {
    addAllowedMxIds(actorName, userNames);
  }

  @Then("{string} tries to put {listOfStrings} into the allow list again")
  @Dann("{string} versucht die User {listOfStrings} in der Allowliste erneut zu hinterlegen")
  public void triesToPutUserOnAllowListAgain(String actorName, List<String> userNames) {
    addAllowedMxIds(actorName, userNames);
  }

  private static void addAllowedMxIds(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedUsersMxIds = new ArrayList<>();
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      allowedUsersMxIds.add(allowedUser.recall(MX_ID));
    }
    actor.attemptsTo(addAllowedUsers().withMxIds(allowedUsersMxIds));
  }

  @Then("{string} removes the user {listOfStrings} from the allow list")
  @Dann("{string} entfernt die User {listOfStrings} in der Allowliste")
  public void deleteUserFromAllowList(String actorName, List<String> allowedUserNames) {
    removeAllowedUsers(actorName, allowedUserNames);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} tries to remove the user {listOfStrings} from the allow list")
  @Dann("{string} versucht die User {listOfStrings} in der Allowliste zu entfernen")
  public void tryToDeleteUsersFromAllowlist(String actorName, List<String> userNames) {
    removeAllowedUsers(actorName, userNames);
  }

  private static void removeAllowedUsers(String actorName, List<String> allowedUserNames) {
    List<String> allowedUsersMxIds = new ArrayList<>();
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      allowedUsersMxIds.add(allowedUser.recall(MX_ID));
    }
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(deleteAllowedUsers().withMxIds(allowedUsersMxIds));
  }

  @When("{string} checks, if the server name of {listOfStrings} is in the block list")
  @Wenn("{string} prüft, ob der Server-Name von {listOfStrings} in der Blockliste gesetzt ist")
  public void checkServerOnBlocklist(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUserNames);
    AuthorizationListDTO blockList = actor.asksFor(getBlockList());
    for (String blockedServerName : blockedServerNames) {
      checkResponseCode(actorName, OK.value());
      assertThat(blockList.getServerNames())
          .contains(new ServerNameDTO().serverName(blockedServerName));
    }
  }

  @Then("{string} puts the server-name of {listOfStrings} on the block list")
  @Dann("{string} hinterlegt den Server-Namen von {listOfStrings} in der Blockliste")
  public void putServerNameInBlockList(String actorName, List<String> blockedUsers) {
    addBlockedServerNames(actorName, blockedUsers);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} tries to put the server-name of {listOfStrings} on the block list")
  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutServerNameOnBlocklist(String actorName, List<String> serverNames) {
    addBlockedServerNames(actorName, serverNames);
  }

  private void addBlockedServerNames(String actorName, List<String> blockedUsers) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUsers);
    actor.attemptsTo(addBlockedUsers().withServerNames(blockedServerNames));
  }

  @Then("{string} removes the server-name of {listOfStrings} from the block list")
  @Dann("{string} entfernt den Server-Namen von {listOfStrings} in der Blockliste")
  public void deleteServerNameInBlockList(String actorName, List<String> blockedUsers) {
    removeBlockedServerNames(actorName, blockedUsers);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} tries to remove the server-name of {listOfStrings} from the block list")
  @Dann("{string} versucht den Server-Namen von {listOfStrings} in der Blockliste zu entfernen")
  public void tryToDeleteServerNameInBlockList(String actorName, List<String> serverNames) {
    removeBlockedServerNames(actorName, serverNames);
  }

  private void removeBlockedServerNames(String actorName, List<String> blockedUsers) {
    Actor actor = theActorCalled(actorName);
    List<String> blockedServerNames = getServerNamesFromUsers(blockedUsers);
    actor.attemptsTo(deleteBlockedUsers().withServerNames(blockedServerNames));
  }

  @When("{string} checks, if the server name of {listOfStrings} is in the allow list")
  @Wenn("{string} prüft, ob der Server-Name von {listOfStrings} in der Allowliste gesetzt ist")
  public void checkServerOnAllowlist(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUserNames);
    AuthorizationListDTO allowList = actor.asksFor(getAllowList());
    for (String allowedServerName : allowedServerNames) {
      checkResponseCode(actorName, OK.value());
      assertThat(allowList.getServerNames())
          .contains(new ServerNameDTO().serverName(allowedServerName));
    }
  }

  @Then("{string} puts the server-name of {listOfStrings} on the allow list")
  @Dann("{string} hinterlegt den Server-Namen von {listOfStrings} in der Allowliste")
  public void putServerNameInAllowList(String actorName, List<String> allowedUsers) {
    addAllowedServerNames(actorName, allowedUsers);
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} tries to put the server-name of {listOfStrings} on the allow list")
  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutServerNameOnAllowlist(String actorName, List<String> serverNames) {
    addAllowedServerNames(actorName, serverNames);
  }

  private void addAllowedServerNames(String actorName, List<String> allowedUsers) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUsers);
    actor.attemptsTo(addAllowedUsers().withServerNames(allowedServerNames));
  }

  @Then("{string} removes the server-name of {listOfStrings} from the allow list")
  @Dann("{string} entfernt den Server-Namen von {listOfStrings} in der Allowliste")
  public void deleteServerNameInAllowList(String actorName, List<String> allowedUsers) {
    removeAllowedServerNames(actorName, allowedUsers);
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} tries to remove the server-name of {listOfStrings} from the allow list")
  @Dann("{string} versucht den Server-Namen von {listOfStrings} in der Allowliste zu entfernen")
  public void tryToDeleteServerNameInAllowList(String actorName, List<String> serverNames) {
    removeAllowedServerNames(actorName, serverNames);
  }

  private void removeAllowedServerNames(String actorName, List<String> allowedUsers) {
    Actor actor = theActorCalled(actorName);
    List<String> allowedServerNames = getServerNamesFromUsers(allowedUsers);
    actor.attemptsTo(deleteAllowedUsers().withServerNames(allowedServerNames));
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

  @When("{string} checks, that the group {string} is found on the block list")
  @Wenn("{string} prüft, dass die Gruppe {string} in der Blockliste gesetzt ist")
  public void checkGroupOnBlocklist(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO blockList = actor.asksFor(getBlockList());
    checkResponseCode(actorName, OK.value());
    assertThat(blockList.getGroupNames()).contains(groupEnum);
  }

  private GroupNameDTO mapGroupName(String groupName) {
    if (!groupName.equals("Versicherte")) {
      throw new NotImplementedException(format("The group with the name %s is unknown", groupName));
    }
    return GroupNameDTO.IS_INSURED_PERSON;
  }

  @When("{string} checks, that the group {string} is NOT found on the block list")
  @Wenn("{string} prüft, dass die Gruppe {string} in der Blockliste nicht gesetzt ist")
  public void checkGroupNotOnBlocklist(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO blockList = actor.asksFor(getBlockList());
    checkResponseCode(actorName, OK.value());
    assertThat(blockList.getGroupNames()).doesNotContain(groupEnum);
  }

  @When("{string} adds the group {string} to the block list")
  @Wenn("{string} hinterlegt die Gruppe {string} in der Blockliste")
  public void addsGroupToBlockList(String actorName, String blockedGroupName) {
    GroupNameDTO groupEnum = mapGroupName(blockedGroupName);
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(addBlockedUsers().withGroups(List.of(groupEnum)));
    checkResponseCode(actorName, OK.value());
  }

  @When("{string} removes the group {string} from the block list")
  @Wenn("{string} entfernt die Gruppe {string} aus der Blockliste")
  public void deletesGroupFromBlockList(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(deleteBlockedUsers().withGroups(List.of(groupEnum)));
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @When("{string} checks, that the group {string} is found on the allow list")
  @Wenn("{string} prüft, dass die Gruppe {string} in der Allowliste gesetzt ist")
  public void checkGroupOnAllowlist(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO allowList = actor.asksFor(getAllowList());
    checkResponseCode(actorName, OK.value());
    assertThat(allowList.getGroupNames()).contains(groupEnum);
  }

  @When("{string} checks, that the group {string} is NOT found on the allow list")
  @Wenn("{string} prüft, dass die Gruppe {string} in der Allowliste nicht gesetzt ist")
  public void checkGroupNotOnAllowlist(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO allowList = actor.asksFor(getAllowList());
    checkResponseCode(actorName, OK.value());
    assertThat(allowList.getGroupNames()).doesNotContain(groupEnum);
  }

  @When("{string} adds the group {string} to the allow list")
  @Wenn("{string} hinterlegt die Gruppe {string} in der Allowliste")
  public void addsGroupToAllowList(String actorName, String allowedGroupName) {
    GroupNameDTO groupEnum = mapGroupName(allowedGroupName);
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(addAllowedUsers().withGroups(List.of(groupEnum)));
    checkResponseCode(actorName, OK.value());
  }

  @When("{string} removes the group {string} from the allow list")
  @Wenn("{string} entfernt die Gruppe {string} aus der Allowliste")
  public void deletesGroupFromAllowList(String actorName, String groupName) {
    GroupNameDTO groupEnum = mapGroupName(groupName);
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(deleteAllowedUsers().withGroups(List.of(groupEnum)));
    checkResponseCode(actorName, NO_CONTENT.value());
  }

  @Then("{string} cannot find {listOfStrings} in their allow list")
  @Dann("{string} findet {listOfStrings} nicht mehr in seiner Allowliste")
  public void dontFindUserInAllowList(String actorName, List<String> allowedUserNames) {
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO allowList = actor.asksFor(getAllowList());
    for (String allowedUserName : allowedUserNames) {
      Actor allowedUser = theActorCalled(allowedUserName);
      checkResponseCode(actorName, OK.value());
      assertThat(allowList.getMxids())
          .doesNotContain(new MxIdDTO().mxid(allowedUser.recall(MX_ID)));
    }
  }

  @Then("{string} cannot find {listOfStrings} in their block list")
  @Dann("{string} findet {listOfStrings} nicht mehr in seiner Blockliste")
  public void dontFindUserInBlockList(String actorName, List<String> blockedUserNames) {
    Actor actor = theActorCalled(actorName);
    AuthorizationListDTO blockList = actor.asksFor(getBlockList());
    for (String blockedUserName : blockedUserNames) {
      Actor blockedUser = theActorCalled(blockedUserName);
      checkResponseCode(actorName, OK.value());
      assertThat(blockList.getMxids())
          .doesNotContain(new MxIdDTO().mxid(blockedUser.recall(MX_ID)));
    }
  }
}
