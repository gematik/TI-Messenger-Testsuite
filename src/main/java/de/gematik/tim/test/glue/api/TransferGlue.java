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
import io.cucumber.java.de.Wenn;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

  @Dann("{string} hinterlegt die Domain von {listOfStrings} in der Blockliste")
  public void putDomainInBlockList(String actorNames, List<String> blockedUsers) {
    // implement me
  }

  @Dann("{string} entfernt die Domain von {listOfStrings} in der Blockliste")
  public void deleteDomainInBlockList(String actorNames, List<String> blockedUsers) {
    // implement me
  }

  @Dann("{string} hinterlegt die Domain von {listOfStrings} in der Allowliste")
  public void putDomainInAllowList(String actorNames, List<String> blockedUsers) {
    // implement me
  }

  // Noch nicht in gebrauch, wird aber kommen :)
  @Dann("{string} entfernt die Domain von {listOfStrings} in der Allowliste")
  public void deleteDomainInAllowList(String actorNames, List<String> blockedUsers) {
    // implement me
  }

  @Wenn("{string} sendet ein Attachment {string} an den Raum {string} über Matrix-Protokoll v1.11")
  public void sendetEinAttachmentAnDenRaumÜberMatrixProtokollV(
      String arg0, String arg1, String arg2) {
    // implement me
  }

  @Dann(
      "{string} empfängt das Attachment {string} von {string} im Raum {string} über Matrix-Protokoll v1.11")
  public void empfängtDasAttachmentVonImRaumÜberMatrixProtokollV(
      String arg0, String arg1, String arg2, String arg3) {
    // implement me
  }

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

  @Dann("{string} findet Healthcare-Service {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten über die FDV-Schnittstelle")
  public void findHealthcareServiceWithSearchParamFromEpaClient(String actorName, String healthcareServiceName, int begin, int end) {
    // implement me
  }

  @Dann("{string} findet TI-Messenger-Nutzer {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten die FDV-Schnittstelle")
  public void findPractitionerWithSearchParamFromEpaClient(String actorName, String healthcareServiceName, int begin, int end) {
    // implement me
  }

  @Dann("{string} ändert seine letzte Nachricht im Chat mit {string} in {string}")
  public void findHCSFromEpaClient(String actor, String chatUser, String message) {
    // implement me
  }

  @Dann("{string} ist dem Chat mit {listOfStrings} beigetreten")
  public void enterChat(String actor, List<String> chatUser) {
    // implement me
  }
}
