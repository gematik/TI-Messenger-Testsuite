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
package de.gematik.tim.test.glue.api.message;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DIRECT_CHAT_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirOrgAdminGlue.findsAddressInHealthcareService;
import static de.gematik.tim.test.glue.api.message.DeleteMessageTask.deleteMessageWithId;
import static de.gematik.tim.test.glue.api.message.EditMessageTask.editMessage;
import static de.gematik.tim.test.glue.api.message.GetLastOwnMessageFromRoomQuestion.lastOwnMessage;
import static de.gematik.tim.test.glue.api.message.GetNoMessageQuestion.noMessageFromSenderWithTextInActiveRoom;
import static de.gematik.tim.test.glue.api.message.GetRoomMessageQuestion.messageFromSenderWithTextInActiveRoom;
import static de.gematik.tim.test.glue.api.message.GetRoomMessagesQuestion.messagesInActiveRoom;
import static de.gematik.tim.test.glue.api.message.SendDirectMessageTask.sendDirectMessageTo;
import static de.gematik.tim.test.glue.api.message.SendDirectMessageToMxIdTask.sendDirectMessageToMxIdOutOfFederation;
import static de.gematik.tim.test.glue.api.message.SendMessageTask.sendMessage;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.checkRoomMembershipState;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.checkRoomMembershipStateInDirectChatOf;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.checkRoomVersion;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.filterMessageForSenderAndText;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getRoomBetweenTwoActors;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getCreatedMessage;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getInternalRoomNameForActor;
import static java.lang.String.format;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import io.cucumber.java.Before;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import org.apache.commons.lang3.NotImplementedException;

public class MessageControllerGlue {

    private static final String geoUri = "geo:52.5229073,13.3875704";

    @Before
    public void setup() {
        if (TestcasePropertiesManager.isDryRun()) {
            return;
        }
        setTheStage(Cast.ofStandardActors());
    }

