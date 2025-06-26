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

import static de.gematik.tim.test.glue.api.contact.DeleteContactTask.deleteContact;
import static java.util.Objects.isNull;

import de.gematik.tim.test.glue.api.MultiTargetAbility;
import de.gematik.tim.test.models.ContactDTO;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class HasContactAbility extends MultiTargetAbility<String, ContactDTO> {

  private HasContactAbility(String contactId, ContactDTO contact) {
    addAndSetActive(contactId, contact);
  }

  public static <T extends Actor> void addContactToActor(
      String contactId, ContactDTO contact, T actor) {
    HasContactAbility ability = actor.abilityTo(HasContactAbility.class);
    if (isNull(ability)) {
      ability = new HasContactAbility(contactId, contact);
      actor.can(ability);
    }
    ability.addAndSetActive(contactId, contact);
  }

  public static <T extends Actor> void removeContactFromActor(String contactId, T actor) {
    HasContactAbility ability = actor.abilityTo(HasContactAbility.class);
    if (ability != null) {
      ability.remove(contactId);
    }
  }

  @Override
  protected Task tearDownPerTarget(String contactId) {
    setActive(contactId);
    return deleteContact(contactId);
  }
}
