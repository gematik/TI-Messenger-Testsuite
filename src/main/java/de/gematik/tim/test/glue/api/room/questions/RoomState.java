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

package de.gematik.tim.test.glue.api.room.questions;

import lombok.Getter;

@Getter
public enum RoomState {
  TIM_DEFAULT("de.gematik.tim.room.default.v1", null),
  TIM_ROOM_TYPE("m.room.create", new Content("$.type", "de.gematik.tim.roomtype.default.v1")),
  TIM_ROOM_NAME("de.gematik.tim.room.name", null),
  TIM_ROOM_TOPIC("de.gematik.tim.room.topic", null),
  ROOM_NAME("m.room.name", null),
  ROOM_TOPIC("m.room.topic", null);

  private final String type;
  private final Content content;

  RoomState(String type, Content content) {
    this.type = type;
    this.content = content;
  }

  public record Content(String jsonPath, String value) {}
}
