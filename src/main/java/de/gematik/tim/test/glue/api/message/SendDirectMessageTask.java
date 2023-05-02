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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DIRECT_CHAT_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEND_DIRECT_MESSAGE;
import static de.gematik.tim.test.glue.api.room.UseRoomAbility.addRoomToActor;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomQuestion.ownRoom;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.isSameHomeserver;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.DirectMessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class SendDirectMessageTask implements Task {

  private final Actor toActor;
  private final String message;

  public static SendDirectMessageTask sendDirectMessageTo(Actor toActor, String message) {
    return new SendDirectMessageTask(toActor, message);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    String actorMxId = actor.recall(MX_ID);
    String toMxId = toActor.recall(MX_ID);

    DirectMessageDTO directMessage = new DirectMessageDTO()
        .body(this.message)
        .msgtype("m.text")
        .toAccount(toMxId);
    actor.attemptsTo(SEND_DIRECT_MESSAGE.request().with(req -> req.body(directMessage)));

    logEventsAndSaveRoomToActor(actor, actorMxId, toMxId);

  }

  private <T extends Actor> void logEventsAndSaveRoomToActor(T actor, String actorMxId,
      String toMxId) {
    boolean roomAlreadyExist = isNotBlank(actor.recall(DIRECT_CHAT_NAME + toMxId))
        || lastResponse().statusCode() != 200;
    if (isSameHomeserver(toMxId, actorMxId)) {
      RawDataStatistics.exchangeMessageSameHomeserver();
      if (!roomAlreadyExist) {
        RawDataStatistics.inviteToRoomSameHomeserver();
        handleNewRoom(actor, actorMxId, toMxId);
      }
    } else {
      RawDataStatistics.exchangeMessageMultiHomeserver();
      if (!roomAlreadyExist) {
        RawDataStatistics.inviteToRoomMultiHomeserver();
        handleNewRoom(actor, actorMxId, toMxId);
      }
    }
  }

  private void handleNewRoom(Actor actor, String actorMxId, String toMxId) {
    RoomDTO room = actor.asksFor(
        ownRoom().withMembers(List.of(actorMxId, toMxId)));
    actor.remember(DIRECT_CHAT_NAME + toMxId, room.getName());
    toActor.remember(DIRECT_CHAT_NAME + actorMxId, room.getName());
    addRoomToActor(room, actor);
    addRoomToActor(room, toActor);
  }
}
