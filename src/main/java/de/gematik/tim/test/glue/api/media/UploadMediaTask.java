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

package de.gematik.tim.test.glue.api.media;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MEDIA_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.UPLOAD_MEDIA;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.MediaFileIdDTO;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class UploadMediaTask implements Task {

  private byte[] media;

  public static UploadMediaTask uploadMedia() {
    return new UploadMediaTask();
  }

  public UploadMediaTask withMedia(byte[] media) {
    this.media = media;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(
        UPLOAD_MEDIA.request().with(req -> req.contentType(ContentType.BINARY).body(media)));

    Response response = lastResponse();
    assert (response.statusCode() / 100 == 2);
    String mediaId = response.body().as(MediaFileIdDTO.class).getFileId();
    actor.remember(MEDIA_ID, mediaId);
  }
}
