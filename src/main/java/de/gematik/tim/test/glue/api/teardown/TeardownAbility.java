/*
 * Copyright 2024 gematik GmbH
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
 */

package de.gematik.tim.test.glue.api.teardown;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.login.LoginTask.login;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addFailedActor;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.isActorFailed;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.glue.api.devices.UseDeviceAbility;
import de.gematik.tim.test.glue.api.login.IsLoggedInAbility;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.HasTeardown;
import net.serenitybdd.screenplay.RefersToActor;
import org.opentest4j.AssertionFailedError;

@Slf4j
public abstract class TeardownAbility implements RefersToActor, Ability, HasTeardown {

  @Getter protected Actor actor;
  @Setter protected boolean tearedDown = false;

  @Override
  public void tearDown() {
    if (tearedDown || isActorFailed(actor)) {
      return;
    }
    if (actor.recall(IS_LOGGED_IN) != null
        && !(boolean) actor.recall(IS_LOGGED_IN)
        && !(this instanceof UseDeviceAbility)
        && !(this instanceof IsLoggedInAbility)) {
      actor.attemptsTo(login());
    }
    for (Class<? extends TeardownAbility> abilityClass : TeardownOrder.before(this)) {
      TeardownAbility ability = actor.abilityTo(abilityClass);
      if (nonNull(ability) && !ability.tearedDown) {
        try {
          ability.tearDown();
        } catch (TeardownException | AssertionFailedError e) {
          log.error("Ability tearDown was not successful, proceeding", e);
          addFailedActor(actor);
        }
      }
    }
    try {
      teardownThis();
    } catch (TeardownException | AssertionFailedError e) {
      log.error("TearDown was not successful, proceeding", e);
      addFailedActor(actor);
    }
    tearedDown = true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Ability> T asActor(Actor actor) {
    this.actor = actor;
    return (T) this;
  }

  protected abstract void teardownThis();
}
