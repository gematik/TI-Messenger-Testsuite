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

package de.gematik.tim.test.glue.api.message;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_MESSAGES;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getCreatedMessage;
import static java.lang.String.format;

import de.gematik.tim.test.models.MessageDTO;
import java.util.List;
import java.util.Optional;
import jxl.common.AssertionFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
@RequiredArgsConstructor
public class GetRoomMessageQuestion implements Question<Optional<MessageDTO>> {

  private final String messageText;
  private final String mxIdSender;

  public static GetRoomMessageQuestion messageFromSenderWithTextInActiveRoom(
      String messageText, String mxIdSender) {
    return new GetRoomMessageQuestion(getCreatedMessage(messageText).getBody(), mxIdSender);
  }

  @Override
  public Optional<MessageDTO> answeredBy(Actor actor) {
    try {
      return repeatedRequest(() -> getMessage(actor), "message");
    } catch (ConditionTimeoutException e) {
      log.error(
          "Search for message from mxid {} with text '{}' did not return expected result",
          mxIdSender,
          messageText,
          e);
      throw new AssertionFailed(
          format(
              "Search for message from mxid %s with text '%s' did not return expected result",
              mxIdSender, messageText));
    }
  }

  private Optional<Optional<MessageDTO>> getMessage(Actor actor) {
    actor.attemptsTo(GET_MESSAGES.request());
    List<MessageDTO> messages = List.of(parseResponse(MessageDTO[].class));
    return findMessage(messages);
  }

  private Optional<Optional<MessageDTO>> findMessage(List<MessageDTO> messages) {
    Optional<MessageDTO> foundMessage =
        messages.stream()
            .filter(
                message ->
                    message.getBody().equals(messageText) && message.getAuthor().equals(mxIdSender))
            .findFirst();
    return foundMessage.isPresent() ? Optional.of(foundMessage) : Optional.empty();
  }
}
