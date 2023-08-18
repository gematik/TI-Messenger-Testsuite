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

package de.gematik.tim.test.glue.api.room.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.FORGET_ROOM;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import net.serenitybdd.screenplay.Actor;
import org.springframework.http.HttpStatus;

public class ForgetRoomTask extends RoomSpecificTask {

  public static ForgetRoomTask forgetRoom() {
    return new ForgetRoomTask();
  }

  public ForgetRoomTask withName(String roomName) {
    return forRoomName(roomName);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    actor.attemptsTo(FORGET_ROOM.request());
    if (lastResponse().statusCode() == HttpStatus.NO_CONTENT.value()) {
      actor.abilityTo(UseRoomAbility.class).removeCurrent();
    }
  }
}