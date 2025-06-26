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

package de.gematik.tim.test.glue.api.homeserver;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.homeserver.HomeserverSearchQuestion.userOnHomeserver;
import static de.gematik.tim.test.glue.api.homeserver.NotOnHomeserverSearchQuestion.userNotOnHomeserver;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.models.HomeserverSearchResultDTO;
import de.gematik.tim.test.models.HomeserverSearchResultListDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;

public class HomeserverSearchGlue {

  @When("{string} can find the TI-Messenger user {string} when searching in the homeserver")
  @Wenn("{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer")
  public void findsTimUserWhenSearchingOnHomeserver(String actorName, String userName) {

    Actor actor = theActorCalled(actorName);
    Actor actorToFind = theActorCalled(userName);

    HomeserverSearchResultListDTO res =
        actor.asksFor(userOnHomeserver().withMxid(actorToFind.recall(MX_ID)));

    assertThat(res.getTotalSearchResults())
        .withFailMessage("HomeserverSearch expected one item, but found more")
        .isEqualTo(1);
    assertThat(res.getSearchResults())
        .as("HomeserverSearch expected mxId not found")
        .extracting(HomeserverSearchResultDTO::getMxId)
        .contains((String) actorToFind.recall(MX_ID));
    assertThat(res.getSearchResults())
        .as("HomeserverSearch expected displayName not found")
        .extracting(HomeserverSearchResultDTO::getDisplayName)
        .contains((String) actorToFind.recall(DISPLAY_NAME));
  }

  @When(
      "{string} can find the TI-Messenger user {string} when searching in the homeserver by name omitting {int}-{int} \\(number of chars front-back)")
  @Wenn(
      "{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten")
  public void findsTimUserWhenSearchingOnHomeserver(
      String actorName, String userName, int beginCutoff, int endCutoff) {
    Actor actor = theActorCalled(actorName);
    Actor actorToFind = theActorCalled(userName);
    String displayName = actorToFind.recall(DISPLAY_NAME);
    String cuttedDisplayName = displayName.substring(beginCutoff, displayName.length() - endCutoff);

    HomeserverSearchResultListDTO resultList =
        actor.asksFor(userOnHomeserver().withDisplayName(cuttedDisplayName));
    HomeserverSearchResultDTO searchResult =
        resultList.getSearchResults().stream()
            .filter(result -> result.getMxId().equals(actorToFind.recall(MX_ID)))
            .findFirst()
            .orElseThrow(
                () ->
                    new TestRunException(
                        "Could not find any endpoint with cutted display name: %s -> cutted to %s"
                            .formatted(displayName, cuttedDisplayName)));
    assertThat(searchResult.getDisplayName()).isEqualTo(displayName);
  }

  @Then(
      "{string} cannot find TI-Messenger user {string} when searching in the homeserver [retry {long} - {long}]")
  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer nicht [Retry {long} - {long}]")
  public void doesNotFindTIMUserWhenSearchingOnHomeserver(
      String actorName, String userName, Long customTimeout, Long customPollInterval) {
    Actor actor = theActorCalled(actorName);
    Actor shouldNotBeFound = theActorCalled(userName);
    actor.asksFor(
        userNotOnHomeserver()
            .withMxid(shouldNotBeFound.recall(MX_ID))
            .withCustomPollIntervall(customTimeout, customPollInterval));
  }
}
