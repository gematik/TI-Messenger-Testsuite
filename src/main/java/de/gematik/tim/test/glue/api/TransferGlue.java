/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api;

import io.cucumber.java.de.Dann;

public class TransferGlue {

  public static final String ERROR_MESSAGE = "Have to be implemented";

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Practitioner-Verzeichnis im VZD NICHT")
  public void dontFindUserWithNameInPractitionerVz(String actorName, String userName) {
    //implement me
  }

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im Organisations-Verzeichnis im VZD NICHT")
  public void dontFindUserWithNameInOrgVz(String actorName, String userName) {
    //implement me
  }

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche im VZD NICHT")
  public void dontFindUserWithNameInAllVz(String actorName, String userName) {
    //implement me
  }

}
