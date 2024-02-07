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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_INFO;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.threading.ClientFactory.getClient;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.fromJson;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;

import de.gematik.tim.test.glue.api.threading.ParallelQuestionRunner;
import de.gematik.tim.test.models.InfoObjectDTO;
import kong.unirest.UnirestInstance;
import net.serenitybdd.screenplay.Actor;

public class ApiInfoQuestion extends ParallelQuestionRunner<InfoObjectDTO> {

  public static ApiInfoQuestion apiInfo() {
    return new ApiInfoQuestion();
  }

  @Override
  public InfoObjectDTO answeredBy(Actor actor) {
    actor.attemptsTo(
        GET_INFO.request().with(res -> res.header(TEST_CASE_ID_HEADER, getTestcaseId())));
    InfoObjectDTO info = parseResponse(InfoObjectDTO.class);
    actor.remember(HOME_SERVER, info.getHomeserver());
    return info;
  }

  @Override
  public InfoObjectDTO searchParallel() {
    UnirestInstance client = getClient();
    InfoObjectDTO info = fromJson(client.get(GET_INFO.getResolvedPath(actor)).asString().getBody(), InfoObjectDTO.class);
    actor.remember(HOME_SERVER, info.getHomeserver());
    return info;
  }
}
