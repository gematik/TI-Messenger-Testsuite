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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DOWNLOAD_AUTHENTICATED_MEDIA;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MEDIA_ID_VARIABLE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class DownloadAuthenticatedMediaQuestion implements Question<byte[]> {

  private String fileId;

  public static DownloadAuthenticatedMediaQuestion downloadAuthenticatedMedia() {
    return new DownloadAuthenticatedMediaQuestion();
  }

  public DownloadAuthenticatedMediaQuestion withFileId(String fileId) {
    this.fileId = fileId;
    return this;
  }

  @Override
  public byte[] answeredBy(Actor actor) {
    String mediaId = fileId == null ? actor.recall(MEDIA_ID) : this.fileId;
    actor.attemptsTo(
        DOWNLOAD_AUTHENTICATED_MEDIA
            .request()
            .with(query -> query.pathParam(MEDIA_ID_VARIABLE, mediaId)));
    return lastResponse().body().asByteArray();
  }
}
