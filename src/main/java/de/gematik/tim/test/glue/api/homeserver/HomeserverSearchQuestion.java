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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ON_HOMESERVER;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.repeatedRequest;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.HomeserverSearchResultListDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

public class HomeserverSearchQuestion implements Question<HomeserverSearchResultListDTO> {

  private String displayName;
  private String mxid;
  private Integer minExpected = 1;
  private Long timeout;
  private Long pollIntervall;

  public static HomeserverSearchQuestion userOnHomeserver() {
    return new HomeserverSearchQuestion();
  }

  public HomeserverSearchQuestion withDisplayName(String name) {
    this.displayName = name;
    return this;
  }

  public HomeserverSearchQuestion withMxid(String mxid) {
    this.mxid = mxid;
    return this;
  }

  public HomeserverSearchQuestion withAtLeastExpectedResults(Integer i) {
    this.minExpected = i;
    return this;
  }

  public HomeserverSearchQuestion withCustomPollIntervall(Long timeout, Long pollIntervall) {
    this.timeout = timeout;
    this.pollIntervall = pollIntervall;
    return this;
  }


  @Override
  public HomeserverSearchResultListDTO answeredBy(Actor actor) {
    try {
      return repeatedRequest(
          () -> requestSearchOnHomeserver(actor), "homeserverSearch", timeout, pollIntervall);
    } catch (ConditionTimeoutException ignored) {
      return ((Response) actor.recall(LAST_RESPONSE)).body()
          .as(HomeserverSearchResultListDTO.class);
    }
  }

  private Optional<HomeserverSearchResultListDTO> requestSearchOnHomeserver(Actor actor) {
    actor.attemptsTo(SEARCH_ON_HOMESERVER.request().with(this::prepareQuery));
    RawDataStatistics.search();
    actor.remember(LAST_RESPONSE, lastResponse());

    HomeserverSearchResultListDTO res = lastResponse().body()
        .as(HomeserverSearchResultListDTO.class);
    return requireNonNull(res.getTotalSearchResults()) < minExpected ? Optional.empty()
        : Optional.of(res);
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (isNotBlank(displayName)) {
      request.queryParam("displayName", displayName);
    }
    if (isNotBlank(mxid)) {
      request.queryParam("mxId", mxid);
    }
    return request;
  }
}
