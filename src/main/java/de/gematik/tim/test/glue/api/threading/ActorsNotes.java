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

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

@Getter
public final class ActorsNotes {

  private final String name;
  private final String api;
  private final String testcaseId = getTestcaseId();
  private final Map<String, Object> notepad = new HashMap<>();
  private final Set<Ability> abilities = new HashSet<>();

  public ActorsNotes(Actor actor) {
    this.name = actor.getName();
    this.api = actor.abilityTo(CallAnApi.class).resolve("");
    actor.recallAll().forEach(this::remember);
  }

  public void remember(String key, Object val) {
    notepad.put(key, val);
  }

  public Object recall(String key) {
    return notepad.get(key);
  }

  public boolean containsKey(String key) {
    return notepad.containsKey(key);
  }

  public void addAbility(Ability ability) {
    abilities.stream()
        .filter(a -> a.getClass().equals(ability.getClass()))
        .findAny()
        .ifPresent(a -> {
          throw new TestRunException(
              "actor '%s' already has ability %s".formatted(
                  name, a.getClass().getSimpleName()));
        });
    abilities.add(ability);
  }
}
