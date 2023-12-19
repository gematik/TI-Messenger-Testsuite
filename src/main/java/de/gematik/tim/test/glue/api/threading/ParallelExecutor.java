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

import static java.util.Objects.isNull;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.serenitybdd.screenplay.Actor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParallelExecutor {

  @Setter
  private static ParallelExecution execution;
  private static final ExecutorService executor = newFixedThreadPool(10);
  private static final ThreadLocal<OkHttpClient> client =
      ThreadLocal.withInitial(ThreadClientFactory::getOkHttpClient);

  public static ParallelExecution parallel() {
    if (isNull(execution)) {
      setExecution(new ParallelExecution());
    }
    return execution;
  }

  public static synchronized boolean isClaimable(Long deviceId) {
    return execution.isClaimable(deviceId);
  }

  public static synchronized ThreadLocal<OkHttpClient> parallelClient() {
    return client;
  }

  public static void teardown() {
    client.remove();
    executor.shutdown();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class ParallelExecution {

    private final Map<Actor, CompletableFuture<ActorsNotes>> threads = new ConcurrentHashMap<>();
    private final Set<Long> notClaimable = new ConcurrentSkipListSet<>();

    public synchronized boolean isClaimable(Long deviceId) {
      return notClaimable.add(deviceId);
    }

    public void task(Actor actor, Parallel<ActorsNotes> task) {
      CompletableFuture<ActorsNotes> actorThread =
          threads.getOrDefault(actor, supplyAsync(() -> new ActorsNotes(actor), executor));
      threads.put(actor, actorThread.thenApplyAsync(task::parallel));
    }

    public void join() {
      if (threads.isEmpty()) {
        return;
      }
      threads.entrySet().forEach(this::joinNotesWithActor);
      setExecution(null);
    }

    private void joinNotesWithActor(Entry<Actor, CompletableFuture<ActorsNotes>> thread) {
      thread.getValue()
          .handle((notes, exception) -> {
            if (isNull(exception)) {
              return notes;
            }
            throw new TestRunException("parallel claim for actor '%s' failed"
                .formatted(thread.getKey().getName()), exception.getCause());
          })
          .thenAcceptBoth(
              getActorFrom(thread), (notes, actor) -> {
                notes.getNotepad().forEach(actor::remember);
                notes.getAbilities().forEach(actor::can);
              })
          .join();
    }

    @NotNull
    private static CompletableFuture<Actor> getActorFrom(Entry<Actor, ?> entry) {
      return completedFuture(entry.getKey());
    }
  }
}
