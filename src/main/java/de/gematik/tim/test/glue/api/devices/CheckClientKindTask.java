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

package de.gematik.tim.test.glue.api.devices;

import static de.gematik.tim.test.glue.api.devices.ClientKind.CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.EPA_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.ORG_ADMIN;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRACTITIONER;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRO_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRO_PRACTITIONER;
import static de.gematik.tim.test.glue.api.info.ApiInfoQuestion.apiInfo;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.threading.ParallelTaskRunner;
import de.gematik.tim.test.models.InfoObjectDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

@RequiredArgsConstructor
public class CheckClientKindTask extends ParallelTaskRunner {

  private final List<ClientKind> kind;

  public static CheckClientKindTask checkIs(List<ClientKind> kinds) {
    return new CheckClientKindTask(kinds);
  }

  @Override
  public void runParallel() {
    this.performAs(actor);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    this.actor = actor;
    InfoObjectDTO info = apiInfo().withActor(actor).run();
    checkForKinds(info);
  }

  private void checkForKinds(InfoObjectDTO info) {
    if (kind.contains(ORG_ADMIN)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirOrganization())
          .as(
              "Claimed device failed for actor %s on api %s because api have no org admin privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
    if (kind.contains(CLIENT)) {
      assertThat(info.getClientInfo().getCanSendMessages())
          .as(
              "Claimed device failed for actor %s on api %s because api have no write messages privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
    if (kind.contains(PRACTITIONER)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirPractitioner())
          .as(
              "Claimed device failed for actor %s on api %s because api have no practitioner privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
    if (kind.contains(EPA_CLIENT)) {
      assertThat(info.getClientInfo().getIsInsurance())
          .as(
              "Claimed device failed for actor %s on api %s because api have no insurant privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
    if (kind.contains(PRO_CLIENT)) {
      assertThat(info.getClientInfo().getIsPro())
          .as(
              "Claimed device failed for actor %s on api %s because api have no pro version privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
    if (kind.contains(PRO_PRACTITIONER)) {
      assertThat(
              Boolean.TRUE.equals(info.getClientInfo().getIsPro())
                  && Boolean.TRUE.equals(info.getClientInfo().getCanAdministrateFhirPractitioner()))
          .as(
              "Claimed device failed for actor %s on api %s because api have no practitioner and pro version privileges! This information was received from the info endpoint."
                  .formatted(actor.getName(), actor.abilityTo(CallAnApi.class).resolve("")))
          .isTrue();
    }
  }
}
