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

package de.gematik.tim.test.glue.api.devices;

import static de.gematik.tim.test.glue.api.devices.ClientKind.MESSENGER_CLIENT;
import static de.gematik.tim.test.glue.api.devices.ClientKind.ORG_ADMIN;
import static de.gematik.tim.test.glue.api.devices.ClientKind.PRACTITIONER;
import static de.gematik.tim.test.glue.api.info.ApiInfoQuestion.apiInfo;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.threading.Parallel;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.models.InfoObjectDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class CheckClientKindTask implements Task, Parallel<ActorsNotes> {


  private final List<ClientKind> kind;

  public static CheckClientKindTask checkIs(List<ClientKind> kinds) {
    return new CheckClientKindTask(kinds);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    InfoObjectDTO info = actor.asksFor(apiInfo());
    checkForKinds(info);
  }

  @Override
  public ActorsNotes parallel(ActorsNotes notes) {
    InfoObjectDTO info = apiInfo().parallel(notes);
    checkForKinds(info);
    return notes;
  }

  private void checkForKinds(InfoObjectDTO info) {
    if (kind.contains(ORG_ADMIN)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirOrganization())
          .as("Claimed device have no org admin privileges! This information is got from the info endpoint.")
          .isTrue();
    }
    if (kind.contains(MESSENGER_CLIENT)) {
      assertThat(info.getClientInfo().getCanSendMessages())
          .as("Claimed device have no write messages privileges! This information is got from the info endpoint.")
          .isTrue();
    }
    if (kind.contains(PRACTITIONER)) {
      assertThat(info.getClientInfo().getCanAdministrateFhirPractitioner())
          .as("Claimed device have no practitioner privileges! This information is got from the info endpoint.")
          .isTrue();
    }
  }
}
