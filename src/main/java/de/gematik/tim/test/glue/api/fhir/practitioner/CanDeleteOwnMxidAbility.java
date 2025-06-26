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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirDeleteOwnMxidTask.deleteMxidFromFhir;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequestForTeardown;
import static java.lang.Boolean.TRUE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.teardown.TeardownAbility;
import java.util.Optional;
import org.springframework.http.HttpStatus;

public class CanDeleteOwnMxidAbility extends TeardownAbility {

  public static CanDeleteOwnMxidAbility deleteOwnMxid() {
    return new CanDeleteOwnMxidAbility();
  }

  @Override
  protected void teardownThis() {
    repeatedRequestForTeardown(this::deleteFromFhirVzd, actor);
  }

  private Optional<Boolean> deleteFromFhirVzd() {
    this.actor.attemptsTo(deleteMxidFromFhir());
    return HttpStatus.valueOf(lastResponse().statusCode()).is2xxSuccessful()
        ? Optional.of(TRUE)
        : Optional.empty();
  }
}
