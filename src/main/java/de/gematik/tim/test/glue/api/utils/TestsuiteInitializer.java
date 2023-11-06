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

package de.gematik.tim.test.glue.api.utils;

import static com.networknt.schema.utils.StringUtils.isBlank;
import static com.nimbusds.jose.util.X509CertUtils.parse;
import static io.cucumber.messages.types.SourceMediaType.TEXT_X_CUCUMBER_GHERKIN_PLAIN;
import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

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

  public static final Integer MAX_RETRY_CLAIM_REQUEST;
  public static final Boolean SAVE_CONNECTIONS;
  public static final Boolean CLEAR_ROOMS;
  public static final Integer CLAIM_DURATION;
  public static final String CERT_CN;
  public static final String FEATURE_PATH = "./target/features";
  public static final String INDIVIDUAL_LOG_PATH = "./target/individual-log.json";
  public static final String CLEAR_ROOMS_PROPERTY_NAME = "clearRooms";
  public static final String FEATURE_ENDING = "feature";
  static final boolean RUN_WITHOUT_RETRY;
  private static final String MVN_PROPERTIES_LOCATION = "./target/classes/mvn.properties";
  private static final String POLL_INTERVAL_PROPERTY_NAME = "pollInterval";
  private static final String TIMEOUT_PROPERTY_NAME = "timeout";
  private static final String HTTP_TIMEOUT_PROPERTY_NAME = "httpTimeout";
  private static final String RUN_WITHOUT_RETRY_PROPERTY_NAME = "runWithoutRetry";
  private static final String SAVE_CONNECTIONS_PROPERTY_NAME = "saveConnections";
  private static final String CLAIM_DURATION_PROPERTYNAME = "claimDuration";
  private static final String KEY_STORE = "TIM_KEYSTORE";
  private static final String KEY_STORE_PW = "TIM_KEYSTORE_PW";
  private static final String RUN_WITHOUT_CERT = "no configured cert found";
  private static final String MAX_RETRY_CLAIM_REQUEST_NAME = "maxRetryClaimRequest";
  private static final Long TIMEOUT_DEFAULT = 10L;
  private static final Long POLL_INTERVAL_DEFAULT = 1L;
  private static final Integer HTTP_TIMEOUT;
  private static final Jackson2Mapper mapper;
  static Long timeout;
  static Long pollInterval;

  static {
    Properties p = new Properties();
    try {
      FileInputStream is = FileUtils.openInputStream(new File(MVN_PROPERTIES_LOCATION));
      p.load(is);
    } catch (IOException e) {
      log.error("Could not find any maven properties at " + MVN_PROPERTIES_LOCATION);
      throw new IllegalArgumentException(e);
    }
    String timeoutString = p.getProperty(TIMEOUT_PROPERTY_NAME);
    String pollIntervalString = p.getProperty(POLL_INTERVAL_PROPERTY_NAME);
    RUN_WITHOUT_RETRY = Boolean.parseBoolean(p.getProperty(RUN_WITHOUT_RETRY_PROPERTY_NAME));
    SAVE_CONNECTIONS = Boolean.parseBoolean(p.getProperty(SAVE_CONNECTIONS_PROPERTY_NAME));
    CLAIM_DURATION = Integer.parseInt(isBlank(p.getProperty(CLAIM_DURATION_PROPERTYNAME)) ? "180"
        : p.getProperty(CLAIM_DURATION_PROPERTYNAME));
    HTTP_TIMEOUT = Integer.parseInt(isBlank(p.getProperty(HTTP_TIMEOUT_PROPERTY_NAME)) ? "180" : p.getProperty(
        HTTP_TIMEOUT_PROPERTY_NAME));
    MAX_RETRY_CLAIM_REQUEST = Integer.parseInt(
        isBlank(p.getProperty(MAX_RETRY_CLAIM_REQUEST_NAME)) ? "3" : p.getProperty(MAX_RETRY_CLAIM_REQUEST_NAME));

    CERT_CN = parseCn();
    CLEAR_ROOMS = Boolean.parseBoolean(p.getProperty(CLEAR_ROOMS_PROPERTY_NAME));
    try {
      timeout = Long.parseLong(timeoutString);
      pollInterval = Long.parseLong(pollIntervalString);
    } catch (Exception ex) {
      timeout = TIMEOUT_DEFAULT;
      pollInterval = POLL_INTERVAL_DEFAULT;
      log.info(format(
          "Could not parse timeout (%s) or pollInterval (%s). Will use default -> timeout: %s, pollInterval: %s",
          timeoutString, pollIntervalString, timeout, pollInterval));
    }
    mapper = createMapper();
    ObjectMapperConfig config = RestAssured.config().getObjectMapperConfig();
    config.defaultObjectMapper(getMapper());
    RestAssured.config().objectMapperConfig(config);
    addHostsToTigerProxy();
    configRestAssured();
  }

  private static void configRestAssured() {

    HttpClientConfig httpClientFactory = HttpClientConfig
        .httpClientConfig()
        .setParam("http.socket.timeout", HTTP_TIMEOUT * 1000)
        .setParam("http.connection.timout", HTTP_TIMEOUT * 1000);

    RestAssured.config = RestAssured
        .config()
        .httpClient(httpClientFactory);
  }

  static Jackson2Mapper getMapper() {
    return mapper;
  }

  private static Jackson2Mapper createMapper() {
    return new Jackson2Mapper((type, s) -> {
      ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
      om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return om;
    });
  }

  private static List<File> getFeatureFiles(File file) {
    List<File> files = new ArrayList<>();
    for (File f : file.listFiles()) {
      if (f.isDirectory()) {
        files.addAll(getFeatureFiles(f));
      }
      if (f.getAbsolutePath().endsWith(FEATURE_ENDING)) {
        files.add(f);
      }
    }
    return files;
  }

  @SneakyThrows
  public static void addHostsToTigerProxy() {
    Set<String> hosts = getHttpsApisFromFeatureFiles();
    log.info("{} apis going to be added to alternative name of tiger proxy\n\t{}", hosts.size(),
        String.join("\n\t", hosts));
    TigerProxy proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail();
    hosts.forEach(proxy::addAlternativeName);
  }

  private static Set<String> getHttpsApisFromFeatureFiles() {
    List<File> featureFiles = getFeatureFiles(new File(FEATURE_PATH));
    List<GherkinDocument> features = featureFiles.stream().map(TestsuiteInitializer::transformToGherkin).toList();
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
    try (InputStream stream = new FileInputStream(System.getenv(KEY_STORE))) {
      KeyStore store = KeyStore.getInstance("PKCS12");
      store.load(stream, System.getenv(KEY_STORE_PW).toCharArray());
      X509Certificate cert = parse(
          store.getCertificate(store.aliases().nextElement()).getEncoded());
      RDN cn = new JcaX509CertificateHolder(cert).getSubject().getRDNs(BCStyle.CN)[0];
      return cn.getFirst().getValue().toString();
    } catch (Exception ex) {
      log.error("Could not parse certificat" + " KEY_STORE: " + System.getenv(KEY_STORE));
    }
    return RUN_WITHOUT_CERT;
  }

  private static GherkinDocument parseGherkinString(String gherkin) {
    final GherkinParser parser = GherkinParser.builder()
        .includeSource(false)
        .includePickles(false)
        .includeGherkinDocument(true)
        .build();

    final Source source = new Source("not needed", gherkin, TEXT_X_CUCUMBER_GHERKIN_PLAIN);
    final Envelope envelope = Envelope.of(source);

    return parser.parse(envelope)
        .map(Envelope::getGherkinDocument)
        .flatMap(Optional::stream)
        .findAny()
        .orElseThrow(
            () -> new IllegalArgumentException("Could not parse invalid gherkin."));
  }

  @SneakyThrows
  private static GherkinDocument transformToGherkin(File f) {
    return parseGherkinString(Files.readString(f.toPath()));
  }

}
