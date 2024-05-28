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

import static lombok.AccessLevel.PRIVATE;

import io.cucumber.java.de.Dann;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor(access = PRIVATE)
public class TransferGlue {

    @Dann("{listOfStrings} prüft, ob seinen Authorization Mode auf {string} gesetzt ist")
    public void checkAuthMode(List<String> actorNames, String authModeEnum) {
        //implement me
    }

    @Dann("{listOfStrings} setzt seinen Authorization Mode auf {string}")
    public void setAuthMode(List<String> actorNames, String authModeEnum) {
        //implement me
    }

    @Dann("{string} hinterlegt {listOfStrings} in der Blockliste")
    public void putUserInBlockList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    @Dann("{string} entfernt {listOfStrings} in der Blockliste")
    public void deleteUserInBlockList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    @Dann("{string} hinterlegt die Domain von {listOfStrings} in der Blockliste")
    public void putDomainInBlockList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    @Dann("{string} entfernt die Domain von {listOfStrings} in der Blockliste")
    public void deleteDomainInBlockList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    @Dann("{string} hinterlegt {listOfStrings} in der Allowliste")
    public void putUserInAllowList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    //Noch nicht in gebrauch, wird aber kommen :)
    @Dann("{string} entfernt {listOfStrings} in der Allowliste")
    public void deleteUserInAllowList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    @Dann("{string} hinterlegt die Domain von {listOfStrings} in der Allowliste")
    public void putDomainInAllowList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

    //Noch nicht in gebrauch, wird aber kommen :)
    @Dann("{string} entfernt die Domain von {listOfStrings} in der Allowliste")
    public void deleteDomainInAllowList(String actorNames, List<String> blockedUsers) {
        //implement me
    }

}
