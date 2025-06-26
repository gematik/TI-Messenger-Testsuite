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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ON_HOMESERVER;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForEmptyResult;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.HomeserverSearchResultListDTO;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class NotOnHomeserverSearchQuestion implements Question<Boolean> {

  private String mxid;
  private Long timeout;
  private Long pollIntervall;

  public static NotOnHomeserverSearchQuestion userNotOnHomeserver() {
    return new NotOnHomeserverSearchQuestion();
  }

  public NotOnHomeserverSearchQuestion withMxid(String mxid) {
    this.mxid = mxid;
    return this;
  }

  public NotOnHomeserverSearchQuestion withCustomPollIntervall(Long timeout, Long pollIntervall) {
    this.timeout = timeout;
    this.pollIntervall = pollIntervall;
    return this;
  }

  @Override
  public Boolean answeredBy(Actor actor) {
    try {
      repeatedRequestForEmptyResult(() -> dontFindOnHomeserver(actor), timeout, pollIntervall);
    } catch (ConditionTimeoutException e) {
      log.error("Unexpectedly found on homeserver", e);
      throw new AssertionFailed(format("Should not be found on homeserver with mxId %s", mxid));
    }
    return true;
  }

  private Boolean dontFindOnHomeserver(Actor actor) {
    actor.attemptsTo(
        SEARCH_ON_HOMESERVER.request().with(request -> request.queryParam("mxId", mxid)));
    RawDataStatistics.search();
    actor.remember(LAST_RESPONSE, lastResponse());

    HomeserverSearchResultListDTO res =
        lastResponse().body().as(HomeserverSearchResultListDTO.class);
    return res.getTotalSearchResults() == 0;
  }
}
