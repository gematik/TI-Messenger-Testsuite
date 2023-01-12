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

package de.gematik.tim.test.glue.api.rawdata;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.restassured.response.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import net.serenitybdd.core.Serenity;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.provider.CsvParsingException;
import org.springframework.http.HttpStatus;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class RawDataStatistics {

  private RawDataStatistics() {
  }
  public static final String YML_PATHNAME = "./target/generated-combine/rohdaten.yml";
  public static final String JSON_PATHNAME = "./target/generated-combine/rohdaten.json";
  public static final String CSV_PATHNAME = "./target/generated-combine/rohdaten.csv";
  private static final RawDataEventCounter login = new RawDataEventCounter();
  private static final RawDataEventCounter search = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomSameHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageSameHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter editContactManagement = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomMultiHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageMultiHomeserver = new RawDataEventCounter();
  private static final String[] HEADERS = new String[]{"description", "success", "error",  "errors"};
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static final YAMLMapper YAML_MAPPER = new YAMLMapper();



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
    List<RawDataObject> dataObjectList = new ArrayList<>();
    dataObjectList.add(
        new RawDataObject("TIM.UC_10057_01 (Client-Login, Auswahl Authentifizierungsverfahren):",
            login));
    dataObjectList.add(new RawDataObject("TIM.UC_10057_02 (Erstellung Matrix-ACCESS_TOKEN):", login));
    dataObjectList.add(new RawDataObject("TIM.UC_10057_03 (Erstellung Matrix-OpenID-Token):", login));
    dataObjectList.add(new RawDataObject("TIM.UC_10104_01 (Akteur suchen):", search));
    dataObjectList.add(new RawDataObject("TIM.UC_10104_02 (Akteur einladen, gleicher Homeserver):",
        inviteToRoomSameHomeserver));
    dataObjectList.add(new RawDataObject(
        "TIM.UC_10063_01 (Austausch von Events innerhalb einer Organisation, gleicher Homeserver):",
        exchangeMessageSameHomeserver));
    dataObjectList.add(new RawDataObject("TIM.UC_10061_01 (Eintrag in Freigabeliste erzeugen):",
        editContactManagement));
    dataObjectList.add(
        new RawDataObject("TIM.UC_10061_02 (Einladung Sendersystem, unterschiedliche Homeserver):",
            inviteToRoomMultiHomeserver));
    dataObjectList.add(
        new RawDataObject("TIM.UC_10061_03 (Einladung Empfangssystem, unterschiedliche Homeserver):",
            inviteToRoomMultiHomeserver));
    dataObjectList.add(
        new RawDataObject("TIM.UC_10062_01 (Event Sendersystem, unterschiedliche Homeserver):",
            exchangeMessageMultiHomeserver));
    dataObjectList.add(
        new RawDataObject("TIM.UC_10062_02 (Event Empfangssystem, unterschiedliche Homeserver):",
            exchangeMessageMultiHomeserver));
    rawToJson(dataObjectList);
    rawToYaml(dataObjectList);
    rawToCSV(dataObjectList);
    Serenity.recordReportData()
        .withTitle("Rohdaten-Statistik")
        .andContents(join(dataObjectList," "));
  }

  @SneakyThrows
  private static void rawToJson(List<RawDataObject> dataObjectList) {
    OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    String s = OBJECT_MAPPER.writeValueAsString(dataObjectList);
    File jsonFile = new File(JSON_PATHNAME);
    FileUtils.writeStringToFile(jsonFile, s, UTF_8);
  }

  @SneakyThrows
  private static void rawToYaml(List<RawDataObject> dataObjectList) {
    String s = YAML_MAPPER.writeValueAsString(dataObjectList);
    File yamlFile = new File(YML_PATHNAME);
    FileUtils.writeStringToFile(yamlFile, s, UTF_8);
  }

  @SneakyThrows
  private static void rawToCSV(List<RawDataObject> dataObjectList) {
    File csvFile = new File(CSV_PATHNAME);
    try (ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(new FileWriter(csvFile),
        CsvPreference.STANDARD_PREFERENCE)) {
      csvBeanWriter.writeHeader(HEADERS);
      dataObjectList.forEach(e -> {
        try {
          csvBeanWriter.write(e, HEADERS);
        } catch (IOException ex) {
          throw new CsvParsingException("Can't build up correct CSV schema from given objects.");
        }
      });
    }
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
