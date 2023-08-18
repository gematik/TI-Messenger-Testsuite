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

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import lombok.Getter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@Getter
public abstract class RoomSpecificTask implements Task {

  private String roomName;

  protected <T extends RoomSpecificTask> T forRoomName(String roomName) {
    this.roomName = roomName;
    return (T) this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    UseRoomAbility useRoomAbility = actor.abilityTo(UseRoomAbility.class);
    if (isNotBlank(getRoomName())) {
      useRoomAbility.setActive(getRoomName());
    }
    requireNonNull(useRoomAbility.getActive(), "An active Room have to be set");
  }
}
