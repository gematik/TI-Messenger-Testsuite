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

package de.gematik.tim.test.glue.api.message;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEND_DIRECT_MESSAGE;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.isSameHomeserver;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.DirectMessageDTO;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@AllArgsConstructor
public class SendDirectMessageToMxIdTask implements Task {

  private final String mxId;
  private final String message;

  public static SendDirectMessageToMxIdTask sendDirectMessageToMxIdOutOfFederation(
      String mxId, String msg) {
    return new SendDirectMessageToMxIdTask(mxId, msg);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    DirectMessageDTO directMessage =
        new DirectMessageDTO().body(this.message).msgtype("m.text").toAccount(mxId);
    actor.attemptsTo(SEND_DIRECT_MESSAGE.request().with(req -> req.body(directMessage)));

    logEventsAndSaveRoomToActor(actor);
  }

  private <T extends Actor> void logEventsAndSaveRoomToActor(T actor) {
    boolean isSameHomeserver = isSameHomeserver(mxId, actor.recall(MX_ID));
    if (isSameHomeserver) {
      RawDataStatistics.exchangeMessageSameHomeserver();
    } else {
      RawDataStatistics.exchangeMessageMultiHomeserver();
    }
  }
}
