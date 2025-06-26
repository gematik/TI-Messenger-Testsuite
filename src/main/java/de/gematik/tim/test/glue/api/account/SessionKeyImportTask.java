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

package de.gematik.tim.test.glue.api.account;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.IMPORT_SESSION_KEY;

import de.gematik.tim.test.models.MatrixKeyInfoDTO;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@AllArgsConstructor
public class SessionKeyImportTask implements Task {

  private String key;

  public static SessionKeyImportTask importKey(String key) {
    return new SessionKeyImportTask(key);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    MatrixKeyInfoDTO keyInfo = new MatrixKeyInfoDTO();
    keyInfo.setKey(this.key);
    actor.attemptsTo(IMPORT_SESSION_KEY.request().with(r -> r.body(keyInfo)));
  }
}
