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

package de.gematik.tim.test.glue.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParallelUtils {

  private static final ObjectMapper oma = new ObjectMapper().registerModule(new JavaTimeModule());

  public static String toJson(Object o) {
    try {
      return oma.writeValueAsString(o);
    } catch (Exception e) {
      throw new TestRunException(e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return oma.readValue(json, clazz);
    } catch (Exception e) {
      throw new TestRunException(e);
    }
  }
}
