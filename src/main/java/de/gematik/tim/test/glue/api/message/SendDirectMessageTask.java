/*
 * Copyright (c) 2022 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.message;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEND_DIRECT_MESSAGE;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.homeserverFromMxId;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.DirectMessageDTO;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class SendDirectMessageTask implements Task {

  private final String toAccount;
  private final String message;

  public static SendDirectMessageTask sendDirectMessageTo(String toAccount, String message) {
    return new SendDirectMessageTask(toAccount, message);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    DirectMessageDTO directMessage = new DirectMessageDTO()
        .body(this.message)
        .msgtype("m.text")
        .toAccount(this.toAccount);
    actor.attemptsTo(SEND_DIRECT_MESSAGE.request().with(req -> req.body(directMessage)));

    if (homeserverFromMxId(toAccount).equals(homeserverFromMxId(actor.recall(MX_ID)))) {
      RawDataStatistics.exchangeMessageSameHomeserver();
    } else {
      RawDataStatistics.exchangeMessageMultiHomeserver();
    }
  }
}
