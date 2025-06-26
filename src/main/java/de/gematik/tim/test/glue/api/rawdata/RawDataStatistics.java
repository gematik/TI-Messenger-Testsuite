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

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.apache.commons.lang3.StringUtils.join;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.response.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.serenitybdd.core.Serenity;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.provider.CsvParsingException;
import org.springframework.http.HttpStatus;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RawDataStatistics {

  private static final RawDataEventCounter login = new RawDataEventCounter();
  private static final RawDataEventCounter search = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomSameHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageSameHomeserver =
      new RawDataEventCounter();
  private static final RawDataEventCounter editContactManagement = new RawDataEventCounter();
  private static final RawDataEventCounter inviteToRoomMultiHomeserver = new RawDataEventCounter();
  private static final RawDataEventCounter exchangeMessageMultiHomeserver =
      new RawDataEventCounter();
  private static final RawDataEventCounter getRegTokenForVZDEvent = new RawDataEventCounter();

  private static final String YML_PATHNAME = "./target/generated-raw-data/rohdaten.yml";
  private static final String JSON_PATHNAME = "./target/generated-raw-data/rohdaten.json";
  private static final String CSV_PATHNAME = "./target/generated-raw-data/rohdaten.csv";

  private static final String TIM_UC_10057_01 =
      "TIM.UC_10057_01 (Client-Login, Auswahl Authentifizierungsverfahren):";
  private static final String TIM_UC_10057_02 = "TIM.UC_10057_02 (Erstellung Matrix-ACCESS_TOKEN):";
  private static final String TIM_UC_10057_03 = "TIM.UC_10057_03 (Erstellung Matrix-OpenID-Token):";
  private static final String TIM_UC_10104_01_01 = "TIM.UC_10104-01_01 (Akteur suchen):";
  private static final String TIM_UC_10104_01_02 =
      "TIM.UC_10104-01_02 (Akteur einladen, gleicher Homeserver):";
  private static final String TIM_UC_10063_01 =
      "TIM.UC_10063_01 (Austausch von Events innerhalb einer Organisation, gleicher Homeserver):";
  private static final String TIM_UC_10061_01_01 =
      "TIM.UC_10061-01_01 (Eintrag in Freigabeliste erzeugen):";
  private static final String TIM_UC_10061_01_02 =
      "TIM.UC_10061-01_02 (Einladung Sendersystem, unterschiedliche Homeserver):";
  private static final String TIM_UC_10061_01_03 =
      "TIM.UC_10061-01_03 (Einladung Empfangssystem, unterschiedliche Homeserver):";
  private static final String TIM_UC_10062_01_01 =
      "TIM.UC_10062-01_01 (Event Sendersystem, unterschiedliche Homeserver):";
  private static final String TIM_UC_10062_01_02 =
      "TIM.UC_10062-01_02 (Event Empfangssystem, unterschiedliche Homeserver):";
  private static final String TIM_UC_10059_01_02 =
      "TIM.UC_10059-01_02 (Organisationsressourcen im Verzeichnisdienst hinzufügen):";

  private static final String[] HEADERS =
      new String[] {"description", "success", "error", "errors"};
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Yaml YAML_MAPPER;

  private static final String LOGIN_KEY = "login";
  private static final String SEARCH_KEY = "search";
  private static final String ITRSH_KEY = "inviteToRoomSameHomeserver";
  private static final String EMSH_KEY = "exchangeMessageSameHomeserver";
  private static final String ECM_KEY = "editContactManagement";
  private static final String ITRMH_KEY = "inviteToRoomMultiHomeserver";
  private static final String EMMH_KEY = "exchangeMessageMultiHomeserver";
  private static final String GRTFVE_KEY = "getRegTokenForVZDEvent";
  private static final Map<String, RawDataEventCounter> copiedCounter = new HashMap<>();

  static {
    OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    Representer representer = new Representer(options);
    representer.addClassTag(RawDataObject.class, Tag.MAP);
    YAML_MAPPER = new Yaml(representer);
  }

  /**
   * Count method for these events:
   *
   * <ul>
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Client-Login, Auswahl
   *       Authentifizierungsverfahren
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Erstellung Matrix-ACCESS_TOKEN
   *   <li>6.4 AF - Anmeldung eines Akteurs am Messenger-Service: Erstellung Matrix-OpenID-Token
   * </ul>
   */
  public static void login() {
    countEventFor(login);
  }

  public static void login(int statusCode, String statusLine) {
    countEventParallel(statusCode, statusLine);
  }

  /**
   * Count method for this event:
   *
   * <ul>
   *   <li>6.7 AF - Einladung von Akteuren innerhalb einer Organisation: Akteur suchen
   * </ul>
   */
  public static void search() {
    countEventFor(search);
  }

  /**
   * Count method for this event:
   *
   * <ul>
   *   <li>6.7 AF - Einladung von Akteuren innerhalb einer Organisation: Akteur einladen
   * </ul>
   */
  public static void inviteToRoomSameHomeserver() {
    countEventFor(inviteToRoomSameHomeserver);
  }

  /**
   * Count method for this event:
   *
   * <ul>
   *   <li>6.8 AF - Austausch von Events innerhalb einer Organisation
   * </ul>
   */
  public static void exchangeMessageSameHomeserver() {
    countEventFor(exchangeMessageSameHomeserver);
  }

  /**
   * Count method for this event:
   *
   * <ul>
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Eintrag in Freigabeliste
   *       erzeugen
   * </ul>
   */
  public static void editContactManagement() {
    countEventFor(editContactManagement);
  }

  /**
   * Count method for these events:
   *
   * <ul>
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Einladung Sendersystem
   *   <li>6.9 AF - Einladung von Akteuren außerhalb einer Organisation: Einladung Empfangssystem(e)
   * </ul>
   */
  public static void inviteToRoomMultiHomeserver() {
    countEventFor(inviteToRoomMultiHomeserver);
  }

  /**
   * Count method for these events:
   *
   * <ul>
   *   <li>6.10 AF - Austausch von Events zwischen Akteuren außerhalb einer Organisation: Event
   *       Sendersystem
   *   <li>6.10 AF - Austausch von Events zwischen Akteuren außerhalb einer Organisation Event
   *       Empfangssystem(e)
   * </ul>
   */
  public static void exchangeMessageMultiHomeserver() {
    countEventFor(exchangeMessageMultiHomeserver);
  }

  /**
   * Count method for these events:
   *
   * <ul>
   *   <li>6.3 AF - Organisationsressourcen im Verzeichnisdienst hinzufügen: Get
   *       RegService-OpenID-Token
   * </ul>
   */
  public static void getRegTokenForVZDEvent() {
    countEventFor(getRegTokenForVZDEvent);
  }

  public static void addToReport() {
    List<RawDataObject> totalDataObjectList = buildTotalDataObjectList();
    List<RawDataObject> currentDataObjectList = buildCurrentDataObjectList();

    rawToJson(totalDataObjectList);
    rawToYaml(totalDataObjectList);
    rawToCSV(totalDataObjectList);
    Serenity.recordReportData()
        .withTitle("Rohdaten-Statistik-Total")
        .andContents(join(totalDataObjectList, " "));
    Serenity.recordReportData()
        .withTitle("Rohdaten-Statistik")
        .andContents(join(currentDataObjectList, " "));
  }

  public static void startTest() {
    copiedCounter.put(LOGIN_KEY, login.copy());
    copiedCounter.put(SEARCH_KEY, search.copy());
    copiedCounter.put(ITRSH_KEY, inviteToRoomSameHomeserver.copy());
    copiedCounter.put(EMSH_KEY, exchangeMessageSameHomeserver.copy());
    copiedCounter.put(ECM_KEY, editContactManagement.copy());
    copiedCounter.put(ITRMH_KEY, inviteToRoomMultiHomeserver.copy());
    copiedCounter.put(EMMH_KEY, exchangeMessageMultiHomeserver.copy());
    copiedCounter.put(GRTFVE_KEY, getRegTokenForVZDEvent.copy());
  }

  private static List<RawDataObject> buildTotalDataObjectList() {
    return List.of(
        new RawDataObject(TIM_UC_10057_01, login),
        new RawDataObject(TIM_UC_10057_02, login),
        new RawDataObject(TIM_UC_10057_03, login),
        new RawDataObject(TIM_UC_10104_01_01, search),
        new RawDataObject(TIM_UC_10104_01_02, inviteToRoomSameHomeserver),
        new RawDataObject(TIM_UC_10063_01, exchangeMessageSameHomeserver),
        new RawDataObject(TIM_UC_10061_01_01, editContactManagement),
        new RawDataObject(TIM_UC_10061_01_02, inviteToRoomMultiHomeserver),
        new RawDataObject(TIM_UC_10061_01_03, inviteToRoomMultiHomeserver),
        new RawDataObject(TIM_UC_10062_01_01, exchangeMessageMultiHomeserver),
        new RawDataObject(TIM_UC_10062_01_02, exchangeMessageMultiHomeserver),
        new RawDataObject(TIM_UC_10059_01_02, getRegTokenForVZDEvent));
  }

  private static List<RawDataObject> buildCurrentDataObjectList() {
    return List.of(
        new RawDataObject(TIM_UC_10057_01, login.getDiff(copiedCounter.get(LOGIN_KEY))),
        new RawDataObject(TIM_UC_10057_02, login.getDiff(copiedCounter.get(LOGIN_KEY))),
        new RawDataObject(TIM_UC_10057_03, login.getDiff(copiedCounter.get(LOGIN_KEY))),
        new RawDataObject(TIM_UC_10104_01_01, search.getDiff(copiedCounter.get(SEARCH_KEY))),
        new RawDataObject(
            TIM_UC_10104_01_02, inviteToRoomSameHomeserver.getDiff(copiedCounter.get(ITRSH_KEY))),
        new RawDataObject(
            TIM_UC_10063_01, exchangeMessageSameHomeserver.getDiff(copiedCounter.get(EMSH_KEY))),
        new RawDataObject(
            TIM_UC_10061_01_01, editContactManagement.getDiff(copiedCounter.get(ECM_KEY))),
        new RawDataObject(
            TIM_UC_10061_01_02, inviteToRoomMultiHomeserver.getDiff(copiedCounter.get(ITRMH_KEY))),
        new RawDataObject(
            TIM_UC_10061_01_03, inviteToRoomMultiHomeserver.getDiff(copiedCounter.get(ITRMH_KEY))),
        new RawDataObject(
            TIM_UC_10062_01_01,
            exchangeMessageMultiHomeserver.getDiff(copiedCounter.get(EMMH_KEY))),
        new RawDataObject(
            TIM_UC_10062_01_02,
            exchangeMessageMultiHomeserver.getDiff(copiedCounter.get(EMMH_KEY))),
        new RawDataObject(
            TIM_UC_10059_01_02, getRegTokenForVZDEvent.getDiff(copiedCounter.get(GRTFVE_KEY))));
  }

  @SneakyThrows
  private static void rawToJson(List<RawDataObject> dataObjectList) {
    String s = OBJECT_MAPPER.writeValueAsString(dataObjectList);
    File jsonFile = new File(JSON_PATHNAME);
    FileUtils.writeStringToFile(jsonFile, s, UTF_8);
  }

  @SneakyThrows
  private static void rawToYaml(List<RawDataObject> dataObjectList) {
    String yaml = YAML_MAPPER.dumpAsMap(dataObjectList);
    FileUtils.write(new File(YML_PATHNAME), yaml, UTF_8);
  }

  @SneakyThrows
  private static void rawToCSV(List<RawDataObject> dataObjectList) {
    File csvFile = new File(CSV_PATHNAME);
    try (ICsvBeanWriter csvBeanWriter =
        new CsvBeanWriter(new FileWriter(csvFile), CsvPreference.STANDARD_PREFERENCE)) {
      csvBeanWriter.writeHeader(HEADERS);
      dataObjectList.forEach(
          e -> {
            try {
              csvBeanWriter.write(e, HEADERS);
            } catch (IOException ex) {
              throw new CsvParsingException(
                  "Can't build up correct CSV schema from given objects.");
            }
          });
    }
  }

  private static void countEventFor(RawDataEventCounter counter) {
    Response response = lastResponse();
    if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
      counter.countSuccess();
      return;
    }
    counter.addError(response.getStatusLine());
  }

  private static synchronized void countEventParallel(int statusCode, String statusLine) {
    if (HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
      login.countSuccess();
      return;
    }
    login.addError(statusLine);
  }
}
