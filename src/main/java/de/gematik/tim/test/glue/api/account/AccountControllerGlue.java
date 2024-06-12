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

package de.gematik.tim.test.glue.api.account;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.HttpStatus.CREATED;

import io.cucumber.java.de.Dann;
import io.cucumber.java.en.Then;
import net.serenitybdd.screenplay.Actor;

public class AccountControllerGlue {

  @Then("account information for {string} is returned")
  @Dann("werden Accountinformationen für {string} zurückgegeben")
  public void returnsAccountInfo(String actorName) {
    Actor actor = theActorCalled(actorName);
    String mxid = actor.recall(MX_ID);

    checkResponseCode(actor.getName(), CREATED.value());
    lastResponse().then().assertThat().body("mxid", is(mxid));
  }
}
