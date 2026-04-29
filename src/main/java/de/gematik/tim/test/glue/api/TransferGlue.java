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

package de.gematik.tim.test.glue.api;

import static lombok.AccessLevel.PRIVATE;

import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public final class TransferGlue {

  @Then("{word} checks that the presence state of {word} has the value {string} [Retry {long} - {long}]")
  @Dann("{word} prüft den Präsenzstatus von {word} auf den Wert {string} [Retry {long} - {long}]")
  public void checkActualPresenceState(
      String actorName,
      String userName,
      String presenceState,
      Long timeout,
      Long pollInterval) {
    // implement me
  }

  @When("{listOfStrings} set the presence to {string}")
  @Wenn("{listOfStrings} setzt den Präsenzstatus auf {string}")
  @Wenn("{listOfStrings} setzen den Präsenzstatus auf {string}")
  public void setPresenceState(List<String> actorNames, String presenceState) {
    // implement me
  }

  @When("{listOfStrings} check that its own presence state has the value {string} [Retry {long} - {long}]")
  @Wenn("{listOfStrings} prüft den eigenen Präsenzstatus auf den Wert {string} [Retry {long} - {long}]")
  @Wenn("{listOfStrings} prüfen den eigenen Präsenzstatus auf den Wert {string} [Retry {long} - {long}]")
  public void checkOwnPresenceState(
      List<String> actorNames,
      String presenceState,
      Long timeout,
      Long pollIntervall) {
    //implement me
  }
}
