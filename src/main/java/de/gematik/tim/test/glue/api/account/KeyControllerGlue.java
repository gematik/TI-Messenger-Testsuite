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

package de.gematik.tim.test.glue.api.account;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.SESSION_KEY;
import static de.gematik.tim.test.glue.api.account.SessionKeyImportTask.importKey;
import static de.gematik.tim.test.glue.api.account.SessionKeyQuestion.ownSessionKey;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import de.gematik.tim.test.models.MatrixKeyInfoDTO;
import io.cucumber.java.de.Und;
import io.cucumber.java.en.And;
import net.serenitybdd.screenplay.Actor;

public class KeyControllerGlue {

  @And("{string} exports their session key")
  @Und("{string} exportiert seinen Session-Key")
  public void exportSessionKey(String actorName) {
    Actor actor = theActorCalled(actorName);
    MatrixKeyInfoDTO keyInfo = actor.asksFor(ownSessionKey());
    actor.remember(SESSION_KEY, keyInfo.getKey());
  }

  @And("{string} imports their session key from {string}")
  @Und("{string} importiert den Session-Key von {string}")
  public void importSessionKey(String actorName, String userName) {
    Actor actor = theActorCalled(actorName);
    actor.attemptsTo(importKey(theActorCalled(userName).recall(SESSION_KEY)));
  }
}
