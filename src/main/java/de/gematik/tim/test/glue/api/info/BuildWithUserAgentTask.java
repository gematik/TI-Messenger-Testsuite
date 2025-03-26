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

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint;
import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import java.util.List;

import net.serenitybdd.screenplay.rest.interactions.RestInteraction;

public interface BuildWithUserAgentTask {

  String USER_AGENT = "X-TIM-User-Agent";

  default RestInteraction buildApiCall(String httpMethod, String matrixUrl) {
    RestInteraction restInteraction =
        TestdriverApiEndpoint.HttpMethod.valueOf(httpMethod).creator.apply(matrixUrl);
    restInteraction.with(request -> request.headers(USER_AGENT, "gematikTestsuite, web"));
    return restInteraction;
  }

  default RestInteraction buildApiCall(String httpMethod, String matrixUrl, String accessToken) {
    RestInteraction restInteraction =
        TestdriverApiEndpoint.HttpMethod.valueOf(httpMethod).creator.apply(matrixUrl);
    restInteraction.with(
        request ->
            request.headers(
                USER_AGENT, "gematikTestsuite, web", "Authorization", "Bearer " + accessToken));
    return restInteraction;
  }
}
