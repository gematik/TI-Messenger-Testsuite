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

package de.gematik.tim.test.glue.api.fhir;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.OWN_ENDPOINT_ID;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.useDevice;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.setTestcaseId;
import static net.serenitybdd.screenplay.actors.OnStage.drawTheCurtain;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

import de.gematik.tim.test.glue.api.AbstractMockServerTest;
import de.gematik.tim.test.glue.api.fhir.practitioner.FhirPractitionerSearchGlue;
import de.gematik.tim.test.models.FhirEndpointDTO;
import java.io.File;
import java.io.IOException;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FhirPractitionerSearchGlueTest extends AbstractMockServerTest {

  private static final String FHIR_SEARCH_RESULT = "searchPractitionerInFhirDirectory.json";
  private static final String PRACTITIONER_NAME_PLACEHOLDER = "${practitionerNameText}";
  private static final String BOB_DISPLAY_NAME = "Wärkelsheim, Marcel";
  private static final String BOB_MX_ID = "@McWärk:gematik.de";
  private static final String BOB_ENDPOINT_ID = "42";

  @BeforeEach
  void setUpSerenity(TestInfo testInfo) {
    StepEventBus eventBus = StepEventBus.getEventBus();
    eventBus.registerListener(new BaseStepListener(new File("directoryNotUsed")));
    eventBus.testSuiteStarted(FhirPractitionerSearchGlueTest.class);
    eventBus.testStarted(testInfo.getDisplayName(), FhirPractitionerSearchGlueTest.class);
    setTheStage(Cast.ofStandardActors());
    setTestcaseId("@TCID:" + getClass().getSimpleName());
  }

  @AfterEach
  void tearDownSerenity() {
    drawTheCurtain();
    StepEventBus eventBus = StepEventBus.getEventBus();
    eventBus.testFinished();
    eventBus.testSuiteFinished();
    eventBus.dropAllListeners();
    eventBus.clear();
  }

  @ParameterizedTest(name = "practitioner name.text = \"{0}\"")
  @ValueSource(
      strings = {
        "Wärkelsheim, Marcel",
        "Marcel Wärkelsheim",
        "Dr. Marcel Wärkelsheim",
        "Wärkelsheim, Dr. Marcel",
        "Marcel WärkelsheimTEST-ONLY"
      })
  @SuppressWarnings("java:S2699") // assertions are performed inside the glue method under test
  void findUserWithSearchParamTest(String practitionerNameText) throws IOException {
    // arrange
    theActorCalled("Alice").whoCan(CallAnApi.at(url)).can(useDevice(1L));
    Actor bob = theActorCalled("Bob").whoCan(CallAnApi.at(url)).can(useDevice(2L));
    bob.remember(DISPLAY_NAME, BOB_DISPLAY_NAME);
    bob.remember(MX_ID, BOB_MX_ID);
    bob.remember(OWN_ENDPOINT_ID, BOB_ENDPOINT_ID);
    addEndpoint(BOB_DISPLAY_NAME, new FhirEndpointDTO().name(BOB_MX_ID));

    mockResponse(
        """
        {"mxid": "%s", "displayName": "%s"}
        """
            .formatted(BOB_MX_ID, BOB_DISPLAY_NAME));

    mockResponse(
        readResource(FHIR_SEARCH_RESULT)
            .replace(PRACTITIONER_NAME_PLACEHOLDER, practitionerNameText));

    // act
    // assert
    FhirPractitionerSearchGlue.findUserWithSearchParam("Alice", "Bob", 0, 1);
  }
}
