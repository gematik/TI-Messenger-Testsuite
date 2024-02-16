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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_SUPPORTED_VERSIONS;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_WELL_KNOWN_MATRIX_SERVER;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.springframework.http.HttpStatus;

public class SupportedMatrixVersionQuestion implements Question<SupportedMatrixVersionInfo> {

  public static SupportedMatrixVersionQuestion matrixSupportedServerVersion() {
    return new SupportedMatrixVersionQuestion();
  }

  @Override
  @SneakyThrows
  public SupportedMatrixVersionInfo answeredBy(Actor actor) {
    actor.attemptsTo(GET_WELL_KNOWN_MATRIX_SERVER.request());
    if (HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()) {
      String url = lastResponse().jsonPath().getString("'m.server'");
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://" + url;
      }
      actor.can(CallAnApi.at(url));
    }
    actor.attemptsTo(GET_SUPPORTED_VERSIONS.request());

    SupportedMatrixVersionInfo supportedMatrixVersionInfo = parseResponse(SupportedMatrixVersionInfo.class);
    actor.remember(MATRIX_CLIENT_VERSION, supportedMatrixVersionInfo);
    individualLog("supportedMatrixVersion", actor.abilityTo(CallAnApi.class).resolve(""), supportedMatrixVersionInfo);
    return supportedMatrixVersionInfo;
  }
}
