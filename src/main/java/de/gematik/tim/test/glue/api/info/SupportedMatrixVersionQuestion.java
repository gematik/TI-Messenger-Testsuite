/*
 * Copyright 2023 gematik GmbH
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

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MATRIX_CLIENT_VERSION;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.SUPPORTED_VERSIONS_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.WELL_KNOWN_HOMESERVER_PATH;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.springframework.http.HttpStatus;

public class SupportedMatrixVersionQuestion
    implements Question<SupportedMatrixVersionInfo>, BuildWithUserAgentTask {

  public static SupportedMatrixVersionQuestion matrixSupportedServerVersion() {
    return new SupportedMatrixVersionQuestion();
  }

  @Override
  @SneakyThrows
  public SupportedMatrixVersionInfo answeredBy(Actor actor) {
    actor.attemptsTo(
        new TestdriverApiInteraction(
            buildApiCall("GET", WELL_KNOWN_HOMESERVER_PATH), new ArrayList<>()));
    if (HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()) {
      String url = lastResponse().jsonPath().getString("'m.homeserver'.base_url");
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://" + url;
      }
      if (url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      actor.can(CallAnApi.at(url));
    }
    actor.attemptsTo(
        new TestdriverApiInteraction(
            buildApiCall("GET", SUPPORTED_VERSIONS_PATH), new ArrayList<>()));

    SupportedMatrixVersionInfo supportedMatrixVersionInfo =
        parseResponse(SupportedMatrixVersionInfo.class);
    actor.remember(MATRIX_CLIENT_VERSION, supportedMatrixVersionInfo);
    individualLog(
        "supportedMatrixVersion",
        actor.abilityTo(CallAnApi.class).resolve(""),
        supportedMatrixVersionInfo);
    return supportedMatrixVersionInfo;
  }
}
