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

package de.gematik.tim.test.glue.api;

import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addHs;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addRoom;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareServiceInfo;
import de.gematik.tim.test.glue.api.teardown.TeardownAbility;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.RoomDTO;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public abstract class MultiTargetAbility<K, V> extends TeardownAbility {

  private final Map<K, V> targets = new HashMap<>();
  private Entry<K, V> currentTarget;

  private void addTarget(K key, V target) {
    requireNonNull(key);
    requireNonNull(target);
    targets.put(key, target);
  }

  protected void addAndSetActive(K key, V target) {
    addTarget(key, target);
    setActive(key);
    if (target instanceof HealthcareServiceInfo t) {
      addHs((String) key, t);
    }
    if (target instanceof RoomDTO t) {
      addRoom((String) key, t);
    }
    if (target instanceof FhirEndpointDTO t) {
      addEndpoint((String) key, t);
    }
  }

  public V getTarget(K key) {
    return targets.get(key);
  }

  public Set<Entry<K, V>> getAll() {
    return targets.entrySet();
  }

  protected void remove(K key) {
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

  protected void clear() {
    targets.clear();
  }

  @Override
  public void teardownThis() {
    update();
    clearDouble();
    for (K key : List.copyOf(targets.keySet())) {
      this.setActive(key);
      actor.attemptsTo(tearDownPerTarget(key));
    }
  }

  protected void update() {
    // default: no update necessary
  }

  protected void clearDouble() {
    // default: no doubles could appear
  }

  @SuppressWarnings("unused")
  protected Task tearDownPerTarget(K key) {
    return new Task() {
      public <T extends Actor> void performAs(T actor) {
        // default: no teardown action
      }
    };
  }
}
