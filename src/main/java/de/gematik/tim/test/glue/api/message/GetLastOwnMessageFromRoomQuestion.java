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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.message.GetRoomMessagesQuestion.messagesInActiveRoom;
import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.models.MessageDTO;
import java.util.List;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class GetLastOwnMessageFromRoomQuestion implements Question<MessageDTO> {

  public static GetLastOwnMessageFromRoomQuestion lastOwnMessage() {
    return new GetLastOwnMessageFromRoomQuestion();
  }

  @Override
  public MessageDTO answeredBy(Actor actor) {
    String actorId = actor.recall(MX_ID);

    List<MessageDTO> messages = actor.asksFor(messagesInActiveRoom());

    return messages.stream()
        .filter(msg -> actorId.equals(msg.getAuthor()))
        .filter(msg -> nonNull(msg.getTimestamp()))
        .max(comparing(MessageDTO::getTimestamp))
        .orElseThrow();
  }
}
