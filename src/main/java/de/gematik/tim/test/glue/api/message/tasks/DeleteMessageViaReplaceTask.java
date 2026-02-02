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

package de.gematik.tim.test.glue.api.message.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_REPLACE_MESSAGE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MESSAGE_ID_VARIABLE;

import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class DeleteMessageViaReplaceTask implements Task {

  private final String messageId;

  public static DeleteMessageViaReplaceTask deleteReplaceMessageWithId(String messageId) {
    return new DeleteMessageViaReplaceTask(messageId);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(
        DELETE_REPLACE_MESSAGE
            .request()
            .with(request -> request.pathParam(MESSAGE_ID_VARIABLE, this.messageId)));
  }
}
