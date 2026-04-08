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

package de.gematik.tim.test.glue.api.matrix.questions;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.SYNAPSE_VERSIONS_PATH;
import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import de.gematik.tim.test.glue.api.matrix.BuildWithUserAgent;
import de.gematik.tim.test.glue.api.matrix.SynapseVersionInfo;
import de.gematik.tim.test.glue.api.matrix.WithHomeserverForwarding;
import java.util.ArrayList;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.springframework.http.HttpStatus;

public class SynapseVersionQuestion
    implements Question<String>, BuildWithUserAgent, WithHomeserverForwarding {

  public static SynapseVersionQuestion synapseServerVersion() {
    return new SynapseVersionQuestion();
  }

  @Override
  @SneakyThrows
  public String answeredBy(Actor actor) {
    registerHomeserverAddress(actor);
    actor.attemptsTo(
        new TestdriverApiInteraction(
            buildApiCall("GET", SYNAPSE_VERSIONS_PATH), new ArrayList<>()));

    if (HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()) {
      SynapseVersionInfo synapseVersionInfo = parseResponse(SynapseVersionInfo.class);
      individualLog(
          "synapseVersionInfo", actor.abilityTo(CallAnApi.class).resolve(""), synapseVersionInfo);
      return synapseVersionInfo.server_version();
    }
    return "";
  }
}
