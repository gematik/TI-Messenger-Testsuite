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
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.HomeserverSearchResultListDTO;
import io.restassured.specification.RequestSpecification;
import java.util.Optional;
import jxl.common.AssertionFailed;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class HomeserverSearchQuestion implements Question<HomeserverSearchResultListDTO> {

  private String displayName;
  private String mxid;

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

  @Override
  public HomeserverSearchResultListDTO answeredBy(Actor actor) {
    try {
      return repeatedRequest(() -> requestSearchOnHomeserver(actor), "homeserverSearch");
    } catch (ConditionTimeoutException e) {
      log.error("Could not find homeserver", e);
      throw new AssertionFailed(
          format(
              "HomeserverSearch found zero results, expects one though. Expected: displayName: %s or mxid: %s",
              displayName, mxid));
    }
  }

  private Optional<HomeserverSearchResultListDTO> requestSearchOnHomeserver(Actor actor) {
    actor.attemptsTo(SEARCH_ON_HOMESERVER.request().with(this::prepareQuery));
    RawDataStatistics.search();
    actor.remember(LAST_RESPONSE, lastResponse());

    HomeserverSearchResultListDTO res =
        lastResponse().body().as(HomeserverSearchResultListDTO.class);
    return res.getTotalSearchResults() < 1 ? Optional.empty() : Optional.of(res);
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
