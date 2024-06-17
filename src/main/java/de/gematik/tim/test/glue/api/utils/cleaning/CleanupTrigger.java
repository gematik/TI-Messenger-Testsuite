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

package de.gematik.tim.test.glue.api.utils.cleaning;

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
import java.util.stream.Collectors;
import kong.unirest.UnirestInstance;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CleanupTrigger {

  public static final String HOME_SERVER_PROPERTY = "homeserver";
  public static final String ORG_ADMIN_TAG = "orgAdmin";
  private static final List<CombineItem> items;
  private static final Set<String> values;
  private static final ThreadLocal<UnirestInstance> cleanUpClient =
      ThreadLocal.withInitial(ClientFactory::getCleanUpClient);
  private static final ExecutorService executorService = Executors.newFixedThreadPool(5);
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
      items = Arrays.stream(mapper.readValue(combineItemsString, CombineItem[].class)).toList();

      values = new HashSet<>();
      values.addAll(items.stream().map(CombineItem::getValue).filter(Objects::nonNull).toList());
      values.addAll(items.stream().map(CombineItem::getUrl).filter(Objects::nonNull).toList());
    } catch (IOException e) {
      throw new InitializationException(e.getMessage());
    }
  }

  @SneakyThrows
  @SuppressWarnings("java:S2142")
  public static void sendCleanupRequest(List<TestStep> testSteps) {
    Set<String> urlsToTrigger = getOrgAdminApisForApisUsedInTestSteps(testSteps);
    List<Callable<Integer>> calls = createCalls(urlsToTrigger);
    for (Future<Integer> future : executorService.invokeAll(calls)) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        log.error("Was not able to clean up before test.", e);
      }
    }
  }

  public static void removeClientOnCleanupTrigger() {
    cleanUpClient.remove();
  }

  private static List<Callable<Integer>> createCalls(Set<String> urlToTrigger) {
    return urlToTrigger.stream()
        .map(
            url ->
                (Callable<Integer>)
                    () ->
                        cleanUpClient
                            .get()
                            .post(url)
                            .header(TEST_CASE_ID_HEADER, getTestcaseId())
                            .asEmpty()
                            .getStatus())
        .toList();
  }

  @SneakyThrows
  private static Set<String> getOrgAdminApisForApisUsedInTestSteps(List<TestStep> testSteps) {
    Optional<DataTableArgument> dataTable =
        testSteps.stream()
            .filter(PickleStepTestStep.class::isInstance)
            .map(PickleStepTestStep.class::cast)
            .filter(pickleStepTestStep -> pickleStepTestStep.getStep().getArgument() != null)
            .filter(
                pickleStepTestStep ->
                    pickleStepTestStep.getStep().getText().equals(CLAIMING_STEP_TEXT))
            .map(pickleStepTestStep -> pickleStepTestStep.getStep().getArgument())
            .filter(DataTableArgument.class::isInstance)
            .map(DataTableArgument.class::cast)
            .findFirst();
    if (dataTable.isEmpty()) {
      return Set.of();
    }
    List<String> apiUrls = getApiFromDataTable(dataTable.get());
    assertThat(values)
        .as("Unknown api. You tried to call an api that is not included in combine_item.json.")
        .containsAll(apiUrls);
    return apiUrls.stream()
        .map(CleanupTrigger::toItem)
        .map(item -> item.getProperties().get(HOME_SERVER_PROPERTY))
        .map(CleanupTrigger::getOrgAdminForHomeServer)
        .collect(Collectors.toSet());
  }

  @NotNull
  private static List<String> getApiFromDataTable(DataTableArgument dataTable) {
    return dataTable.cells().stream().map(cell -> cell.get(2)).toList();
  }

  private static String getOrgAdminForHomeServer(String homeserver) {
    CombineItem orgadmin =
        items.stream()
            .filter(
                item ->
                    item.getProperties().get(HOME_SERVER_PROPERTY).equals(homeserver)
                        && item.getTags().contains(ORG_ADMIN_TAG))
            .findFirst()
            .orElseThrow(
                () -> new InitializationException("Did not find any Orgadmin for " + homeserver));
    return prepareApiNameForHttp(orgadmin.getValue());
  }

  private static CombineItem toItem(String apiUrl) {
    return items.stream().filter(item -> item.isSameAs(apiUrl)).findAny().orElseThrow();
  }
}
