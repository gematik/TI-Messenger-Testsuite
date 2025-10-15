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

  @Dann("{string} fragt die Room States aus dem Chat mit {string} ab")
  public void getRoomStatesFromChat(String actorNames, String chatMember) {
    // implement me
  }

  @Dann("{string} prüft, dass falls der Parameter {listOfStrings} in den Room States im Chat mit {string} vorhanden ist, er mit dem Wert von {listOfStrings} befüllt ist")
  public void checkRoomStatesForChat(String actorNames, List<String> matrixRoomState, String chatMember, List<String> gematikRoomState) {
    // implement me
  }

}
