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

package de.gematik.tim.test.glue.api;

import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Wenn;
import lombok.AllArgsConstructor;

import static lombok.AccessLevel.PRIVATE;


@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

    @Wenn("{string} verlässt den Chat-Raum")
    public void verlässtDenChatRaum(String actorName) {
        //Implement me
    }

    @Dann("{string} bestätigt, dass {string} den Chat-Raum verlassen hat")
    public void bestätigtDassDenChatRaumVerlassenHat(String actorName, String userName) {
        //Implement me
    }
}


