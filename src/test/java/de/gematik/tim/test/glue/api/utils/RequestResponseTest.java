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
package de.gematik.tim.test.glue.api.utils;

import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.HEALTHCARE_SERVICE;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.gematik.tim.test.glue.api.AbstractMockServerTest;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.models.ContactDTO;
import de.gematik.tim.test.models.FhirBaseResourceDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.restassured.response.Response;
import java.io.IOException;
import net.serenitybdd.rest.SerenityRest;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

class RequestResponseTest extends AbstractMockServerTest {

  private static final String FHIR_SEARCH_RESULT = "vzd-search-result.json";

  @BeforeAll
  static void initialise() {
    assertThat(TestsuiteInitializer.TIMEOUT)
        .as("loading the class runs the test suite setup")
        .isNotNull();
  }

  @BeforeEach
  void performRequest() throws IOException {
    mockResponseFromResource(FHIR_SEARCH_RESULT);
    SerenityRest.given().get(url).andReturn();
  }

  @Test
  void parseResponseLenientTest() {
    FhirSearchResultDTO result = parseResponse(FhirSearchResultDTO.class);
    assertThat(result.getResourceType()).isEqualTo("Bundle");
    assertThat(result.getTotal()).isEqualTo(1L);
    assertThat(result.getEntry()).hasSize(4);

    FhirBaseResourceDTO healthcareService = result.getEntry().get(0).getResource();
    assertThat(healthcareService).isInstanceOf(FhirHealthcareServiceDTO.class);
    assertThat(healthcareService.getResourceType()).isEqualTo(HEALTHCARE_SERVICE);
    assertThat(((FhirHealthcareServiceDTO) healthcareService).getName())
        .isEqualTo("Captain Pancake's Rainbow Clinic");
    assertThat(healthcareService.getMeta().getVersionId()).isEqualTo("4");

    FhirBaseResourceDTO endpoint = result.getEntry().get(1).getResource();
    assertThat(endpoint).isInstanceOf(FhirEndpointDTO.class);
    assertThat(endpoint.getResourceType()).isEqualTo(ENDPOINT);
    assertThat(((FhirEndpointDTO) endpoint).getAddress()).startsWith("matrix:u/");
  }

  @Test
  void defaultStrictMapperThrowsForFhirTest() {
    Response searchResult = lastResponse();
    UnrecognizedPropertyException exception =
        assertThrows(
            UnrecognizedPropertyException.class, () -> searchResult.as(FhirSearchResultDTO.class));

    assertThat(exception).isNotNull();
    AssertionsForClassTypes.assertThat(exception.getMessage())
        .contains(
            "Unrecognized property \"security\" (class de.gematik.tim.test.models.FhirMetaDTO)");
  }

  @Test
  void parseResponseStrictThrowsTest() {
    TestRunException exception =
        assertThrows(TestRunException.class, () -> parseResponse(ContactDTO.class));

    assertThat(exception).isNotNull();
    AssertionsForClassTypes.assertThat(exception.getMessage())
        .contains("Expected ContactDTO but got:");
  }
}
