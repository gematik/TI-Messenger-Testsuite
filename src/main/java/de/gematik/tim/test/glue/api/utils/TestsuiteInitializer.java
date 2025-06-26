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

package de.gematik.tim.test.glue.api.utils;

import static com.nimbusds.jose.util.X509CertUtils.parse;
import static io.cucumber.messages.types.SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.test.tiger.proxy.TigerProxy;
import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Examples;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.FeatureChild;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Source;
import io.cucumber.messages.types.TableCell;
import io.cucumber.messages.types.TableRow;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.internal.mapping.Jackson2Mapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class TestsuiteInitializer {

  public static final String FEATURE_PATH_PROPERTY_NAME = "feature_dir";
  public static final String CLEAR_ROOMS_PROPERTY_NAME = "clearRooms";
  public static final String MVN_PROPERTIES_LOCATION = "./target/classes/mvn.properties";
  public static final String CLAIM_PARALLEL_PROPERTY_NAME = "claimParallel";
  public static final String POLL_INTERVAL_PROPERTY_NAME = "pollInterval";
  public static final String TIMEOUT_PROPERTY_NAME = "timeout";
  public static final String HTTP_TIMEOUT_PROPERTY_NAME = "httpTimeout";
  public static final String RUN_WITHOUT_RETRY_PROPERTY_NAME = "runWithoutRetry";
  public static final String CLAIM_DURATION_PROPERTY_NAME = "claimDuration";
  public static final String COMBINE_ITEMS_FILE_PROPERTY_NAME = "combine.items.file";
  public static final String CHECK_ROOM_STATE_FAIL_PROPERTY_NAME = "skipRoomStateCheck";
  public static final Integer MAX_RETRY_CLAIM_REQUEST;
  public static final Boolean CLEAR_ROOMS;
  public static final Integer CLAIM_DURATION;
  public static final Boolean CLAIM_PARALLEL;
  public static final boolean RUN_WITHOUT_RETRY;
  public static final boolean CHECK_ROOM_STATE_FAIL;
  public static final String CERT_CN;
  public static final String COMBINE_ITEMS_FILE_NAME;
  public static final String COMBINE_ITEMS_FILE_URL;
  public static final String FEATURE_PATH;
  public static final String INDIVIDUAL_LOG_PATH = "./target/individual-log.json";
  public static final String FEATURE_ENDING = "feature";
  public static final String KEY_STORE_ENV_VAR = "TIM_KEYSTORE";
  public static final String KEY_STORE_PW_ENV_VAR = "TIM_KEYSTORE_PW";
  public static final String RUN_WITHOUT_CERT = "no configured cert found";
  public static final String MAX_RETRY_CLAIM_REQUEST_NAME = "maxRetryClaimRequest";
  public static final String NO_PARALLEL_TAG = "@Ctl:NoParallel";
  public static final Long TIMEOUT_DEFAULT = 10L;
  public static final Long POLL_INTERVAL_DEFAULT = 1L;
  public static final Integer HTTP_TIMEOUT;
  private static final Jackson2Mapper mapper;
  public static Long TIMEOUT;
  static Long pollInterval;

  static {
    Properties properties = new Properties();
    try {
      FileInputStream fileStream = FileUtils.openInputStream(new File(MVN_PROPERTIES_LOCATION));
      properties.load(fileStream);
    } catch (IOException e) {
      log.error("Could not find any maven properties at " + MVN_PROPERTIES_LOCATION);
      throw new IllegalArgumentException(e);
    }
    String timeoutString = properties.getProperty(TIMEOUT_PROPERTY_NAME);
    String pollIntervalString = properties.getProperty(POLL_INTERVAL_PROPERTY_NAME);
    RUN_WITHOUT_RETRY =
        Boolean.parseBoolean(properties.getProperty(RUN_WITHOUT_RETRY_PROPERTY_NAME));
    CLAIM_DURATION =
        Integer.parseInt(
            isBlank(properties.getProperty(CLAIM_DURATION_PROPERTY_NAME))
                ? "180"
                : properties.getProperty(CLAIM_DURATION_PROPERTY_NAME));
    HTTP_TIMEOUT =
        Integer.parseInt(
            isBlank(properties.getProperty(HTTP_TIMEOUT_PROPERTY_NAME))
                ? "180"
                : properties.getProperty(HTTP_TIMEOUT_PROPERTY_NAME));
    MAX_RETRY_CLAIM_REQUEST =
        Integer.parseInt(
            isBlank(properties.getProperty(MAX_RETRY_CLAIM_REQUEST_NAME))
                ? "3"
                : properties.getProperty(MAX_RETRY_CLAIM_REQUEST_NAME));
    CHECK_ROOM_STATE_FAIL =
        Boolean.parseBoolean(properties.getProperty(CHECK_ROOM_STATE_FAIL_PROPERTY_NAME));
    CERT_CN = parseCn();
    CLEAR_ROOMS = Boolean.parseBoolean(properties.getProperty(CLEAR_ROOMS_PROPERTY_NAME));
    CLAIM_PARALLEL = Boolean.parseBoolean(properties.getProperty(CLAIM_PARALLEL_PROPERTY_NAME));
    COMBINE_ITEMS_FILE_URL = properties.getProperty(COMBINE_ITEMS_FILE_PROPERTY_NAME);
    COMBINE_ITEMS_FILE_NAME = new File(COMBINE_ITEMS_FILE_URL).getName();
    FEATURE_PATH = properties.getProperty(FEATURE_PATH_PROPERTY_NAME);
    try {
      TIMEOUT = Long.parseLong(timeoutString);
      pollInterval = Long.parseLong(pollIntervalString);
    } catch (Exception ex) {
      TIMEOUT = TIMEOUT_DEFAULT;
      pollInterval = POLL_INTERVAL_DEFAULT;
      log.info(
          "Could not parse timeout ({}) or pollInterval ({}). Will use default -> timeout: {}, pollInterval: {}",
          timeoutString,
          pollIntervalString,
          TIMEOUT,
          pollInterval);
    }

    mapper = createMapper();
    ObjectMapperConfig mapperConfig = RestAssured.config().getObjectMapperConfig();
    mapperConfig.defaultObjectMapper(getMapper());
    RestAssured.config().objectMapperConfig(mapperConfig);
    addHostsToTigerProxy();
    configRestAssured();
    saveMavenProperties(properties);
  }

  private static void saveMavenProperties(Properties properties) {
    StringBuilder sb = new StringBuilder();
    List<String> secrets = List.of("tim.keystore.pw", "tim.truststore.pw");
    properties.forEach(
        (propertyKey, propertyValue) -> {
          if (secrets.stream().noneMatch(secret -> propertyKey.toString().equals(secret))) {
            sb.append("%s=%s%n".formatted(propertyKey, propertyValue));
          }
        });
    try {
      FileUtils.write(new File("./target/saved_mvn.properties"), sb.toString(), UTF_8);
    } catch (IOException e) {
      log.error("Could not save maven properties.");
      throw new IllegalArgumentException(e);
    }
  }

  @SneakyThrows
  public static void addHostsToTigerProxy() {
    Set<String> hosts = getHttpsApisFromFeatureFiles();
    log.info(
        "{} apis going to be added to alternative name of tiger proxy\n\t{}",
        hosts.size(),
        String.join("\n\t", hosts));
    TigerProxy proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail();
    hosts.forEach(proxy::addAlternativeName);
  }

  private static void configRestAssured() {
    HttpClientConfig httpClientFactory =
        HttpClientConfig.httpClientConfig()
            .setParam("http.socket.timeout", HTTP_TIMEOUT * 1000)
            .setParam("http.connection.timout", HTTP_TIMEOUT * 1000);

    RestAssured.config = RestAssured.config().httpClient(httpClientFactory);
  }

  static Jackson2Mapper getMapper() {
    return mapper;
  }

  private static Jackson2Mapper createMapper() {
    return new Jackson2Mapper(
        (type, s) -> {
          ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
          objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          return objectMapper;
        });
  }

  private static List<File> getFeatureFiles(File file) {
    List<File> files = new ArrayList<>();
    for (File f : requireNonNull(file.listFiles())) {
      if (f.isDirectory()) {
        files.addAll(getFeatureFiles(f));
      }
      if (f.getAbsolutePath().endsWith(FEATURE_ENDING)) {
        files.add(f);
      }
    }
    return files;
  }

  private static Set<String> getHttpsApisFromFeatureFiles() {
    List<File> featureFiles = getFeatureFiles(new File(FEATURE_PATH));
    List<GherkinDocument> features =
        featureFiles.stream().map(TestsuiteInitializer::transformToGherkin).toList();
    return features.stream()
        .map(GherkinDocument::getFeature)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Feature::getChildren)
        .flatMap(Collection::stream)
        .map(FeatureChild::getScenario)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Scenario::getExamples)
        .flatMap(Collection::stream)
        .map(Examples::getTableBody)
        .flatMap(Collection::stream)
        .map(TableRow::getCells)
        .flatMap(Collection::stream)
        .map(TableCell::getValue)
        .filter(e -> e.startsWith("https://"))
        .map(e -> URI.create(e).getHost())
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private static String parseCn() {
    try (InputStream stream = new FileInputStream(System.getenv(KEY_STORE_ENV_VAR))) {
      KeyStore store = KeyStore.getInstance("PKCS12");
      store.load(stream, System.getenv(KEY_STORE_PW_ENV_VAR).toCharArray());
      X509Certificate cert =
          parse(store.getCertificate(store.aliases().nextElement()).getEncoded());
      RDN cn = new JcaX509CertificateHolder(cert).getSubject().getRDNs(BCStyle.CN)[0];
      return cn.getFirst().getValue().toString();
    } catch (Exception ex) {
      log.error("Could not parse certificate KEY_STORE: {}", System.getenv(KEY_STORE_ENV_VAR));
    }
    return RUN_WITHOUT_CERT;
  }

  private static GherkinDocument parseGherkinString(String gherkin) {
    final GherkinParser parser =
        GherkinParser.builder()
            .includeSource(false)
            .includePickles(false)
            .includeGherkinDocument(true)
            .build();

    final Source source = new Source("not needed", gherkin, TEXT_X_CUCUMBER_GHERKIN_PLAIN);
    final Envelope envelope = Envelope.of(source);

    return parser
        .parse(envelope)
        .map(Envelope::getGherkinDocument)
        .flatMap(Optional::stream)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Could not parse invalid gherkin."));
  }

  @SneakyThrows
  private static GherkinDocument transformToGherkin(File f) {
    return parseGherkinString(Files.readString(f.toPath()));
  }
}
