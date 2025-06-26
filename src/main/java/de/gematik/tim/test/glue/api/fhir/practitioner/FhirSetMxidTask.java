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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ENDPOINT_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SET_MXID_PRACTITIONER;
import static de.gematik.tim.test.glue.api.fhir.practitioner.CanDeleteOwnMxidAbility.deleteOwnMxid;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.CREATED;

import de.gematik.tim.test.models.FhirEndpointDTO;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class FhirSetMxidTask implements Task {

  public static FhirSetMxidTask setMxid() {
    return new FhirSetMxidTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(SET_MXID_PRACTITIONER.request());
    Response res = lastResponse();
    if (res.statusCode() == CREATED.value()) {
      actor.can(deleteOwnMxid());
      FhirEndpointDTO endpoint = parseResponse(FhirEndpointDTO.class);
      actor.remember(OWN_ENDPOINT_ID, endpoint.getId());
      addEndpoint(actor.recall(DISPLAY_NAME), endpoint);
    }
  }
}
