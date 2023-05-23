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

package de.gematik.tim.test.glue.api;

import static java.util.Objects.requireNonNull;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;
import net.serenitybdd.screenplay.Task;

public abstract class MultiTargetAbility<K, V> implements Ability, HasTeardown, RefersToActor {

  private final Map<K, V> targets = new HashMap<>();
  private Entry<K, V> currentTarget;

  private Actor actor;
  private boolean tearedDown = false;

  public void addTarget(K key, V target) {
    requireNonNull(key);
    requireNonNull(target);
    targets.put(key, target);
  }

  public void addAndSetActive(K key, V target) {
    addTarget(key, target);
    setActive(key);
  }

  public V getTarget(K key) {
    return targets.get(key);
  }

  public Set<Entry<K, V>> getAll() {
    return targets.entrySet();
  }

  public void remove(K key) {
    requireNonNull(key);
    V target = targets.remove(key);
    if (target != null && target.equals(currentTarget)) {
      currentTarget = null;
    }
  }

  public void removeCurrent() {
    if (currentTarget != null) {
      targets.remove(currentTarget.getKey());
    }
  }

  public V getActive() {
    return getActiveValue();
  }

  public V getActiveValue() {
    if (currentTarget == null) {
      return null;
    }
    return currentTarget.getValue();
  }

  public K getActiveKey() {
    if (currentTarget == null) {
      return null;
    }
    return currentTarget.getKey();
  }

  public V setActive(K key) {
    requireNonNull(key);
    V target = targets.get(key);
    requireNonNull(target, "No value registered for key %s".formatted(key));
    currentTarget = new SimpleImmutableEntry<>(key, target);
    return target;
  }


  public void clear() {
    targets.clear();
  }

  @Override
  public void tearDown() {
    if (tearedDown) {
      return;
    }
    for (var key : List.copyOf(targets.keySet())) {
      actor.attemptsTo(tearDownPerTarget(key));
    }
    tearedDown = true;
  }

  @SuppressWarnings("unused")
  protected Task tearDownPerTarget(K key) {
    return new Task() {
      public <T extends Actor> void performAs(T actor) {
        // default: no teardown action
      }
    };
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Ability> T asActor(Actor actor) {
    this.actor = actor;
    return (T) this;
  }
}
