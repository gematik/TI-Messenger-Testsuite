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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.CREATE_ENDPOINT;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.addEndpointToActorForHS;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueEndpointName;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static de.gematik.tim.test.models.FhirEndpointDTO.StatusEnum.ACTIVE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.models.FhirCodeableConceptDTO;
import de.gematik.tim.test.models.FhirCodingDTO;
import de.gematik.tim.test.models.FhirConnectionTypeDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirEndpointDTO.StatusEnum;
import de.gematik.tim.test.models.FhirMetaDTO;
import de.gematik.tim.test.models.FhirTagDTO;
import io.restassured.response.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;

@RequiredArgsConstructor
public class CreateEndpointTask extends HealthcareSpecificTask {

  private final String name;
  private String mxId;
  private StatusEnum status;
  private String connectionTypeCode;

  public static CreateEndpointTask addHealthcareServiceEndpoint(String endpointName) {
    return new CreateEndpointTask(endpointName);
  }

  public CreateEndpointTask withMxIdAsUri(String mxId) {
    this.mxId = mxId;
    return this;
  }

  public CreateEndpointTask withConnectionTypeCode(String connectionTypeCode) {
    this.connectionTypeCode = connectionTypeCode;
    return this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    FhirEndpointDTO endpointDTO = buildEndpoint();

    super.performAs(actor);

    actor.attemptsTo(CREATE_ENDPOINT.request().with(req -> req.body(endpointDTO)));

    Response response = lastResponse();
    if (response.statusCode() == CREATED.value()) {
      FhirEndpointDTO endpoint = parseResponse(FhirEndpointDTO.class);
      assertThat(endpoint.getId())
          .as("Endpoint %s id is empty".formatted(endpoint.getName()))
          .isNotBlank();
      addEndpointToActorForHS(actor, name, endpoint.getId(), hsName);
      addEndpoint(name, endpoint);
    }
  }

  private FhirEndpointDTO buildEndpoint() {
    status = status == null ? ACTIVE : status;
    return new FhirEndpointDTO()
        .address(mxId)
        .name(createUniqueEndpointName())
        .status(status)
        .payloadType(buildPayloadType())
        .connectionType(buildConnectionType())
        .meta(defaultEndpointMeta());
  }

  private List<FhirCodeableConceptDTO> buildPayloadType() {
    return List.of(
        new FhirCodeableConceptDTO()
            .coding(
                List.of(
                    new FhirCodingDTO()
                        .code("tim-chat")
                        .system(
                            "https://gematik.de/fhir/directory/CodeSystem/EndpointDirectoryPayloadType")
                        .display("TI-Messenger chat"))));
  }

  private FhirConnectionTypeDTO buildConnectionType() {
    connectionTypeCode = connectionTypeCode == null ? "tim" : connectionTypeCode;
    return new FhirConnectionTypeDTO()
        .system("https://gematik.de/fhir/directory/CodeSystem/EndpointDirectoryConnectionType")
        .code(connectionTypeCode);
  }

  public static FhirMetaDTO defaultEndpointMeta() {
    return new FhirMetaDTO()
        .tag(
            List.of(
                new FhirTagDTO()
                    .code("owner")
                    .system("https://gematik.de/fhir/directory/CodeSystem/Origin")))
        .profile(
            List.of(
                "https://gematik.de/fhir/directory/StructureDefinition/EndpointDirectory",
                "http://hl7.org/fhir/StructureDefinition/Endpoint"));
  }
}
