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

package de.gematik.tim.test.glue.api.homeserver;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.homeserver.HomeserverSearchQuestion.userOnHomeserver;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import de.gematik.tim.test.models.HomeserverSearchResultDTO;
import de.gematik.tim.test.models.HomeserverSearchResultListDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import net.serenitybdd.screenplay.Actor;

public class HomeserverSearchGlue {


  @Wenn("{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer")
  public void findsTimUserWhenSearchingOnHomeserver(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    Actor actorToFind = theActorCalled(userName);

    HomeserverSearchResultListDTO res = actor.asksFor(
        userOnHomeserver().withMxid(actorToFind.recall(MX_ID)));

    assertAll(
        () -> assertThat(res.getTotalSearchResults()).isEqualTo(1),
        () -> assertThat(requireNonNull(res.getSearchResults()))
            .extracting(HomeserverSearchResultDTO::getMxId)
            .contains((String) actorToFind.recall(MX_ID)),
        () -> assertThat(requireNonNull(res.getSearchResults()))
            .extracting(HomeserverSearchResultDTO::getDisplayName)
            .contains((String) actorToFind.recall(DISPLAY_NAME)));
  }

  @Wenn("{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten")
  public void findsTimUserWhenSearchingOnHomeserver(
      String actorName, String userName, int beginCutoff, int endCutoff) {
    Actor actor = theActorCalled(actorName);
    Actor actorToFind = theActorCalled(userName);
    String displayName = actorToFind.recall(DISPLAY_NAME);

    HomeserverSearchResultListDTO res = actor.asksFor(
        userOnHomeserver().withDisplayName(
            displayName.substring(beginCutoff, displayName.length() - endCutoff)));
    HomeserverSearchResultDTO res2 = requireNonNull(res.getSearchResults()).stream()
        .filter(r -> requireNonNull(r.getMxId()).equals(actorToFind.recall(MX_ID)))
        .findFirst()
        .orElseThrow();
    assertThat(res2.getDisplayName()).isEqualTo(displayName);
  }

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer nicht")
  public void doesNotFindTIMUserWhenSearchingOnHomeserver(String actorName, String userName) {
    doesNotFindTIMUserWhenSearchingOnHomeserver(actorName, userName, null, null);
  }

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei der Suche im HomeServer nicht [Retry {long} - {long}]")
  public void doesNotFindTIMUserWhenSearchingOnHomeserver(String actorName, String userName,
      Long customTimeout, Long customPollInterval) {
    Actor actor = theActorCalled(actorName);
    Actor shouldNotBeFound = theActorCalled(userName);

    HomeserverSearchResultListDTO res =
        actor.asksFor(userOnHomeserver()
            .withMxid(shouldNotBeFound.recall(MX_ID))
            .withCustomPollIntervall(customTimeout, customPollInterval));

    assertThat(res.getTotalSearchResults()).isZero();
  }

}
