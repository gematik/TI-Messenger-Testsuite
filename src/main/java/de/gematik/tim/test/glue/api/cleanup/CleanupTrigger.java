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

package de.gematik.tim.test.glue.api.cleanup;

import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.prepareApiNameForHttp;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.COMBINE_ITEMS_FILE_NAME;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.COMBINE_ITEMS_FILE_URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.InitializationException;
import de.gematik.tim.test.glue.api.threading.ClientFactory;
import de.gematik.tim.test.glue.api.utils.GlueUtils;
import io.cucumber.core.gherkin.DataTableArgument;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestStep;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CleanupTrigger {

  public static final String HOME_SERVER_PROPERTY = "homeserver";
  public static final String ORG_ADMIN_TAG = "orgAdmin";
  private static final List<CombineItem> combineItems;
  private static final Set<String> combineItemsUrls;
  private static final ThreadLocal<CloseableHttpClient> cleanUpClient =
      ThreadLocal.withInitial(ClientFactory::getCleanUpClient);
  private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
  private static final String CLAIMING_STEP_TEXT = "Es werden folgende Clients reserviert:";

  static {
    try {
      File combinedItemsFile = new File("target/generated-combine/" + COMBINE_ITEMS_FILE_NAME);
      if (!combinedItemsFile.exists()) {
        combinedItemsFile = new File(COMBINE_ITEMS_FILE_URL);
      }
      String combineItemsString = FileUtils.readFileToString(combinedItemsFile, UTF_8);
      ObjectMapper mapper =
          new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      combineItems =
          Arrays.stream(mapper.readValue(combineItemsString, CombineItem[].class)).toList();

      combineItemsUrls = new HashSet<>();
      combineItemsUrls.addAll(
          combineItems.stream().map(CombineItem::getValue).filter(Objects::nonNull).toList());
      combineItemsUrls.addAll(
          combineItems.stream().map(CombineItem::getUrl).filter(Objects::nonNull).toList());
    } catch (IOException e) {
      throw new InitializationException(e.getMessage());
    }
  }

  @SneakyThrows
  @SuppressWarnings("java:S2142")
  public static boolean sendCleanupRequest(List<TestStep> testSteps) {
    final Set<String> urlsToTrigger = getAllApisUsedInTestSteps(testSteps);
    log.info(
        "Cleaning up before tests using {} urlsToTiger: {}",
        urlsToTrigger.size(),
        '[' + String.join(", ", urlsToTrigger) + ']');
    final List<Callable<UrlAndResponseStatus>> calls = createCalls(urlsToTrigger);
    for (final Future<UrlAndResponseStatus> future : executorService.invokeAll(calls)) {
      try {
        final UrlAndResponseStatus urlAndStatus = future.get();
        final HttpStatus status = HttpStatus.valueOf(urlAndStatus.status);
        if (!status.is2xxSuccessful()) {
          log.info(
              "Couldn't clean up before test for url {} . got status {}/{}",
              urlAndStatus.url,
              status.value(),
              status.getReasonPhrase());
          return false;
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("Was not able to clean up all urls before test.", e);
        return false;
      }
    }
    return true;
  }

  public static void removeClientOnCleanupTrigger() {
    cleanUpClient.remove();
  }

  private record UrlAndResponseStatus(String url, int status) {}

  private static List<Callable<UrlAndResponseStatus>> createCalls(Set<String> urlToTrigger) {
    return urlToTrigger.stream()
        .map(
            url ->
                (Callable<UrlAndResponseStatus>)
                    () -> {
                      HttpPost post = new HttpPost(url);
                      post.setHeader(TEST_CASE_ID_HEADER, getTestcaseId());
                      try (CloseableHttpResponse response = cleanUpClient.get().execute(post)) {
                        int status = response.getStatusLine().getStatusCode();
                        return new UrlAndResponseStatus(url, status);
                      }
                    })
        .toList();
  }

  @SneakyThrows
  private static Set<String> getAllApisUsedInTestSteps(List<TestStep> testSteps) {
    Optional<DataTableArgument> dataTable = getDataTable(testSteps);
    if (dataTable.isEmpty()) {
      return Set.of();
    }
    List<String> apiUrls = getApiFromDataTable(dataTable.get());
    assertThat(combineItemsUrls)
        .as("Unknown api. You tried to call an api that is not included in combine_item.json.")
        .containsAll(apiUrls);
    List<String> httpReadyUrls = apiUrls.stream().map(GlueUtils::prepareApiNameForHttp).toList();
    Set<String> allUrlsForTestSteps = new HashSet<>(httpReadyUrls);
    allUrlsForTestSteps.addAll(getOrgAdminApis(apiUrls));
    return allUrlsForTestSteps;
  }

  private static @NotNull Set<String> getOrgAdminApis(List<String> apiUrls) {
    Set<String> orgAdminApis = new HashSet<>();
    for (String apiUrl : apiUrls) {
      CombineItem apiInformation = toCombineItem(apiUrl);
      Optional<CombineItem> orgAdminInformation =
          getOrgAdminForHomeServer(apiInformation.getProperties().get(HOME_SERVER_PROPERTY));
      orgAdminInformation.ifPresent(
          combineItem -> orgAdminApis.add(prepareApiNameForHttp(combineItem.getValue())));
    }
    return orgAdminApis;
  }

  private static Optional<DataTableArgument> getDataTable(List<TestStep> testSteps) {
    return testSteps.stream()
        .filter(PickleStepTestStep.class::isInstance)
        .map(PickleStepTestStep.class::cast)
        .filter(pickleStepTestStep -> pickleStepTestStep.getStep().getArgument() != null)
        .filter(
            pickleStepTestStep -> pickleStepTestStep.getStep().getText().equals(CLAIMING_STEP_TEXT))
        .map(pickleStepTestStep -> pickleStepTestStep.getStep().getArgument())
        .filter(DataTableArgument.class::isInstance)
        .map(DataTableArgument.class::cast)
        .findFirst();
  }

  @NotNull
  private static List<String> getApiFromDataTable(DataTableArgument dataTable) {
    return dataTable.cells().stream().map(cell -> cell.get(2)).toList();
  }

  private static Optional<CombineItem> getOrgAdminForHomeServer(String homeServer) {
    return combineItems.stream()
        .filter(
            item ->
                item.getProperties().get(HOME_SERVER_PROPERTY).equals(homeServer)
                    && item.getTags().contains(ORG_ADMIN_TAG))
        .findFirst();
  }

  private static CombineItem toCombineItem(String apiUrl) {
    return combineItems.stream().filter(item -> item.isSameAs(apiUrl)).findAny().orElseThrow();
  }
}
