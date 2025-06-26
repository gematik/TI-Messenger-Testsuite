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

import static java.util.Objects.requireNonNull;

import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.test.tiger.proxy.TigerProxy;
import io.restassured.RestAssured;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;
import net.serenitybdd.screenplay.rest.questions.RestQueryFunction;

@RequiredArgsConstructor
public class TestdriverApiInteraction implements Performable {

  private final RestInteraction restInteraction;
  private final List<Class<? extends TestdriverApiAbility>> neededAbilities;

  static {
    Optional<TigerProxy> proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOptional();
    proxy.ifPresent(tigerProxy -> RestAssured.trustStore(tigerProxy.buildTruststore()));
  }

  public TestdriverApiInteraction with(RestQueryFunction restConfiguration) {
    restInteraction.with(restConfiguration);
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    neededAbilities.forEach(
        abilityClass -> {
          TestdriverApiAbility ability = actor.abilityTo(abilityClass);
          requireNonNull(
              ability,
              "Actor '%s' needs the '%s' to perform this request!"
                  .formatted(actor.getName(), abilityClass.getSimpleName()));
          restInteraction.with(ability);
        });
    restInteraction.performAs(actor);
  }
}
