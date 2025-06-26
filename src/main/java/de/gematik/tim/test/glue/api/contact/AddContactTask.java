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

package de.gematik.tim.test.glue.api.contact;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.ADD_CONTACT;
import static de.gematik.tim.test.glue.api.contact.HasContactAbility.addContactToActor;
import static java.time.temporal.ChronoUnit.DAYS;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.ContactDTO;
import de.gematik.tim.test.models.ContactInviteSettingsDTO;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@RequiredArgsConstructor
public class AddContactTask implements Task {

  private final String contactId;

  public static AddContactTask addContact(String contactId) {
    return new AddContactTask(contactId);
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    ContactInviteSettingsDTO settings =
        new ContactInviteSettingsDTO()
            .start(Instant.now().getEpochSecond())
            .end(Instant.now().plus(1, DAYS).getEpochSecond());
    ContactDTO contact = new ContactDTO().mxid(this.contactId).inviteSettings(settings);

    actor.attemptsTo(ADD_CONTACT.request().with(req -> req.body(contact)));
    addContactToActor(contactId, contact, actor);
    RawDataStatistics.editContactManagement();
  }
}
