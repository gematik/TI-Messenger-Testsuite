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

import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;

@RequiredArgsConstructor
public class CallMatrixApiTask extends WithHomeserverForwardingTask
    implements BuildWithUserAgentTask {

  private final String httpMethod;
  private final String matrixUrl;
  private String accessToken;
  private Map<String, String> requestParameter;

  public static CallMatrixApiTask callMatrixEndpoint(String httpMethod, String matrixUrl) {
    return new CallMatrixApiTask(httpMethod, matrixUrl);
  }

  public CallMatrixApiTask withAccessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  public CallMatrixApiTask withRequestParameter(Map<String, String> requestParameter) {
    this.requestParameter = requestParameter;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    RestInteraction matrixApiCall;
    if (accessToken != null) {
      matrixApiCall = buildApiCall(httpMethod, matrixUrl, accessToken);
    } else {
      matrixApiCall = buildApiCall(httpMethod, matrixUrl);
    }
    if (requestParameter != null) {
      for (Map.Entry<String, String> requestParameterEntry : requestParameter.entrySet()) {
        matrixApiCall.with(
            request ->
                request.params(requestParameterEntry.getKey(), requestParameterEntry.getValue()));
      }
    }
    actor.attemptsTo(new TestdriverApiInteraction(matrixApiCall, new ArrayList<>()));
  }
}
