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

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.WELL_KNOWN_HOMESERVER_PATH;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint;
import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;
import org.springframework.http.HttpStatus;

public abstract class WithHomeserverForwardingTask implements Task {

  String USER_AGENT = "X-TIM-User-Agent";

  @Override
  public <T extends Actor> void performAs(T actor) {
    RestInteraction restInteraction =
        TestdriverApiEndpoint.HttpMethod.valueOf("GET").creator.apply(WELL_KNOWN_HOMESERVER_PATH);
    restInteraction.with(request -> request.headers(USER_AGENT, "gematikTestsuite, web"));
    actor.attemptsTo(new TestdriverApiInteraction(restInteraction, new ArrayList<>()));

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
  }
}
