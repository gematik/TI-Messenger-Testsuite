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

package de.gematik.tim.test.glue.api.threading;

import static de.gematik.tim.test.glue.api.utils.IndividualLogger.individualLog;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static lombok.AccessLevel.PRIVATE;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import kong.unirest.UnirestInstance;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ParallelExecutor {

  private static final ExecutorService executor = newFixedThreadPool(10);
  private static final ThreadLocal<UnirestInstance> client =
      ThreadLocal.withInitial(ClientFactory::getClient);
  private static final ConcurrentHashMap<String, ConcurrentSkipListSet<Long>> devicesMap =
      new ConcurrentHashMap<>();
  private static final ConcurrentHashMap<String, Integer> lastResponses = new ConcurrentHashMap<>();

  @SuppressWarnings("java:S2142")
  public static void run(List<Callable<Void>> calls) {
    Exception exception = null;
    try {
      List<Future<Void>> parallelTasks = executor.invokeAll(calls);
      for (Future<Void> task : parallelTasks) {
        try {
          task.get();
        } catch (Exception e) {
          if (exception == null) {
            exception = e;
            for (Future<Void> taskToBeCancelled : parallelTasks) {
              if (taskToBeCancelled != task) {
                taskToBeCancelled.cancel(true);
              }
            }
          }
        }
      }
    } catch (InterruptedException e) {
      exception = e;
    }
    if (exception != null) {
      throw new TestRunException("Claiming parallel failed...", exception);
    }
  }

  public static ThreadLocal<UnirestInstance> getParallelClient() {
    return client;
  }

  public static boolean isClaimable(String api, Long deviceId) {
    individualLog(
        "Ask for DeviceID %s on APi: %s currently claimed: %s"
            .formatted(deviceId, api, devicesMap.get(api)));
    return devicesMap.computeIfAbsent(api, k -> new ConcurrentSkipListSet<>()).add(deviceId);
  }

  public static void saveLastResponseCode(String actorName, int statusCode) {
    lastResponses.put(actorName, statusCode);
  }

  public static Integer getLastResponseCodeForActor(String actorName) {
    return lastResponses.get(actorName);
  }

  public static void reset() {
    devicesMap.clear();
    lastResponses.clear();
  }

  public static void removeClientOnParallelExecutor() {
    client.remove();
  }
}
