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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.contact.AddContactTask.addContact;
import static de.gematik.tim.test.glue.api.contact.ContactListQuestion.ownContactList;
import static de.gematik.tim.test.glue.api.contact.DeleteContactTask.deleteContact;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.models.ContactDTO;
import de.gematik.tim.test.models.ContactsDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import net.serenitybdd.screenplay.Actor;

public class ContactManagementControllerGlue {

  @Then("{string} puts {string} into their contact management")
  @Dann("{string} hinterlegt {string} in seiner Freigabeliste")
  public void putIntoHisContactManagement(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(addContact(theActorCalled(userName).recall(MX_ID)));
    checkResponseCode(actorName, OK.value());
  }

  @Then("{string} deletes {string} from their contact management")
  @Dann("{string} löscht {string} in seiner Freigabeliste")
  public void deletesFromHisContactManagement(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    String userMxid = theActorCalled(userName).recall(MX_ID);
    actor.attemptsTo(deleteContact(userMxid));
    ContactsDTO contacts = actor.asksFor(ownContactList());
    assertThat(contacts.getContacts())
        .extracting(ContactDTO::getMxid)
        .as(
            "User mit Mxid %s wurde nicht von Freigabeliste gelöscht",
            userMxid, contacts.getContacts())
        .doesNotContainSequence(userMxid);
  }
}
