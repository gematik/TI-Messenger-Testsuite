/*
 * Copyright 2025 gematik GmbH
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
 */

package de.gematik.tim.test.glue.api.info;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_WELL_KNOWN_HOMESERVER;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import lombok.Getter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CallMatrixApiTask implements Task {

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(GET_WELL_KNOWN_HOMESERVER.request());
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
