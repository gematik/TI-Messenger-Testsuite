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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.EDIT_MESSAGE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MESSAGE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueMessageTextWithTimestamp;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addMessage;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.MessageContentDTO;
import de.gematik.tim.test.models.MessageDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import org.springframework.http.HttpStatus;

public class EditMessageTask implements Task {

  private String messageId;
  private String message;

  public static EditMessageTask editMessage() {
    return new EditMessageTask();
  }

  public EditMessageTask withMessageId(String messageId) {
    this.messageId = messageId;
    return this;
  }

  public EditMessageTask withMessage(String message) {
    this.message = message;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    MessageContentDTO messageBody =
        new MessageContentDTO().body(createUniqueMessageTextWithTimestamp()).msgtype("m.text");
    actor.attemptsTo(
        EDIT_MESSAGE
            .request()
            .with(req -> req.pathParam(MESSAGE_ID_VARIABLE, this.messageId).body(messageBody)));
    actor.remember(LAST_RESPONSE, lastResponse());
    if (HttpStatus.valueOf(lastResponse().getStatusCode()).is2xxSuccessful()) {
      addMessage(this.message, lastResponse().as(MessageDTO.class));
    }
  }
}
