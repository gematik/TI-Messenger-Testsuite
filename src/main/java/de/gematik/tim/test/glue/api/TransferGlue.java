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

package de.gematik.tim.test.glue.api;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DIRECT_CHAT_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.message.GetRoomMessageQuestion.messageFromSenderWithTextInActiveRoom;
import static lombok.AccessLevel.PRIVATE;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.models.MessageDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import java.util.List;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

  public static final String ERROR_MESSAGE = "Have to be implemented";

  @Then("{string} can not more find {string}")
  @Dann("{string} kann {string} nicht mehr finden in FHIR [Retry {long} - {long}]")
  public void dontFindMorePractitionerInFhir(String actorName, String userName, Long timeout, Long pollInterval) {
    //implement me
  }

  @Then("{string} could not see more message {string} from {string} in chat with {string} [Retry {long} - {long}]")
  @Dann("{string} kann die Nachricht {string} von {string} im Chat mit {string} nicht mehr sehen [Retry {long} - {long}]")
  public void canNotSeeMoreChatMessage(String actorName, String messageText, String messageAuthor,
                                   String chatPartner, Long timeout, Long pollInterval) {
    //implement me
  }

  @Then("{string} could not find more message {string} from {string} in room {string} [Retry {long} - {long}]")
  @Dann("{string} kann die Nachricht {string} von {string} im Raum {string} nicht mehr sehen [Retry {long} - {long}]")
  public void cantFindMoreMessageInRoom(String actorName, String messageText, String userName,
                                    String roomName, Long timeout, Long pollInterval) {
    //implement me
  }
}
