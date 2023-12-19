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

import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.COMBINE_ITEMS_FILE_NAME;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.COMBINE_ITEMS_FILE_URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.logging.log4j.util.Strings.isBlank;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.InitializationException;
import de.gematik.tim.test.glue.api.threading.ThreadClientFactory;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestStep;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CleanupTrigger {

  public static final String HOME_SERVER_PROPERTY = "homeserver";
  public static final String ORG_ADMIN_TAG = "orgAdmin";
  private static final List<CombineItem> items;
  private static final Set<String> values;
  private static final ThreadLocal<OkHttpClient> client = ThreadLocal.withInitial(ThreadClientFactory::getOkHttpClient);
  private static final ExecutorService exService = Executors.newFixedThreadPool(5);

  static {
    try {
      File combinedItemsFile = new File("target/generated-combine/" + COMBINE_ITEMS_FILE_NAME);
      if (!combinedItemsFile.exists()) {
        combinedItemsFile = new File(COMBINE_ITEMS_FILE_URL);
      }
      String combineItemsString = FileUtils.readFileToString(combinedItemsFile, UTF_8);
      ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      items = Arrays.stream(om.readValue(combineItemsString, CombineItem[].class)).toList();

      values = new HashSet<>();
      values.addAll(items.stream().map(CombineItem::getValue).filter(Objects::nonNull).toList());
      values.addAll(items.stream().map(CombineItem::getUrl).filter(Objects::nonNull).toList());
    } catch (IOException e) {
      throw new InitializationException(e.getMessage());
    }
  }

  @SneakyThrows
  public static void sendCleanupRequest(List<TestStep> testSteps) {
    Set<String> urlsToTrigger = getOrgAdminApisForApisUsedInTestSteps(testSteps);
    List<Callable<Integer>> calls = createCalls(urlsToTrigger);
    exService.invokeAll(calls);
  }

  public static void endTestCase() {
    client.remove();
  }

  private static List<Callable<Integer>> createCalls(Set<String> urlToTrigger) {
    return urlToTrigger.stream().map(url -> (Callable<Integer>) () -> {
      Request request = new Request.Builder().post(RequestBody.create("", MediaType.get("application/json"))).url(url).build();
      try (Response res = client.get().newCall(request).execute()) {
        return res.code();
      }
    }).toList();
  }


  @SneakyThrows
  private static Set<String> getOrgAdminApisForApisUsedInTestSteps(List<TestStep> testSteps) {
    List<String> stepLines = testSteps.stream()
            .filter(PickleStepTestStep.class::isInstance)
            .map(t -> (PickleStepTestStep) t)
            .map(t -> t.getStep().getText()).toList();
    Set<String> inTestCalledUrls = values.stream().filter(v -> stepLines.stream().anyMatch(s -> s.contains(v))).collect(Collectors.toSet());
    return inTestCalledUrls.stream()
            .map(CleanupTrigger::toItem)
            .map(i -> i.getProperties().get(HOME_SERVER_PROPERTY))
            .map(CleanupTrigger::getOrgAdminForHomeServer)
            .collect(Collectors.toSet());
  }

  private static String getOrgAdminForHomeServer(String s) {
    CombineItem orgadmin = items.stream()
            .filter(i -> i.getProperties().get(HOME_SERVER_PROPERTY).equals(s) && i.getTags().contains(ORG_ADMIN_TAG))
            .findFirst()
            .orElseThrow(() -> new InitializationException("Did not found any Orgadmin for " + s));
    return isBlank(orgadmin.getUrl()) ? orgadmin.getValue() : orgadmin.getUrl();
  }

  private static CombineItem toItem(String s) {
    return items.stream().filter(i -> i.isSameAs(s)).findAny().orElseThrow();
  }

}
