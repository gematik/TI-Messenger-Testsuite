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

package de.gematik.tim.test.glue.api.media;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MEDIA_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.media.DownloadAuthenticatedMediaQuestion.downloadAuthenticatedMedia;
import static de.gematik.tim.test.glue.api.media.DownloadMediaQuestion.downloadMedia;
import static de.gematik.tim.test.glue.api.media.UploadMediaTask.uploadMedia;
import static de.gematik.tim.test.glue.api.message.GetRoomMessageQuestion.messageFromSenderWithTextInActiveRoom;
import static de.gematik.tim.test.glue.api.message.SendMessageTask.sendMessage;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.checkRoomMembershipState;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getCreatedMessage;
import static java.lang.String.format;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.models.MessageDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.lang3.NotImplementedException;

public class MediaGlue {

  public static final String RESOURCES_PATH = "src/test/resources/media/";

  @SneakyThrows
  @When("{string} sends an attachment {string} into the room {string}")
  @Wenn("{string} sendet ein Attachment {string} an den Raum {string}")
  public void sendsAttachmentToRoom(String actorName, String fileName, String roomName) {
    uploadsAMediaFile(actorName, fileName);
    Path path = new File(RESOURCES_PATH + fileName).toPath();
    String msgType;
    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
      msgType = "m.image";
    } else if (fileName.endsWith(".txt")) {
      msgType = "m.file";
    } else {
      throw new NotImplementedException(
          "Unknown file type. Add a new messageType in the glue step to support this file.");
    }
    Actor actor = theActorCalled(actorName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    actor.attemptsTo(
        sendMessage(fileName)
            .withFileId(actor.recall(MEDIA_ID))
            .withMimetype(Files.probeContentType(path))
            .withSize((int) Files.size(path))
            .withMsgType(msgType));
    checkResponseCode(actorName, OK.value());
    checkRoomMembershipState(actor, roomName);
  }

  @SneakyThrows
  private void uploadsAMediaFile(String actorName, String media) {
    Path path = Path.of(RESOURCES_PATH + media);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      theActorCalled(actorName).attemptsTo(uploadMedia().withMedia(fis.readAllBytes()));
    }
    checkResponseCode(actorName, CREATED.value());
  }

  @Then("{string} receives the attachment {string} from {string} in the room {string}")
  @Dann("{string} empfängt das Attachment {string} von {string} im Raum {string}")
  public void receiveAttachmentInRoom(
      String receiverName, String fileName, String senderName, String roomName) {
    Actor actor = theActorCalled(receiverName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    MessageDTO message = getMediaMessage(senderName, receiverName, fileName);
    byte[] receivedMedia = actor.asksFor(downloadMedia().withFileId(message.getFileId()));

    checkResponseCode(receiverName, OK.value());
    checkMediaIntegrity(fileName, receivedMedia, message);
    checkRoomMembershipState(actor, roomName);
  }

  @Then(
      "{string} receives an attachment {string} from {string} in the room {string} using matrix protocol v1.11")
  @Dann(
      "{string} empfängt das Attachment {string} von {string} im Raum {string} über Matrix-Protokoll v1.11")
  public void receiveAuthenticatedAttachmentInRoom(
      String receiverName, String fileName, String senderName, String roomName) {
    Actor actor = theActorCalled(receiverName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    MessageDTO message = getMediaMessage(senderName, receiverName, fileName);
    byte[] receivedMedia =
        actor.asksFor(downloadAuthenticatedMedia().withFileId(message.getFileId()));

    checkResponseCode(receiverName, OK.value());
    checkMediaIntegrity(fileName, receivedMedia, message);
    checkRoomMembershipState(actor, roomName);
  }

  private MessageDTO getMediaMessage(String senderName, String receiverName, String fileName) {
    Actor actor = theActorCalled(receiverName);
    String senderMxId = theActorCalled(senderName).recall(MX_ID);
    Optional<MessageDTO> message =
        actor.asksFor(messageFromSenderWithTextInActiveRoom(fileName, senderMxId));
    if (message.isEmpty()) {
      throw new TestRunException(
          format("Could not find message %s from sender %s", fileName, senderMxId));
    }
    return message.get();
  }

  @SneakyThrows
  private void checkMediaIntegrity(String fileName, byte[] receivedMedia, MessageDTO message) {
    Path path = Path.of(RESOURCES_PATH + fileName);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      assertThat(receivedMedia).isEqualTo(fis.readAllBytes());
    }
    assertThat(message.getBody()).isEqualTo(getCreatedMessage(fileName).getBody());
  }
}
