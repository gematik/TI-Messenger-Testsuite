/*
 * Copyright 20023 gematik GmbH
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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_MESSAGES;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.repeatedRequest;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.MessageDTO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@RequiredArgsConstructor
public class GetRoomMessageQuestion implements Question<MessageDTO> {

  private final String msg;
  private final String mxIdSender;
  private Long customTimeout;
  private Long customPollInterval;

  public static GetRoomMessageQuestion messageFromSenderWithTextInActiveRoom(String msg,
      String mxIdSender) {
    return new GetRoomMessageQuestion(msg, mxIdSender);
  }

  public GetRoomMessageQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  @Override
  public MessageDTO answeredBy(Actor actor) {
    try {
      return repeatedRequest(() -> getMessage(actor), "message", customTimeout,
          customPollInterval);
    } catch (ConditionTimeoutException ex) {
      return null;
    }
  }

  private Optional<MessageDTO> getMessage(Actor actor) {
    actor.attemptsTo(GET_MESSAGES.request());
    List<MessageDTO> messages = List.of(lastResponse().body().as(MessageDTO[].class));
    return messages.stream()
        .filter(m -> m.getBody().equals(msg) && requireNonNull(m.getAuthor()).equals(mxIdSender))
        .findFirst();
  }
}