    @When("{string} sends message {string} in room {string}")
    @Wenn("{string} sendet die Nachricht {string} an den Raum {string}")
    public void sendsMessageInRoom(String actorName, String messageText, String roomName) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        actor.attemptsTo(sendMessage(messageText));
        checkResponseCode(actorName, OK.value());
        checkRoomMembershipState(actor, roomName);
    }

    @When("{string} writes {string} directly {string}")
    @Wenn("{string} schreibt {string} direkt {string}")
    public void sendDirectMessage(String actorName, String userName, String message) {
        Actor actor1 = theActorCalled(actorName);
        Actor actor2 = theActorCalled(userName);
        actor1.attemptsTo(sendDirectMessageTo(actor2, message));
        checkResponseCode(actorName, OK.value());
        RoomDTO room = checkRoomMembershipStateInDirectChatOf(actorName);
        checkRoomVersion(room);
    }

    @When("{string} writes {string} via healthcare service {string} directly {string}")
    @Wenn("{string} schreibt {string} über den Healthcare-Service {string} direkt {string}")
    public void writesDirectlyToHealthcareService(
            String actorName, String userName, String hsName, String message) {
        findsAddressInHealthcareService(actorName, userName, hsName);
        sendDirectMessage(actorName, userName, message);
        checkRoomMembershipStateInDirectChatOf(actorName);
    }

    @When("{string} tries to write {string} directly {string}")
    @Wenn("{string} versucht {string} direkt {string} zu schreiben")
    public void triesToWriteDirectly(String actorName, String userName, String message) {
        Actor actor1 = theActorCalled(actorName);
        Actor actor2 = theActorCalled(userName);
        actor1.attemptsTo(sendDirectMessageTo(actor2, message));
        checkResponseCode(actorName, FORBIDDEN.value());
    }

    @Then("{string} tries to write to MXID {string} directly {string}")
    @Dann("{string} versucht der MXID {string} direkt {string} zu schreiben")
    public void directMessageMxid(String actorName, String mxid, String message) {
        Actor actor = theActorCalled(actorName);
        actor.attemptsTo(sendDirectMessageToMxIdOutOfFederation(mxid, message));
        checkResponseCode(actorName, FORBIDDEN.value());
    }

    @When("{string} does not have the rights to contact {string}")
    @Wenn("{string} ist nicht berechtigt {string} zu kontaktieren")
    public void notAuthorizedForCommunication(String actorName, String userName) {
        checkResponseCode(actorName, FORBIDDEN.value());
    }

    @Then("{listOfStrings} can see message {string} from {string} in room {string}")
    @Then("{listOfStrings} can see messages {string} from {string} in room {string}")
    @Dann("{listOfStrings} empfängt eine Nachricht {string} von {string} im Raum {string}")
    @Dann("{listOfStrings} empfangen eine Nachricht {string} von {string} im Raum {string}")
    public void canSeeMessagesInRoom(
            List<String> actorNames, String messageText, String authorName, String roomName) {
        String authorId = theActorCalled(authorName).recall(MX_ID);
        actorNames.forEach(
                actorName -> {
                    Actor actor = theActorCalled(actorName);
                    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
                    assertThat(actor.asksFor(messageFromSenderWithTextInActiveRoom(messageText, authorId)))
                            .isPresent();
                    checkRoomMembershipState(actor, roomName);
                });
    }

    @Then("{string} receives a message {string} from {string}")
    @Dann("{string} empfängt eine Nachricht {string} von {string}")
    public void receiveMessageChat(String actorName, String textMessage, String senderName) {
        Actor actor = theActorCalled(actorName);
        RoomDTO room = getRoomBetweenTwoActors(actor, senderName);
        actor.abilityTo(UseRoomAbility.class).setActive(getInternalRoomNameForActor(room, actor));
        actor.asksFor(
                messageFromSenderWithTextInActiveRoom(
                        textMessage, theActorCalled(senderName).recall(MX_ID)));
        checkRoomMembershipState(room);
    }

    @Then("{string} receives the message {string} in the chat with {string}")
    @Dann("{string} empfängt seine Nachricht {string} im Chat mit {string}")
    public void findOwnMessageInChat(String actorName, String messageText, String userName) {
        Actor actor = theActorCalled(actorName);
        String roomName = DIRECT_CHAT_NAME + theActorCalled(userName).recall(MX_ID);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        List<MessageDTO> messages = actor.asksFor(messagesInActiveRoom());
        assertThat(messages).extracting("body").contains(getCreatedMessage(messageText).getBody());
        checkRoomMembershipState(actor, roomName);
    }

    @Wenn(
            "{string} could not see message {string} from {string} in the chat with {string} [Retry {long} - {long}]")
    @Dann(
            "{string} kann die Nachricht {string} von {string} im Chat mit {string} nicht sehen [Retry {long} - {long}]")
    public void canNotSeeChatMessage(
            String actorName,
            String messageText,
            String messageAuthor,
            String chatPartner,
            Long timeout,
            Long pollInterval) {
        Actor actor = theActorCalled(actorName);
        String roomName = actor.recall(DIRECT_CHAT_NAME + theActorCalled(chatPartner).recall(MX_ID));
        cantFindMessageInRoom(actorName, messageText, messageAuthor, roomName, timeout, pollInterval);
    }

    @Then(
            "{string} could not see more message {string} from {string} in chat with {string} [Retry {long} - {long}]")
    @Dann(
            "{string} kann die Nachricht {string} von {string} im Chat mit {string} nicht mehr sehen [Retry {long} - {long}]")
    public void canNotSeeChatMessageAnymore(
            String actorName,
            String messageText,
            String messageAuthor,
            String chatPartner,
            Long timeout,
            Long pollInterval) {
        Actor actor = theActorCalled(actorName);
        String roomName = DIRECT_CHAT_NAME + theActorCalled(chatPartner).recall(MX_ID);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        canNotFindMessageAnymore(messageText, messageAuthor, timeout, pollInterval, actor);
    }

    @Then(
            "{string} could not find message {string} from {string} in room {string} [Retry {long} - {long}]")
    @Dann(
            "{string} kann die Nachricht {string} von {string} im Raum {string} nicht sehen [Retry {long} - {long}]")
    public void cantFindMessageInRoom(
            String actorName,
            String messageText,
            String userName,
            String roomName,
            Long timeout,
            Long pollInterval) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        actor.asksFor(
                noMessageFromSenderWithTextInActiveRoom(messageText, theActorCalled(userName).recall(MX_ID))
                        .withCustomInterval(timeout, pollInterval));
    }

    @Then(
            "{string} could not find message {string} from {string} in room {string} anymore [Retry {long} - {long}]")
    @Dann(
            "{string} kann die Nachricht {string} von {string} im Raum {string} nicht mehr sehen [Retry {long} - {long}]")
    public void cantFindMessageInRoomAnymore(
            String actorName,
            String messageText,
            String messageAuthor,
            String roomName,
            Long timeout,
            Long pollInterval) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        canNotFindMessageAnymore(messageText, messageAuthor, timeout, pollInterval, actor);
    }

    private void canNotFindMessageAnymore(
            String messageText, String messageAuthor, Long timeout, Long pollInterval, Actor actor) {
        actor.asksFor(
                noMessageFromSenderWithTextInActiveRoom(
                        messageText, theActorCalled(messageAuthor).recall(MX_ID))
                        .withCustomInterval(timeout, pollInterval));
    }

    @When("{string} deletes their message {string} in chat with {string}")
    @Wenn("{string} löscht seine Nachricht {string} im Chat mit {string}")
    public void deleteMessageInChat(String actorName, String messageText, String userName) {
        deleteMessageInRoom(
                actorName, messageText, DIRECT_CHAT_NAME + theActorCalled(userName).recall(MX_ID));
    }

    @When("{string} deletes their message {string} in the room with {string}")
    @Wenn("{string} löscht seine Nachricht {string} im Raum {string}")
    public void deleteMessageInRoom(String actorName, String messageName, String roomName) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        List<MessageDTO> messages = actor.asksFor(messagesInActiveRoom());
        MessageDTO message =
                filterMessageForSenderAndText(
                        getCreatedMessage(messageName).getBody(), actorName, messages);
        actor.attemptsTo(deleteMessageWithId(message.getMessageId()));
        checkResponseCode(actorName, NO_CONTENT.value());
    }

    @When("{string} edits her last sent message in room {string} to {string}")
    @Wenn("{string} ändert seine letzte Nachricht im Raum {string} in {string}")
    public void editsHerLastSentMessageTo(String actorName, String roomName, String messageText) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        MessageDTO message = actor.asksFor(lastOwnMessage());
        actor.attemptsTo(editMessage().withMessage(messageText).withMessageId(message.getMessageId()));
        checkResponseCode(actorName, OK.value());
    }

    @Then("{string} edits her last sent message in chat with {string} to {string}")
    @Dann("{string} ändert seine letzte Nachricht im Chat mit {string} in {string}")
    public void editsLastSentMessageInChat(String actorName, String chatUser, String messageText) {
        editsHerLastSentMessageTo(
                actorName, DIRECT_CHAT_NAME + theActorCalled(chatUser).recall(MX_ID), messageText);
    }

    private void checkRequiredProperties(MessageDTO receivedMessage, String msgType) {
        assertThat(receivedMessage.getEventId()).isNotNull().isNotEmpty();
        assertThat(receivedMessage.getMsgtype()).isEqualTo(msgType);
        assertThat(receivedMessage.getType()).isEqualTo("m.room.message");
        if (receivedMessage.getFormat() != null && !receivedMessage.getFormat().isEmpty()) {
            assertThat(receivedMessage.getFormattedBody()).isNotNull().isNotEmpty();
        } else if (msgType.equals("m.location")) {
            assertThat(receivedMessage.getGeoUri()).isEqualTo(geoUri);
        }
    }

    @Then("{string} writes {string} directly {string} as {string}")
    @Dann("{string} schreibt {string} direkt {string} als {string}")
    public void sendMessageToChatWithMsgType(String actorName, String userName, String message, String msgType) {
        Actor sender = theActorCalled(actorName);
        Actor receiver = theActorCalled(userName);
        sender.attemptsTo(sendDirectMessageTo(receiver, message)
                .withMsgType(msgType));
        RoomDTO room = checkRoomMembershipStateInDirectChatOf(actorName);
        checkRoomVersion(room);
        checksForSendMessageWithMsgType(actorName, message, msgType);
    }

    @Then("{string} sends the message {string} as {string} to the room {string}")
    @Dann("{string} sendet die Nachricht {string} als {string} an den Raum {string}")
    public void sendMessageToRoomWithMsgType(String actorName, String message, String msgType, String roomName) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        actor.attemptsTo(
                sendMessage(message)
                        .withMsgType(msgType));
        checkRoomMembershipState(actor, roomName);
        checksForSendMessageWithMsgType(actorName, message, msgType);
    }

    private void checksForSendMessageWithMsgType(String actorName, String message, String msgType) {
        if (!msgType.equals("m.text") && !msgType.equals("m.emote") && !msgType.equals("m.notice")) {
            throw new NotImplementedException(
                    "Unknown message type. Add a new messageType in the glue step to support this message.");
        }
        checkResponseCode(actorName, OK.value());
        MessageDTO receivedMessage = getCreatedMessage(message);
        assertThat(receivedMessage.getBody()).isNotNull().isNotEmpty();
        checkRequiredProperties(receivedMessage, msgType);
    }

    @Then("{string} receives a message {string} from {string} as {string}")
    @Dann("{string} empfängt eine Nachricht {string} von {string} als {string}")
    public void getMessageFromChatWithMsgType(String actorName, String message, String userName, String msgType) {
        Actor actor = theActorCalled(actorName);
        RoomDTO room = getRoomBetweenTwoActors(actor, userName);
        String roomName = getInternalRoomNameForActor(room, actor);
        getMessageFromRoomWithMsgType(actorName, message, msgType, userName, roomName);
    }

    @Then("{string} receives a message {string} as {string} from {string} in room {string}")
    @Dann("{string} empfängt eine Nachricht {string} als {string} von {string} im Raum {string}")
    public void getMessageFromRoomWithMsgType(String actorName, String message, String msgType, String userName, String roomName) {
        Actor actor = theActorCalled(actorName);
        String senderMxId = theActorCalled(userName).recall(MX_ID);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        Optional<MessageDTO> optionalMessage = actor.asksFor(
                messageFromSenderWithTextInActiveRoom(
                        message, senderMxId));
        if (optionalMessage.isEmpty()) {
            throw new TestRunException(
                    format("Could not find message %s from sender %s", message, senderMxId));
        }
        MessageDTO receivedMessage = optionalMessage.get();
        checkRoomMembershipState(actor, roomName);
        checkResponseCode(actorName, OK.value());
        assertThat(receivedMessage.getBody()).isEqualTo(getCreatedMessage(message).getBody());
        checkRequiredProperties(receivedMessage, msgType);
    }


    @Then("{string} sends {string} the location {string} as {string}.")
    @Dann("{string} sendet {string} die Location {string} als {string}")
    public void sendLocationToChatWithMsgType(String actorName, String userName, String location, String msgType) {
        Actor sender = theActorCalled(actorName);
        Actor receiver = theActorCalled(userName);
        sender.attemptsTo(sendDirectMessageTo(receiver, location)
                .withMsgType(msgType)
                .withGeoUri(geoUri));
        RoomDTO room = checkRoomMembershipStateInDirectChatOf(actorName);
        checkRoomVersion(room);
        checkResponseCode(actorName, OK.value());
        MessageDTO receivedMessage = getCreatedMessage(location);
        assertThat(receivedMessage.getBody()).isNotNull().isNotEmpty();
        checkRequiredProperties(receivedMessage, msgType);
    }

    @Then("{string} sends the location {string} as {string} to the room {string}.")
    @Dann("{string} sendet die Location {string} als {string} an den Raum {string}")
    public void sendLocationToRoomWithMsgType(String actorName, String location, String msgType, String roomName) {
        Actor actor = theActorCalled(actorName);
        actor.abilityTo(UseRoomAbility.class).setActive(roomName);
        actor.attemptsTo(
                sendMessage(location)
                        .withMsgType(msgType)
                        .withGeoUri(geoUri));
        checkRoomMembershipState(actor, roomName);
        checkResponseCode(actorName, OK.value());
        MessageDTO receivedMessage = getCreatedMessage(location);
        assertThat(receivedMessage.getBody()).isNotNull().isNotEmpty();
        checkRequiredProperties(receivedMessage, msgType);
    }

    @Then("{string} receives the location {string} from {string} as {string}.")
    @Dann("{string} empfängt die Location {string} von {string} als {string}")
    public void getLocationToChatWithMsgType(String actorName, String location, String userName, String msgType) {
        Actor actor = theActorCalled(actorName);
        RoomDTO room = getRoomBetweenTwoActors(actor, userName);
        String roomName = getInternalRoomNameForActor(room, actor);
        getLocationToRoomWithMsgType(actorName, location, msgType, userName, roomName);
    }

    @Then("{string} receives a location {string} as {string} from {string} in room {string}")
    @Dann("{string} empfängt die Location {string} als {string} von {string} im Raum {string}")
    public void getLocationToRoomWithMsgType(String actorName, String location, String msgType, String userName, String roomName) {
        getMessageFromRoomWithMsgType(actorName, location, msgType, userName, roomName);
    }
}

