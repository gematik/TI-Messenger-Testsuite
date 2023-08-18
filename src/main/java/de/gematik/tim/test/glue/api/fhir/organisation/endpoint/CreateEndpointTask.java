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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_ENDPOINT;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.addEndpointToActorForHS;
import static de.gematik.tim.test.models.FhirEndpointDTO.StatusEnum.ACTIVE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.springframework.http.HttpStatus.CREATED;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirCodingDTO;
import de.gematik.tim.test.models.FhirCodingEntryDTO;
import de.gematik.tim.test.models.FhirConnectionTypeDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirEndpointDTO.StatusEnum;
import io.restassured.response.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@RequiredArgsConstructor
public class CreateEndpointTask extends HealthcareSpecificTask {

  private final String name;
  private String mxId;
  private StatusEnum status;
  private List<String> payloadType;
  private String connectionType;
  private String managingOrganization;

  public static CreateEndpointTask addHealthcareServiceEndpoint(String endpointName) {
    return new CreateEndpointTask(endpointName);
  }

  public CreateEndpointTask withMxId(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public CreateEndpointTask withStatus(StatusEnum status) {
    this.status = status;
    return this;
  }

  public CreateEndpointTask withPayloadType(List<String> payloadType) {
    this.payloadType = payloadType;
    return this;
  }

  public CreateEndpointTask withConnectionType(String connectionType) {
    this.connectionType = connectionType;
    return this;
  }

  public CreateEndpointTask withManagingOrganization(String managingOrganization) {
    this.managingOrganization = managingOrganization;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    FhirEndpointDTO endpointDTO = buildEndpoint();

    super.performAs(actor);

    actor.attemptsTo(CREATE_ENDPOINT.request().with(req -> req.body(endpointDTO)));

    Response response = lastResponse();
    if (response.statusCode() == CREATED.value()) {
      FhirEndpointDTO endpoint = response.getBody().as(FhirEndpointDTO.class);
      addEndpointToActorForHS(actor, endpoint.getName(), endpoint.getId(), hsName);
    }
  }

  private FhirEndpointDTO buildEndpoint() {
    status = status == null ? ACTIVE : status;
    return new FhirEndpointDTO()
        .address(mxId)
        .name(name)
        .status(status)
        .payloadType(buildPayloadType())
        .connectionType(buildConnectionType());
  }

  private List<FhirCodingEntryDTO> buildPayloadType() {
    return List.of(
        new FhirCodingEntryDTO().coding(
            List.of(
                new FhirCodingDTO()
                    .code("tim-chat")
                    .system("https://gematik.de/fhir/directory/CodeSystem/EndpointDirectoryPayloadType")
                    .display("TI-Messenger chat"))
        ));
  }

  private FhirConnectionTypeDTO buildConnectionType() {
    return new FhirConnectionTypeDTO()
        .system("https://gematik.de/fhir/directory/CodeSystem/EndpointDirectoryConnectionType")
        .code("tim");
  }
}
