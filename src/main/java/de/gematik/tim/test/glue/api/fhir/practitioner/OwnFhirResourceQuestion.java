/*
 * Copyright 20023 gematik GmbH
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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.READ_MXID_PRACTITIONER;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirPractitionerDTO;
import io.restassured.response.Response;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;


public class OwnFhirResourceQuestion implements Question <Optional<FhirPractitionerDTO>> {

  public static OwnFhirResourceQuestion ownFhirResource(){return new OwnFhirResourceQuestion();}

  @Override
  public Optional<FhirPractitionerDTO> answeredBy(Actor actor) {
    actor.attemptsTo(READ_MXID_PRACTITIONER.request());
    Response lastResponse = lastResponse();

    if(lastResponse.getStatusCode() == 404){
      return Optional.empty();
    }

    FhirPractitionerDTO fhirPractitionerDTO = lastResponse.body().as(FhirPractitionerDTO.class);
    actor.remember(LAST_RESPONSE, lastResponse);
    return Optional.of(fhirPractitionerDTO);
  }
}
