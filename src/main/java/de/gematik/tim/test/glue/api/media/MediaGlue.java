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
import static de.gematik.tim.test.glue.api.media.DownloadMediaQuestion.downloadMedia;
import static de.gematik.tim.test.glue.api.media.UploadMediaTask.uploadMedia;
import static de.gematik.tim.test.glue.api.message.GetRoomMessageQuestion.messageFromSenderWithTextInActiveRoom;
import static de.gematik.tim.test.glue.api.message.SendMessageTask.sendMessage;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.checkRoomMembershipState;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getCreatedMessage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.models.MessageDTO;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class MediaGlue {

  public static final String RESOURCES_PATH = "src/test/resources/media/";

  @SneakyThrows
  @When("{string} sends an attachment {string} into the room {string}")
  @Wenn("{string} sendet ein Attachment {string} an den Raum {string}")
  public void sendsAttachmentToRoom(String actorName, String fileName, String roomName) {
    uploadsAMediaFile(actorName, fileName);
    Path path = new File(RESOURCES_PATH + fileName).toPath();
    Actor actor = theActorCalled(actorName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    actor.attemptsTo(sendMessage(fileName)
        .withFileId(actor.recall(MEDIA_ID))
        .withMimetype(Files.probeContentType(path))
        .withSize((int) Files.size(path))
        .withMsgType("m.file"));
    checkResponseCode(actorName, OK.value());
    checkRoomMembershipState(actor, roomName);
  }

  @SneakyThrows
  private void uploadsAMediaFile(String actorName, String media) {
    Path path = Path.of(RESOURCES_PATH + media);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      theActorCalled(actorName).attemptsTo(
          uploadMedia().withMedia(fis.readAllBytes()));
    }
    checkResponseCode(actorName, CREATED.value());
  }

  @SneakyThrows
  @SuppressWarnings("java:S3655")
  // It gets checked by asserThat(message).isPresent()
  @Then("{string} receives the attachment {string} from {string} in the room {string}")
  @Dann("{string} empfängt das Attachment {string} von {string} im Raum {string}")
  public void receiveAttachmentInRoom(String actorName, String fileName, String userName,
      String roomName) {
    Actor actor = theActorCalled(actorName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    String senderMxId = theActorCalled(userName).recall(MX_ID);
    Optional<MessageDTO> message = actor
        .asksFor(messageFromSenderWithTextInActiveRoom(fileName, senderMxId));

    assertThat(message).isPresent();

    byte[] receivedMedia = actor.asksFor(downloadMedia().withFileId(message.get().getFileId()));

    Path path = Path.of(RESOURCES_PATH + fileName);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      assertThat(receivedMedia).isEqualTo(fis.readAllBytes());
    }
    assertThat(message.get().getBody()).isEqualTo(getCreatedMessage(fileName).getBody());
    checkRoomMembershipState(actor, roomName);
  }

}
