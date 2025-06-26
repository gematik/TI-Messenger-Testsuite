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

import de.gematik.tim.test.models.MessageContentFileDTO;
import de.gematik.tim.test.models.MessageContentFileKeyDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageContentFileWrapper {

    private final MessageContentFileDTO messageContentFile;

    public MessageContentFileWrapper(MessageContentFileDTO messageContentFile) {
        this.messageContentFile = messageContentFile;
    }

    public void checkFileFields(){
        assertThat(messageContentFile.getUrl()).as("Field url is required").isNotNull().isNotEmpty();
        assertThat(messageContentFile.getKey()).as("Field key is required").isNotNull();
        assertThat(messageContentFile.getIv()).as("Field iv is required").isNotNull().isNotEmpty();
        assertThat(messageContentFile.getHashes()).as("Field hashes is required").isNotNull();
        assertThat(messageContentFile.getV()).as("Field v is required").isNotNull().isNotEmpty();
        assertThat(messageContentFile.getHashes().getSha256()).as("Field sha256 is required").isNotNull().isNotEmpty();
        checkKeyFields();
    }

    public void checkKeyFields(){
        MessageContentFileKeyDTO key = messageContentFile.getKey();
        assertThat(key.getAlg()).as("Field alg is required").isNotNull().isNotEmpty();
        assertThat(key.getExt()).as("Field ext is required").isNotNull();
        assertThat(key.getK()).as("Field k is required").isNotNull().isNotEmpty();
        assertThat(key.getKeyOps()).as("Field key_ops is required").isNotNull().isNotEmpty();
        assertThat(key.getKty()).as("Field kty is required").isNotNull().isNotEmpty();
    }
}