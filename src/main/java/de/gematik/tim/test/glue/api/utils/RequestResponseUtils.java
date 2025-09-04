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

import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addFailedActor;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.RUN_WITHOUT_RETRY;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.TIMEOUT;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.pollInterval;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.awaitility.Awaitility.await;

import de.gematik.tim.test.glue.api.exceptions.RequestedResourceNotAvailable;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.teardown.TeardownException;
import de.gematik.tim.test.models.FhirBaseResourceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import org.awaitility.core.ConditionTimeoutException;

@Slf4j
public class RequestResponseUtils {

  private RequestResponseUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request) {
    return repeatedRequest(request, "resource");
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request, String resourceType) {
    return repeatedRequest(request, resourceType, TIMEOUT, pollInterval);
  }

  public static <T> T repeatedRequestWithLongerTimeout(
      Supplier<Optional<T>> request, String resourceType, int factor) {
    return repeatedRequest(request, resourceType, TIMEOUT * factor, pollInterval);
  }

  public static <T> T repeatedRequest(
      Supplier<Optional<T>> request,
      String resourceType,
      Long customTimeout,
      Long customPollInterval) {
    if (customTimeout == null || customPollInterval == null) {
      customTimeout = TIMEOUT;
      customPollInterval = pollInterval;
    }
    if (customTimeout <= 1 || RUN_WITHOUT_RETRY) {
      return request
          .get()
          .orElseThrow(
              () ->
                  new TestRunException(
                      format("Asked for %s, but could not be found", resourceType)));
    }
    return await()
        .atMost(Duration.of(customTimeout, SECONDS))
        .pollDelay(0L, TimeUnit.SECONDS)
        .pollInSameThread()
        .pollInterval(Duration.of(customPollInterval, SECONDS))
        .until(request::get, Optional::isPresent)
        .orElseThrow(
            () ->
                new RequestedResourceNotAvailable(
                    format("Asked for %s, but could not be found", resourceType)));
  }

  public static void repeatedRequestForEmptyResult(
      BooleanSupplier request, Long customTimeout, Long customPollInterval) {
    if (customTimeout == null || customPollInterval == null) {
      customTimeout = TIMEOUT;
      customPollInterval = pollInterval;
    }
    if (customTimeout <= 1 || RUN_WITHOUT_RETRY) {
      if (!request.getAsBoolean()) {
        throw new TestRunException("Expected no result, but a result was found");
      }
    } else {
      await()
          .atMost(Duration.of(customTimeout, SECONDS))
          .pollDelay(0L, TimeUnit.SECONDS)
          .pollInSameThread()
          .pollInterval(Duration.of(customPollInterval, SECONDS))
          .until(request::getAsBoolean);
    }
  }

  @SuppressWarnings(
      "java:S2201") // Run without retry only used for internal CI, therefor we do not need result
  public static void repeatedRequestForTeardown(Supplier<Optional<Boolean>> request, Actor actor) {
    if (TIMEOUT <= 1 || RUN_WITHOUT_RETRY) {
      request
          .get()
          .orElseThrow(
              () ->
                  new TeardownException(
                      "Teardown failed for actor "
                          + actor.getName()
                          + "! Looks like you have tried to delete a resource that should not be available"));
      return;
    }
    try {
      await()
          .atMost(Duration.of(20, SECONDS))
          .pollDelay(0L, TimeUnit.SECONDS)
          .pollInSameThread()
          .pollInterval(Duration.of(5, SECONDS))
          .until(request::get, Optional::isPresent);
    } catch (ConditionTimeoutException ex) {
      log.warn("Could not teardown correctly actor {}!", actor.getName());
      addFailedActor(actor);
    }
  }

  public static <T> T parseResponse(Class<T> clazz) {
    try {
      if (FhirBaseResourceDTO.class.isAssignableFrom(clazz)
          || clazz.isInstance(new FhirSearchResultDTO())) {
        return lastResponse().as(clazz, TestsuiteInitializer.getFhirMapper());
      }
      return lastResponse().as(clazz);
    } catch (Exception e) {
      log.error("Could not parse response: ", e);
      throw new TestRunException(
          "Expected "
              + clazz.getSimpleName()
              + " but got:\n"
              + lastResponse().body().prettyPrint()
              + " with status code "
              + lastResponse().statusCode(),
          e);
    }
  }
}
