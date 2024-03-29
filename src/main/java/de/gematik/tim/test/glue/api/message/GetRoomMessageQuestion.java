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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_MESSAGES;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getCreatedMessage;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.models.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class GetRoomMessageQuestion implements Question<Optional<MessageDTO>> {

  private final String msg;
  private final String mxIdSender;
  private Long customTimeout;
  private Long customPollInterval;
  private boolean shouldWaitUntilDeleted;

  public static GetRoomMessageQuestion messageFromSenderWithTextInActiveRoom(String msg, String mxIdSender) {
    return new GetRoomMessageQuestion(getCreatedMessage(msg).getBody(), mxIdSender).shouldWaitUntilDeleted(false);
  }

  public GetRoomMessageQuestion withCustomInterval(Long timeout, Long pollInterval) {
    this.customTimeout = timeout;
    this.customPollInterval = pollInterval;
    return this;
  }

  public GetRoomMessageQuestion shouldWaitUntilDeleted(boolean shouldWait) {
    this.shouldWaitUntilDeleted = shouldWait;
    return this;
  }

  @Override
  public Optional<MessageDTO> answeredBy(Actor actor) {
    try {
      return repeatedRequest(() -> getMessage(actor), "message", customTimeout,
          customPollInterval);
    } catch (ConditionTimeoutException ex) {
      log.error("Search for message from mxid {} with test '{}' did not return expected result", mxIdSender, msg);
      return Optional.empty();
    }
  }

  private Optional<Optional<MessageDTO>> getMessage(Actor actor) {
    actor.attemptsTo(GET_MESSAGES.request());
    List<MessageDTO> messages = List.of(parseResponse(MessageDTO[].class));
    if (shouldWaitUntilDeleted) {
      return dontFindMessage(messages);
    }
    return findMessage(messages);
  }

  private Optional<Optional<MessageDTO>> findMessage(List<MessageDTO> messages) {
    Optional<MessageDTO> message = messages.stream()
        .filter(m -> m.getBody().equals(msg) && requireNonNull(m.getAuthor()).equals(mxIdSender))
        .findFirst();
    return message.isPresent() ? Optional.of(message) : Optional.empty();
  }

  private Optional<Optional<MessageDTO>> dontFindMessage(List<MessageDTO> messages) {
    Optional<MessageDTO> message = messages.stream()
        .filter(m -> m.getBody().equals(msg) && requireNonNull(m.getAuthor()).equals(mxIdSender))
        .findFirst();
    if (message.isPresent()) {
      return Optional.empty();
    }
    return Optional.of(message);
  }
}
