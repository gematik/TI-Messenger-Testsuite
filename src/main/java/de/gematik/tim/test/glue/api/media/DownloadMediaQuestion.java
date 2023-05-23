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
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DOWNLOAD_MEDIA;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MEDIA_ID_VARIABLE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import java.util.UUID;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class DownloadMediaQuestion implements Question<byte[]> {

  private UUID fileId;

  public static DownloadMediaQuestion downloadMedia() {
    return new DownloadMediaQuestion();
  }

  public DownloadMediaQuestion withFileId(UUID fileId) {
    this.fileId = fileId;
    return this;
  }

  @Override
  public byte[] answeredBy(Actor actor) {
    UUID mediaId = fileId == null ? actor.recall(MEDIA_ID) : this.fileId;
    actor.attemptsTo(DOWNLOAD_MEDIA.request().with(s -> s.pathParam(MEDIA_ID_VARIABLE, mediaId)));
    return lastResponse().body().asByteArray();
  }
}
