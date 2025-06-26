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

package de.gematik.tim.test.glue.api.rawdata;

import static org.apache.commons.lang3.StringUtils.leftPad;

import java.util.Collection;
import lombok.Data;

@Data
public class RawDataObject {

  private String description;
  private long success;
  private long error;
  private final Collection<String> errors;

  public RawDataObject(String description, RawDataEventCounter eventCounter) {
    this.description = description;
    this.success = eventCounter.getSuccessCount();
    this.error = eventCounter.getErrors().size();
    this.errors = eventCounter.getErrors();
  }

  @Override
  public String toString() {
    return description
        + "\n\t   Erfolgreich:\t"
        + leftPad("" + success, 5)
        + "\n\tFehlgeschlagen:\t"
        + leftPad("" + error, 5)
        + (!errors.isEmpty() ? "\n\t" + errors + "\n\n" : "\n\n");
  }
}
