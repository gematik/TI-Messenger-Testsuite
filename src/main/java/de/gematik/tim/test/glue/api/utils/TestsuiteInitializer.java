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
import io.restassured.internal.mapping.Jackson3Mapper;
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
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class TestsuiteInitializer {

  public static final boolean RUN_WITHOUT_RETRY;
  public static final Integer CLAIM_DURATION;
  public static final String COMBINE_ITEMS_FILE_NAME;
  public static final String COMBINE_ITEMS_FILE_URL;
  public static final Integer MAX_RETRY_CLAIM_REQUEST;
  public static String BUILD_DIRECTORY;
  public static String INDIVIDUAL_LOG_PATH = "./target/individual-log.json";
  public static Long TIMEOUT;
  public static final String ORGANIZATION_NAME;

  private static final String FEATURE_PATH_PROPERTY_NAME = "feature_dir";
  private static final String FEATURE_PATH;
  private static final String POLL_INTERVAL_PROPERTY_NAME = "pollInterval";
  private static final Long POLL_INTERVAL_DEFAULT = 1L;
  private static final String TIMEOUT_PROPERTY_NAME = "timeout";
  private static final Long TIMEOUT_DEFAULT = 10L;
  private static final String HTTP_TIMEOUT_PROPERTY_NAME = "httpTimeout";
  private static final Integer HTTP_TIMEOUT;
  private static final String RUN_WITHOUT_RETRY_PROPERTY_NAME = "runWithoutRetry";
  private static final String CLAIM_DURATION_PROPERTY_NAME = "claimDuration";
  private static final String COMBINE_ITEMS_FILE_PROPERTY_NAME = "combine.items.file";
  private static final String MAX_RETRY_CLAIM_REQUEST_NAME = "maxRetryClaimRequest";
  private static final String DEFAULT_MVN_PROPERTIES_LOCATION = "./target/classes/mvn.properties";
  private static final String MVN_PROPERTIES_LOCATION;
  private static final String BUILD_DIRECTORY_PROPERTY_NAME = "buildDirectory";
  private static final String DEFAULT_BUILD_DIRECTORY = "target";
  private static final String FEATURE_ENDING = "feature";
  private static final String KEY_STORE_ENV_VAR = "TIM_KEYSTORE";
  private static final String KEY_STORE_PW_ENV_VAR = "TIM_KEYSTORE_PW";
  private static final String RUN_WITHOUT_CERT = "no configured cert found";

  static Long pollInterval;

  static {
    BUILD_DIRECTORY = System.getProperty(BUILD_DIRECTORY_PROPERTY_NAME);

    Properties properties = new Properties();
    if (BUILD_DIRECTORY == null) {
      BUILD_DIRECTORY = DEFAULT_BUILD_DIRECTORY;
    }
    MVN_PROPERTIES_LOCATION =
        DEFAULT_MVN_PROPERTIES_LOCATION.replace(DEFAULT_BUILD_DIRECTORY, BUILD_DIRECTORY);
    INDIVIDUAL_LOG_PATH = INDIVIDUAL_LOG_PATH.replace(DEFAULT_BUILD_DIRECTORY, BUILD_DIRECTORY);

    try (FileInputStream fileStream =
        FileUtils.openInputStream(new File(MVN_PROPERTIES_LOCATION))) {
      properties.load(fileStream);
    } catch (IOException e) {
      log.error("Could not find any maven properties at {}", MVN_PROPERTIES_LOCATION);
      throw new IllegalArgumentException(e);
    }
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
    ORGANIZATION_NAME = parseOrganizationName();
    COMBINE_ITEMS_FILE_URL = properties.getProperty(COMBINE_ITEMS_FILE_PROPERTY_NAME);
    COMBINE_ITEMS_FILE_NAME = new File(COMBINE_ITEMS_FILE_URL).getName();
    FEATURE_PATH = properties.getProperty(FEATURE_PATH_PROPERTY_NAME);

    String timeoutString = properties.getProperty(TIMEOUT_PROPERTY_NAME);
    TIMEOUT = TIMEOUT_DEFAULT;
    if (!timeoutString.isEmpty()) {
      try {
        TIMEOUT = Long.parseLong(timeoutString);
      } catch (NumberFormatException e) {
        log.info(
            "Could not parse timeout ({}). Will use default -> timeout: {}",
            timeoutString,
            TIMEOUT);
      }
    }

    String pollIntervalString = properties.getProperty(POLL_INTERVAL_PROPERTY_NAME);
    pollInterval = POLL_INTERVAL_DEFAULT;
    if (!pollIntervalString.isEmpty()) {
      try {
        pollInterval = Long.parseLong(pollIntervalString);
      } catch (NumberFormatException e) {
        log.info(
            "Could not parse pollInterval ({}). Will use default -> pollInterval: {}",
            pollIntervalString,
            pollInterval);
      }
    }

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
    ObjectMapperConfig mapperConfig = RestAssured.config().getObjectMapperConfig();
    ObjectMapperConfig strictMapperConfig = mapperConfig.defaultObjectMapper(createStrictMapper());

    HttpClientConfig httpClientFactory =
        HttpClientConfig.httpClientConfig()
            .setParam("http.socket.timeout", HTTP_TIMEOUT * 1000)
            .setParam("http.connection.timeout", HTTP_TIMEOUT * 1000);
    RestAssured.config =
        RestAssured.config().httpClient(httpClientFactory).objectMapperConfig(strictMapperConfig);
  }

  private static Jackson3Mapper createStrictMapper() {
    return new Jackson3Mapper(
        (type, string) ->
            JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .build());
  }

  private static List<File> getFeatureFiles(File parentFile) {
    List<File> featureFiles = new ArrayList<>();
    for (File file : requireNonNull(parentFile.listFiles())) {
      if (file.isDirectory()) {
        featureFiles.addAll(getFeatureFiles(file));
      }
      if (file.getAbsolutePath().endsWith(FEATURE_ENDING)) {
        featureFiles.add(file);
      }
    }
    return featureFiles;
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

  private static String parseOrganizationName() {
    try (InputStream stream = new FileInputStream(System.getenv(KEY_STORE_ENV_VAR))) {
      KeyStore store = KeyStore.getInstance("PKCS12");
      store.load(stream, System.getenv(KEY_STORE_PW_ENV_VAR).toCharArray());
      X509Certificate cert =
          parse(store.getCertificate(store.aliases().nextElement()).getEncoded());
      RDN organizationName = new JcaX509CertificateHolder(cert).getSubject().getRDNs(BCStyle.O)[0];
      return organizationName.getFirst().getValue().toString();
    } catch (Exception e) {
      log.error(
          "Could not parse certificate name from KEY_STORE: {}", System.getenv(KEY_STORE_ENV_VAR));
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
  private static GherkinDocument transformToGherkin(File file) {
    return parseGherkinString(Files.readString(file.toPath()));
  }
}
