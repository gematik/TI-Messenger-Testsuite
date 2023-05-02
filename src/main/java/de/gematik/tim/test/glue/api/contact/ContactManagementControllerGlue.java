/*
 * Copyright 20023 gematik GmbH
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

package de.gematik.tim.test.glue.api.contact;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.contact.AddContactTask.addContact;
import static de.gematik.tim.test.glue.api.contact.ContactListQuestion.ownContactList;
import static de.gematik.tim.test.glue.api.contact.DeleteContactTask.deleteContact;
import static de.gematik.tim.test.glue.api.message.SendDirectMessageTask.sendDirectMessageTo;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.models.ContactDTO;
import de.gematik.tim.test.models.ContactsDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;

public class ContactManagementControllerGlue {

  @Then("{string} is notable to contact {string}")
  @Dann("{string} darf {string} nicht kontaktieren")
  public void isNotableToContact(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    actor.should(
        seeThatResponse("check status code", res -> res.statusCode(403)));
  }

  @Dann("{string} hinterlegt {string} in seiner Freigabeliste")
  @When("{string} put {string} into his contact management")
  public void putIntoHisContactManagement(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(addContact(theActorCalled(userName).recall(MX_ID)));
  }

  @Then("{string} is able to contact {string}")
  public void isAbleToContact(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    actor.should(
        seeThatResponse("check status code", res -> res.statusCode(200)));
  }

  @When("{string} tries to contact {string} directly {string}")
  public void triesToContactDirectly(String actorName, String userName, String message) {
    Actor actor = theActorCalled(actorName);
    Actor user = theActorCalled(userName);
    actor.attemptsTo(sendDirectMessageTo(user, message));
  }

  @Dann("{string} löscht {string} in seiner Freigabeliste")
  public void deletesFromHisContactManagement(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    String userMxid = theActorCalled(userName).recall(MX_ID);
    actor.attemptsTo(deleteContact(userMxid));
    ContactsDTO contacts = actor.asksFor(ownContactList());
    assertThat(contacts.getContacts()).extracting(ContactDTO::getMxid)
        .doesNotContainSequence(userMxid);
  }
}
