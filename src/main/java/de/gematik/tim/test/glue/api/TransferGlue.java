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

  @Dann("{string} erstellt einen Chat-Raum {string} inkl. Topic")
  public void crateRoomInclTopic(String actor, String roomName) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Raum {string} befüllt sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Raum {string} befüllt ist")
  public void checkParameterFilledForRoomStatesOfRoom(
      String actor, List<String> requestedRoomStates, String roomName) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Chat mit {string} befüllt sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Chat mit {string} befüllt ist")
  public void checkParameterFilledForRoomStatesOfChat(
      String actor, List<String> requestedRoomStates, String chatPartner) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Chat mit {string} leer sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Chat mit {string} leer ist")
  public void checkParameterNotExistentForRoomStatesOfChat(
      String actor, List<String> requestedRoomStates, String chatPartner) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Raum {string} leer sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Raum {string} leer ist")
  public void checkParameterNotExistentForRoomStatesOfRoom(
      String actor, List<String> requestedRoomStates, String roomName) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Raum {string} vorhanden sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Raum {string} vorhanden ist")
  public void checkParameterExistentForRoomStatesOfRoom(
      String actor, List<String> requestedRoomStates, String roomName) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob die Parameter in den Room States {listOfStrings} im Chat mit {string} vorhanden sind")
  @Then(
      "{string} prüft, ob der Parameter in den Room States {listOfStrings} im Chat mit {string} vorhanden ist")
  public void checkParameterExistentForRoomStatesOfChat(
      String actor, List<String> requestedRoomStates, String chatPartner) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob der Parameter in den Room States {string} im Raum {string} mit dem Wert von {string} befüllt ist")
  public void compareParameterForRoomStatesOfRoom(
      String actor, String requestedValue, String roomName, String comapreValue) {
    // implement me
  }

  @Dann(
      "{string} prüft, ob der Parameter in den Room States {string} im Chat mit {string} mit dem Wert von {string} befüllt ist")
  public void compareParameterForRoomStatesOfChat(
      String actor, String requestedValue, String chatPartner, String comapreValue) {
    // implement me
  }

  @Then("{string} überprüft, ob die Response der API {string} befüllt ist")
  public void pingApiOnHomeserverIncludingParameter(String actorName, String apiUrl) {
    // implement me - Werte 200 und 401 (o.ä. sind erlaubt, Ergebniss muss geloggt werden)
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

  @Then("{string} schreibt {string} direkt {string} als {string}")
  public void sendMessageToChatWithMsgType(
      String actorName, String userName, String message, String msgType) {
    // implement me - hier werden verschiedene Typen als msgType erwartet, die dann auch in den
    // Responses als Struktur gerüft werden sollen)
  }

  @Then("{string} empfängt eine Nachricht {string} von {string} als {string}")
  public void getMessageFromChatWithMsgType(
      String actorName, String message, String userName, String msgType) {
    // tbd ... implement me - hier werden verschiedene Typen als msgType erwartet, die dann auch in
    // den Responses als Struktur gerüft werden sollen)
  }

  @Then("{string} sendet die Nachricht {string} als {string} an den Raum {string}")
  public void sendMessageToRoomWithMsgType(
      String actorName, String message, String msgType, String roomName) {
    // tbd ... implement me - hier werden verschiedene Typen als msgType erwartet, die dann auch in
    // den Responses als Struktur gerüft werden sollen)
  }

  @Then("{string} empfängt eine Nachricht {string} als {string} von {string} im Raum {string}")
  public void getMessageFromRoomWithMsgType(
      String actorName, String message, String msgType, String userName, String roomName) {
    // implement me - hier werden verschiedene Typen als msgType erwartet, die dann auch in den
    // Responses als Struktur gerüft werden sollen)
  }

  @Then("{string} sendet {string} die Location {string} als {string}")
  public void sendLocationToChatWithMsgType(
      String actorName, String userName, String location, String msgType) {
    // implement me)
  }

  @Then("{string} sendet die Location {string} als {string} an den Raum {string}")
  public void sendLocationToRoomWithMsgType(
      String actorName, String location, String msgType, String roomName) {
    // implement me)
  }

  @Then("{string} empfängt die Location {string} von {string} als {string}")
  public void getLocationToChatWithMsgType(
      String actorName, String location, String userName, String msgType) {
    // implement me)
  }

  @Then("{string} empfängt die Location {string} als {string} von {string} im Raum {string}")
  public void getLocationToRoomWithMsgType(
      String actorName, String location, String msgType, String userName, String roomName) {
    // implement me)
  }

  @Then("{string} sendet ein Attachment {string} direkt an {string} als {string}")
  public void sendAttachmentToChatWithMsgType(
      String actorName, String file, String userName, String msgType) {
    // implement me)
  }

  @Then("{string} sendet ein Attachment {string} als {string} an den Raum {string}")
  public void sendAttachmentToRoomWithMsgType(
      String actorName, String file, String msgType, String roomName) {
    // implement me)
  }

  @Then(
      "{string} empfängt das Attachment {string} von {string} über Matrix-Protokoll v1.11 als {string}")
  public void getAttachmentToChatWithMsgType(
      String actorName, String file, String userName, String msgType) {
    // implement me)
  }

  @Then(
      "{string} empfängt das Attachment {string} von {string} im Raum {string} über Matrix-Protokoll v1.11 als {string}")
  public void getAttachmentToRoomWithMsgType(
      String actorName, String file, String userName, String roomName, String msgType) {
    // implement me)
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

  @When(
      "{word} fragt an Schnittstelle {word} die profile API {string} inkl Parameterbefüllung von {string} über ein {string} ab")
  public void pingApiOnHomeserverWithUserParameters(
      String actorName,
      String interfaceName,
      String matrixUrl,
      String parameterUser,
      String httpMethod) {
    // implement me
  }
}
