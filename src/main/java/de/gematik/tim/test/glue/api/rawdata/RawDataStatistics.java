/*
 * Copyright (c) 2022 gematik GmbH
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

package de.gematik.tim.test.glue.api.rawdata;

import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.leftPad;

import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import org.springframework.http.HttpStatus;

public class RawDataStatistics {

  private RawDataStatistics() {
  }

  private static final RawDataEventCounter login = new RawDataEventCounter();
  private static final RawDataEventCounter search = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomSameHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageSameHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter editContactManagement = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomMultiHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageMultiHomeserver = new RawDataEventCounter();

  /**
   * Count method for these events:
   * <ul>
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Client-Login, Auswahl Authentifizierungsverfahren</li>
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Erstellung Matrix-ACCESS_TOKEN</li>
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Erstellung Matrix-OpenID-Token</li>
   * </ul>
   **/
  public static void login() {
    countEventFor(login);
  }

  /**
   * Count method for this event:
   * <ul>
   *   <li>6.7 AF - Einladung von Akteuren innerhalb einer Organisation: Akteur suchen</li>
   * </ul>
   **/
  public static void search() {
    countEventFor(search);
  }

  /**
   * Count method for this event:
   * <ul>
   *   <li>6.7 AF - Einladung von Akteuren innerhalb einer Organisation: Akteur einladen</li>
   * </ul>
   **/
  public static void inviteToRoomSameHomeserver() {
    countEventFor(inviteToRoomSameHomeserver);
  }

  /**
   * Count method for this event:
   * <ul>
   *   <li>6.8 AF - Austausch von Events innerhalb einer Organisation</li>
   * </ul>
   **/
  public static void exchangeMessageSameHomeserver() {
    countEventFor(exchangeMessageSameHomeserver);
  }

  /**
   * Count method for this event:
   * <ul>
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Eintrag in Freigabeliste erzeugen</li>
   * </ul>
   **/
  public static void editContactManagement() {
    countEventFor(editContactManagement);
  }

  /**
   * Count method for these events:
   * <ul>
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Einladung Sendersystem</li>
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Einladung Empfangssystem(e)</li>
   * </ul>
   **/
  public static void inviteToRoomMultiHomeserver() {
    countEventFor(inviteToRoomMultiHomeserver);
  }

  /**
   * Count method for these events:
   * <ul>
   *   <li>6.10 AF - Austausch von Events zwischen Akteuren außerhalb einer Organisation: Event Sendersystem</li>
   *   <li>6.10 AF - Austausch von Events zwischen Akteuren außerhalb einer Organisation Event Empfangssystem(e)</li>
   * </ul>
   **/
  public static void exchangeMessageMultiHomeserver() {
    countEventFor(exchangeMessageMultiHomeserver);
  }

  public static void addToReport() {
    Serenity.recordReportData()
        .withTitle("Rohdaten-Statistik")
        .andContents(
            reportFor(login, "TIM.UC_10057_01 (Client-Login, Auswahl Authentifizierungsverfahren):")
                + reportFor(login, "TIM.UC_10057_02 (Erstellung Matrix-ACCESS_TOKEN):")
                + reportFor(login, "TIM.UC_10057_03 (Erstellung Matrix-OpenID-Token):")
                + reportFor(search, "TIM.UC_10104_01 (Akteur suchen):")
                + reportFor(inviteToRoomSameHomeserver,
                "TIM.UC_10104_02 (Akteur einladen, gleicher Homeserver):")
                + reportFor(exchangeMessageSameHomeserver,
                "TIM.UC_10063_01 (Austausch von Events innerhalb einer Organisation, gleicher Homeserver):")
                + reportFor(editContactManagement,
                "TIM.UC_10061_01 (Eintrag in Freigabeliste erzeugen):")
                + reportFor(inviteToRoomMultiHomeserver,
                "TIM.UC_10061_02 (Einladung Sendersystem, unterschiedliche Homeserver):")
                + reportFor(inviteToRoomMultiHomeserver,
                "TIM.UC_10061_03 (Einladung Empfangssystem, unterschiedliche Homeserver):")
                + reportFor(exchangeMessageMultiHomeserver,
                "TIM.UC_10062_01 (Event Sendersystem, unterschiedliche Homeserver):")
                + reportFor(exchangeMessageMultiHomeserver,
                "TIM.UC_10062_02 (Event Empfangssystem, unterschiedliche Homeserver):")
        );
  }

  private static String reportFor(RawDataEventCounter counter, String description) {
    return description
        + "\n\t   Erfolgreich:\t" + leftPad("" + counter.getSuccessCount(), 5)
        + "\n\tFehlgeschlagen:\t" + leftPad("" + counter.getErrors().size(), 5)
        + (!counter.getErrors().isEmpty() ? "\n\t" + counter.getErrors() + "\n\n" : "\n\n");
  }

  private static void countEventFor(RawDataEventCounter counter) {
    Response response = lastResponse();
    if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
      counter.countSuccess();
    } else {
      counter.addError(response.getStatusLine());
    }
  }

}
