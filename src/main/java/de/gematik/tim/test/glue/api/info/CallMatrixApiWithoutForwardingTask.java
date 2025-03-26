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

import de.gematik.tim.test.glue.api.TestdriverApiInteraction;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class CallMatrixApiWithoutForwardingTask implements Task, BuildWithUserAgentTask {

  private final String httpMethod;
  private final String matrixUrl;

  public static CallMatrixApiWithoutForwardingTask callMatrixEndpointWithoutForwarding(
      String httpMethod, String matrixUrl) {
    return new CallMatrixApiWithoutForwardingTask(httpMethod, matrixUrl);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(
        new TestdriverApiInteraction(buildApiCall(httpMethod, matrixUrl), new ArrayList<>()));
  }
}
