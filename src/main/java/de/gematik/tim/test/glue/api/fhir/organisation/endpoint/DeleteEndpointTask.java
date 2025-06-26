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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.DELETE_ENDPOINT;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.removeEndpointFromActor;
import static java.lang.String.format;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import io.restassured.response.Response;
import jxl.common.AssertionFailed;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;

@Slf4j
public class DeleteEndpointTask extends EndpointSpecificTask {

  public static DeleteEndpointTask deleteEndPoint() {
    return new DeleteEndpointTask();
  }

  @SneakyThrows
  @Override
  public <T extends Actor> void performAs(T actor) {
    try {
      super.performAs(actor);
      actor.attemptsTo(DELETE_ENDPOINT.request());

      Response response = lastResponse();
      if (response.statusCode() == NO_CONTENT.value()) {
        UseEndpointAbility useEndpointAbility = actor.abilityTo(UseEndpointAbility.class);
        removeEndpointFromActor(useEndpointAbility.getActiveKey(), actor);
      }
    } catch (RuntimeException e) {
      log.error(
          String.format(
              "Attempted to remove endpoint %s, but got status code: %d ",
              DELETE_ENDPOINT.getPath(), lastResponse().statusCode()));
      throw new AssertionFailed(
          format("Endpoint for mxId %s could not be deleted", actor.recall(MX_ID)));
    }
  }
}
