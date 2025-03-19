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

package de.gematik.tim.test.glue.api;

import static lombok.AccessLevel.PRIVATE;

import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

  @Dann(
      "{listOfStrings} hinterlegt auf dem HomeServer sein Dehydrated Device inkl speichern des Schlüsselmaterials")
  @Dann(
      "{listOfStrings} hinterlegen auf dem HomeServer ihre Dehydrated Device inkl speichern des Schlüsselmaterials")
  public void postDehydratedDevice(List<String> actorNames) {
    // implement me
  }

  @Dann("{listOfStrings} haben {string} Dehydrated Device auf dem HomeServer hinterlegt")
  @Dann("{listOfStrings} hat {string} Dehydrated Device auf dem HomeServer hinterlegt")
  public void getDehydratedDevices(List<String> actorNames, String keineODERein) {
    // implement me
  }

  @Dann("{listOfStrings} holt sich entschlüsselt sein Dehydrated Device vom HomeServer ab")
  @Dann("{listOfStrings} holen sich entschlüsselt ihre Dehydrated Device vom HomeServer ab")
  public void getBackDehydratedDevices(List<String> actorNames) {
    // implement me
  }

  @Dann("{string} findet {listOfStrings} in FHIR über die FDV-Schnittstelle")
  public void findPractitionFromEpaClient(String actor, List<String> actorNames) {
    // implement me
  }

  @Dann("{string} findet {string} im Healthcare-Service {string} über die FDV-Schnittstelle")
  public void findHCSFromEpaClient(String actor, List<String> endpointUser, String hcsName) {
    // implement me
  }

  @Dann(
      "{string} findet Healthcare-Service {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten über die FDV-Schnittstelle")
  public void findHealthcareServiceWithSearchParamFromEpaClient(
      String actorName, String healthcareServiceName, int begin, int end) {
    // implement me
  }

  @Dann(
      "{string} findet TI-Messenger-Nutzer {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten die FDV-Schnittstelle")
  public void findPractitionerWithSearchParamFromEpaClient(
      String actorName, String healthcareServiceName, int begin, int end) {
    // implement me
  }

  @Dann(
      "{string} findet {string} im Healthcare-Service {string} über die FDV-Schnittstelle NICHT [Retry 13 - 4]")
  public void dontfindHcsEndpintWithSearchParamFromEpaClient(
      String actorName, String userEndpoint, String healthcareServiceName) {
    // implement me
  }

  @Dann(
      "{string} findet {string} bei Suche in FHIR über die FDV-Schnittstelle NICHT [Retry 13 - 4]")
  public void dontFindPractitionerEndpintWithSearchParamFromEpaClient(
      String actorName, String userEndpoint, String healthcareServiceName) {
    // implement me
  }

  @Then("{string} versucht User {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutUserOnAllowlist(String actorName, List<String> userName) {
    // implement me - negativ - hier wird forbidden/400 erwartet)
  }

  @Then("{string} versucht User {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutUserOnBlocklist(String actorName, List<String> userName) {
    // implement me - negativ - hier wird forbidden/400 erwartet)
  }

  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Allowliste zu hinterlegen")
  public void tryToPutServerNameOnAllowlist(String actorName, List<String> userName) {
    // implement me - negativ - hier wird forbidden/400 erwartet)
  }

  @Then("{string} versucht den Server-Namen von {listOfStrings} in der Blockliste zu hinterlegen")
  public void tryToPutServerNameOnBlocklist(String actorName, List<String> userName) {
    // implement me - negativ - hier wird forbidden/400 erwartet)
  }

  @Then("{string} findet {string} nicht mehr in seiner Allowliste")
  public void dontFindUserInAllowList(int actorName, String userName) {
    // implement me
  }

  @Then("{string} findet {listOfStrings} nicht mehr in seiner Blockliste")
  public void dontFindUserInBlockList(int actorName, List<String> userName) {
    // implement me
  }

  @Then("{string} hinterlegt die User {listOfStrings} in der Allowliste erneut")
  public void putUserOnAllowListAgain(int actorName, List<String> userName) {
    // implement me - Values 200 % 403 (blocked) OK
  }

  @Then("{string} hinterlegt die User {listOfStrings} in der Blockliste erneut")
  public void putUserOnBlockListAgain(int actorName, List<String> userName) {
    // implement me - Values 200 % 403 (blocked) OK
  }

  @When("{string} hinterlegt die Gruppe {string} in der Blockliste")
  public void addsGroupToBlockList(String actorName, String groupName) {
    // implement me
  }

  @When("{string} entfernt die Gruppe {string} aus der Blockliste")
  public void deletesGroupFromBlockList(String actorName, String groupName) {
    // implement me
  }

  @When("{string} hinterlegt die Gruppe {string} in der Allowliste")
  public void addsGroupToAllowList(String actorName, String groupName) {
    // implement me
  }

  @When("{string} entfernt die Gruppe {string} aus der Allowliste")
  public void deletesGroupFromAllowList(String actorName, String groupName) {
    // implement me
  }

  @When("{string} prüft, dass die Gruppe {listOfStrings} in der Blockliste gesetzt ist")
  public void checkGroupOnBlocklist(int actorName, String groupName) {
    // implement me
  }

  @When("{string} prüft, dass die Gruppe {listOfStrings} in der Allowliste gesetzt ist")
  public void checkGroupOnAllowlist(int actorName, String groupName) {
    // implement me
  }

  @When("{string} prüft, dass die Gruppe {listOfStrings} in der Blockliste nicht gesetzt ist")
  public void checkGroupNotOnBlocklist(int actorName, String groupName) {
    // implement me
  }

  @When("{string} prüft, dass die Gruppe {listOfStrings} in der Allowliste nicht gesetzt ist")
  public void checkGroupNotOnAllowlist(int actorName, String groupName) {
    // implement me
  }

  @When("{string} prüft, ob User {listOfStrings} in der Blockliste gesetzt ist")
  public void checkUserOnBlocklist(int actorName, List<String> userName) {
    // implement me
  }

  @When("{string} prüft, ob User {listOfStrings} in der Allowliste gesetzt ist")
  public void checkUserOnAllowlist(int actorName, List<String> userName) {
    // implement me
  }

  @When("{string} prüft, ob die Domain von {listOfStrings} in der Blockliste gesetzt ist")
  public void checkServerOnBlocklist(int actorName, List<String> userName) {
    // implement me
  }

  @When("{string} prüft, ob die Domain von {listOfStrings} in der Allowliste gesetzt ist")
  public void checkServerOnAllowlist(int actorName, List<String> userName) {
    // implement me
  }

  @When("ist die unterstützte Matrix-Version {string}")
  public void checkMatrixVersionInLastResponse(int matrixVersion) {
    // implement me
  }
}
