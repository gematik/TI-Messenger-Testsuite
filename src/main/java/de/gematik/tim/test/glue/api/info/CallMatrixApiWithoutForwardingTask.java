/*
 * Copyright 2025 gematik GmbH
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

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod;
import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;

@RequiredArgsConstructor
public class CallMatrixApiWithoutForwardingTask implements Task {

  private final String matrixUrl;
  private final String httpMethod;

  public static CallMatrixApiWithoutForwardingTask callMatrixEndpointWithoutForwarding(
      String matrixUrl, String httpMethod) {
    return new CallMatrixApiWithoutForwardingTask(matrixUrl, httpMethod);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(buildApiCall());
  }

  private TestdriverApiInteraction buildApiCall() {
    RestInteraction restInteraction = HttpMethod.valueOf(httpMethod).creator.apply(matrixUrl);
    return new TestdriverApiInteraction(restInteraction, new ArrayList<>());
  }
}
