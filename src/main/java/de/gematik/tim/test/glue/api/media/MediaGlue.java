/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.media;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MEDIA_ID;
import static de.gematik.tim.test.glue.api.media.DownloadMediaQuestion.downloadMedia;
import static de.gematik.tim.test.glue.api.media.UploadMediaTask.uploadMedia;
import static de.gematik.tim.test.glue.api.message.GetRoomMessagesQuestion.messagesInActiveRoom;
import static de.gematik.tim.test.glue.api.message.SendMessageTask.sendMessage;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.filterMessageForSenderAndText;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import de.gematik.tim.test.models.MessageDTO;
import io.cucumber.java.Before;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;

public class MediaGlue {

  public static final String RESOURCES_PATH = "src/test/resources/media/";

  @Before
  public void setup() {
    setTheStage(Cast.ofStandardActors());
  }

  @When("{string} uploads a media file with content {string}")
  public void uploadsAMedia(String actorName, String media) {
    theActorCalled(actorName).attemptsTo(uploadMedia().withMedia(media.getBytes()));
  }

  @Then("{string} can download media file with content {string}")
  public void canDownloadMedia(String actorName, String media) {
    byte[] receivedMedia = theActorCalled(actorName).asksFor(downloadMedia());
    assertThat(receivedMedia).isEqualTo(media.getBytes());
  }

  @SneakyThrows
  @When("{string} uploads a media file {string}")
  public void uploadsAMediaFile(String actorName, String media) {
    Path path = Path.of(RESOURCES_PATH + media);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      theActorCalled(actorName).attemptsTo(
          uploadMedia().withMedia(fis.readAllBytes()));
    }
  }

  @SneakyThrows
  @Then("{string} can download media file {string}")
  public void canDownloadMediaFile(String actorName, String media) {
    byte[] receivedMedia = theActorCalled(actorName).asksFor(downloadMedia());

    Path path = Path.of(RESOURCES_PATH + media);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      assertThat(receivedMedia).isEqualTo(fis.readAllBytes());
    }
  }


  @SneakyThrows
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
        .withMsgType("m." + Files.probeContentType(path).split("/")[0]));
  }

  @SneakyThrows
  @Dann("{string} empfängt das Attachment {string} von {string} im Raum {string}")
  public void receiveAttachmentInRoom(String actorName, String fileName, String userName,
      String roomName) {
    Actor actor = theActorCalled(actorName);
    actor.abilityTo(UseRoomAbility.class).setActive(roomName);
    List<MessageDTO> messages = actor.asksFor(messagesInActiveRoom());
    MessageDTO filteredMessages = filterMessageForSenderAndText(fileName, userName, messages);

    byte[] receivedMedia = actor.asksFor(downloadMedia().withFileId(filteredMessages.getFileId()));

    Path path = Path.of(RESOURCES_PATH + fileName);
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      assertThat(receivedMedia).isEqualTo(fis.readAllBytes());
    }
    assertThat(filteredMessages.getBody()).isEqualTo(fileName);
  }

}
