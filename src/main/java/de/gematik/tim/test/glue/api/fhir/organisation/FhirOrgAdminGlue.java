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

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirSearchOrgQuestion.organizationEndpoints;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.CreateEndpointTask.addHealthcareServiceEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointTask.deleteEndPoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointVisibilityOfHealthcareServiceTask.deleteEndpointVisibility;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointListQuestion.getEndpointList;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointQuestion.getEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.NoFhirVisibilityOfHealthcareServiceQuestion.endpointVisibleInFhirDirectory;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.SetEndpointVisibilityOfHealthcareServiceTask.setEndpointVisibility;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpointFromFile;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.CreateHealthcareServiceTask.createHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.DeleteHealthcareServicesTask.deleteHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceQuestion.getHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirIsHealthcareServiceDeletedQuestion.getDeletedHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UpdateHealthcareServiceTask.updateHealthcareService;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertCorrectEndpointNameAndMxid;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertMxIdsInEndpoint;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueEndpointName;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getHomeServerWithoutHttpAndPort;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.mxidToUri;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getHealthcareServiceFromInternalName;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.removeInternalEndpointWithName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.HEALTHCARE_SERVICE;
import static java.lang.String.format;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareServiceInfo;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.models.FhirCodingDTO;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirExtensionDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import net.serenitybdd.screenplay.Actor;

public class FhirOrgAdminGlue {

  @Given("{string} finds {string} in the healthcare service {string}")
  @Angenommen("{string} findet {string} im Healthcare-Service {string}")
  public static void findsAddressInHealthcareService(
      String actorName, String userName, String hcsName) {
    Actor actor = theActorCalled(actorName);
    String mxId = theActorCalled(userName).recall(MX_ID);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints()
                .withHealthcareServiceName(hcsName)
                .havingMxIdAsUriInEndpoint(mxidToUri(mxId)));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertCorrectEndpointNameAndMxid(endpoints, theActorCalled(userName));
  }

  @And(
      "{string} can find healthcare service {string} by searching by name with cut {int}-{int} \\(amount front-back) char\\(s)")
  @Und(
      "{string} findet Healthcare-Service {string} bei Suche nach Namen minus {int}-{int} \\(Anzahl vorne-hinten) Char\\(s) abgeschnitten")
  public static void findHealthcareServiceWithSearchParam(
      String actorName, String healthcareServiceName, int begin, int end) {
    Actor actor = theActorCalled(actorName);

    HealthcareServiceInfo healthcareServiceInfo =
        getHealthcareServiceFromInternalName(healthcareServiceName);
    String uniqueHealthcareServiceName = healthcareServiceInfo.name();
    assertThat(begin + end)
        .as("You cut to much of the healthcare service name: " + uniqueHealthcareServiceName)
        .isLessThan(uniqueHealthcareServiceName.length());
    String cutName =
        uniqueHealthcareServiceName.substring(begin, uniqueHealthcareServiceName.length() - end);

    FhirSearchResultDTO searchResult =
        actor.asksFor(organizationEndpoints().withUniqueHsName(cutName));
    checkResponseCode(actor.getName(), OK.value());
    List<FhirHealthcareServiceDTO> healthcareServices =
        getResourcesFromSearchResult(
            searchResult, HEALTHCARE_SERVICE, FhirHealthcareServiceDTO.class);
    List<FhirHealthcareServiceDTO> healthcareServicesWithMatchingName =
        healthcareServices.stream()
            .filter(
                healthcareService ->
                    healthcareService.getName().equals(uniqueHealthcareServiceName))
            .toList();
    assertThat(healthcareServicesWithMatchingName.size())
        .as(
            "Found none or more than one healthcare service with name "
                + healthcareServicesWithMatchingName)
        .isEqualTo(1);
  }

  @And("{string} creates a healthcare service {string}")
  @Und("{string} erstellt einen Healthcare-Service {string}")
  public void createHealthcareServiceWithName(String orgAdmin, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(createHealthcareService(hcsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @When("{string} creates an endpoint for {string} in healthcare service {string}")
  @Wenn("{string} erstellt Endpunkt für {string} auf Healthcare-Service {string}")
  public void createEndpointForHealthcareService(String orgAdmin, String client, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    Actor endpointActor = theActorCalled(client);
    String endpointMxId = endpointActor.recall(MX_ID);
    admin.attemptsTo(
        addHealthcareServiceEndpoint(endpointActor.recall(DISPLAY_NAME))
            .withMxIdAsUri(mxidToUri(endpointMxId))
            .forHealthcareService(hcsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @Given("{string} creates a healthcare service {string} with endpoint {string}")
  @Angenommen(
      "{string} erstellt einen Healthcare-Service {string} und setzen einen Endpunkt auf {string}")
  public void createsHealthcareServiceWithNameAndCreatEndpointWithMxIdOfActor(
      String orgAdmin, String hcsName, String client) {
    createHealthcareServiceWithName(orgAdmin, hcsName);
    createEndpointForHealthcareService(orgAdmin, client, hcsName);
  }

  @When(
      "{string} can see, that {listOfStrings} have exactly one endpoint in the healthcare service {string}")
  @Wenn(
      "{string} sieht, dass {listOfStrings} genau einen Endpunkt im Healthcare-Service {string} hat")
  @Wenn(
      "{string} sieht, dass {listOfStrings} je einen Endpunkt im Healthcare-Service {string} haben")
  public void healthCareServiceEndpointSearch(
      String adminName, List<String> actorNames, String hcsName) {
    List<String> actorMxids =
        actorNames.stream().map(name -> (String) theActorCalled(name).recall(MX_ID)).toList();
    Actor admin = theActorCalled(adminName);
    String hsId = admin.abilityTo(UseHealthcareServiceAbility.class).getTarget(hcsName).id();
    List<FhirEndpointDTO> results = getEndpointList().withHsId(hsId).answeredBy(admin);
    if (results.isEmpty()) {
      throw new TestRunException("No matching endpoints found for healthcare service " + hcsName);
    }
    assertMxIdsInEndpoint(results, actorMxids);
  }

  @When("{string} changes endpoint of healthcare service {string} to {string}")
  @Wenn("{string} ändert den Endpunkt des Healthcare-Services {string} auf {string}")
  public void changeHealthcareServiceEndpointToOtherMxId(
      String orgAdmin, String hcsName, String userName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    FhirSearchResultDTO result =
        admin.asksFor(organizationEndpoints().withHealthcareServiceName(hcsName));
    FhirEndpointDTO endpoint =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class).stream()
            .findFirst()
            .orElseThrow();
    removeInternalEndpointWithName(endpoint.getName());
    Actor endpointActor = theActorCalled(userName);
    endpoint.setAddress(endpointActor.recall(mxidToUri(MX_ID)));
    admin.attemptsTo(updateEndpoint(endpoint).withAddress(mxidToUri(endpointActor.recall(MX_ID))));
    addEndpoint(endpointActor.recall(DISPLAY_NAME), endpoint);
    checkResponseCode(orgAdmin, OK.value());
  }

  @When("{string} changes the endpoint name of {string} of the healthcare service {string}")
  @Wenn("{string} ändert den Endpunktname von {string} des Healthcare-Services {string}")
  public void changeEndpointNameInHs(String orgAdmin, String client, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    FhirEndpointDTO endpoint =
        getEndpointFromInternalName(theActorCalled(client).recall(DISPLAY_NAME));
    endpoint.setName(createUniqueEndpointName());
    addEndpoint(client, endpoint);
    admin.attemptsTo(updateEndpoint(endpoint));
    FhirEndpointDTO updatedEndpoint = admin.asksFor(getEndpoint());
    assertThat(updatedEndpoint)
        .usingRecursiveComparison()
        .ignoringFields("meta")
        .isEqualTo(endpoint);
  }

  @And(
      "{string} changes the endpoint of {string} of the healthcare service {string} with the JSON {string}")
  @Und(
      "{string} ändert den Endpunkt von {string} im Healthcare-Service {string} mit dem JSON {string}")
  public void changeEndpointForHealthcareServiceWithJson(
      String orgAdmin, String client, String hcsName, String fileName) {
    String endpointName = theActorCalled(client).recall(DISPLAY_NAME);
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(endpointName);
    admin.attemptsTo(
        updateEndpointFromFile(fileName).withHomeserver(getHomeServerWithoutHttpAndPort(admin)));
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} deletes the endpoint of {string} of the healthcare service {string}")
  @Und("{string} löscht den Endpoint von {string} für den Healthcare-Service {string}")
  public void deleteEndpointOfHealthcareService(String orgAdmin, String userName, String hcsName) {
    String endpointName = theActorCalled(userName).recall(DISPLAY_NAME);
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(deleteEndPoint().withName(endpointName).forHealthcareService(hcsName));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
  }

  @When("{string} changes the from {string} created healthcare service {string} with JSON {string}")
  @Wenn(
      "{string} ändert die Daten des von {string} angelegten Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(
      String orgAdmin, String hsCreator, String hcsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);
    UseHealthcareServiceAbility ability =
        theActorCalled(hsCreator).abilityTo(UseHealthcareServiceAbility.class);
    admin.can(ability);
    changeDataFromHealthcareServiceWithJson(orgAdmin, hcsName, fileName);
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} changes the data of the healthcare service {string} with the JSON {string}")
  @Und("{string} ändert die Daten des Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(
      String orgAdmin, String hcsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);

    admin.attemptsTo(
        updateHealthcareService()
            .withName(hcsName)
            .withFile(fileName)
            .withOrganizationOfCurrentDevice());
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} deletes the healthcare service {string}")
  @Und("{string} löscht den Healthcare-Service {string}")
  public void actorDeletesHealthcareService(String orgAdmin, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(deleteHealthcareService().withName(hcsName));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
  }

  @Then(
      "no endpoint exists for {string} in the health-care-service {string} [Retry {long} - {long}]")
  @Dann(
      "existiert kein Endpoint von {string} für den Healthcare-Service {string} [Retry {long} - {long}]")
  public static void noHealthcareServiceEndpointWithNameTiming(
      String actorName, String hcsName, Long timeout, Long pollInterval) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints()
                .withHealthcareServiceName(hcsName)
                .havingAtLeastXEndpoints(0)
                .withCustomInterval(timeout, pollInterval));

    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    List<FhirEndpointDTO> filtered =
        endpoints.stream()
            .filter(endpoint -> endpoint.getAddress().equals(actor.recall(MX_ID)))
            .toList();
    assertThat(filtered).isEmpty();
  }

  @Then("the last deleted healthcare service does not exist anymore [Retry {long} - {long}]")
  @Dann("existiert der zuletzt gelöschte Healthcare-Service nicht mehr [Retry {long} - {long}]")
  public void lastDeletedHsDoesNotExistAnymoreTiming(Long timeout, Long pollInterval) {
    assertTrue(
        theActorInTheSpotlight()
            .asksFor(getDeletedHealthcareService().withCustomInterval(timeout, pollInterval)));
  }

  @Then("{string} compares the healthcare service {string} with the JSON {string}")
  @Dann("vergleicht {string} den Healthcare-Service {string} mit dem JSON {string}")
  public void compareHealthcareServiceWithJson(String orgAdmin, String hcsName, String fileName) {
    Actor actor = theActorCalled(orgAdmin);
    FhirHealthcareServiceDTO actualHealthcareService =
        actor.asksFor(getHealthcareService().withName(hcsName));
    FhirHealthcareServiceDTO expectedHealthcareService =
        readJsonFile(fileName, FhirHealthcareServiceDTO.class);

    expectedHealthcareService.setEndpoint(
        expectedHealthcareService.getEndpoint() == null
            ? List.of()
            : expectedHealthcareService.getEndpoint());
    actualHealthcareService.setEndpoint(
        actualHealthcareService.getEndpoint() == null
            ? List.of()
            : actualHealthcareService.getEndpoint());

    assertThat(actualHealthcareService)
        .usingRecursiveComparison()
        .ignoringFields(
            "providedBy", "identifier", "meta", "resourceType", "text", "name", "location")
        .ignoringFieldsMatchingRegexes(".*id")
        .isEqualTo(expectedHealthcareService);
  }

  @And("the endpoint of {string} in the healthcare service {string} is the JSON {string}")
  @Und("entspricht der Endpunkt von {string} im Healthcare-Service {string} dem JSON {string}")
  public void compareEndpointOfHealthcareServiceWithJson(
      String user, String hcsName, String fileName) {
    Actor admin = theActorInTheSpotlight();
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(theActorCalled(user).recall(DISPLAY_NAME));
    FhirEndpointDTO actualEndpoint = admin.asksFor(getEndpoint());
    FhirEndpointDTO expectedEndpoint = readJsonFile(fileName, FhirEndpointDTO.class);
    String actualAddress = expectedEndpoint.getAddress() + getHomeServerWithoutHttpAndPort(admin);
    expectedEndpoint.setAddress(actualAddress);

    expectedEndpoint.setExtension(
        expectedEndpoint.getExtension() == null ? List.of() : expectedEndpoint.getExtension());
    actualEndpoint.setExtension(
        actualEndpoint.getExtension() == null ? List.of() : actualEndpoint.getExtension());
    assertThat(actualEndpoint)
        .usingRecursiveComparison()
        .ignoringFields("identifier", "meta", "resourceType", "text", "managingOrganization")
        .ignoringFieldsMatchingRegexes(".*id")
        .isEqualTo(expectedEndpoint);
  }

  @When(
      "{string} sets the endpointVisibility of the endpoint extension of {string} in the healthcare service {string} to hide-versicherte")
  @Wenn(
      "{string} setzt die endpointVisibility für die Endpunkt Extension von {string} im Healthcare-Service {string} auf hide-versicherte")
  public void setEndpointVisibilityForHealthcareService(
      String orgAdmin, String client, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    admin
        .abilityTo(UseEndpointAbility.class)
        .setActive(theActorCalled(client).recall(DISPLAY_NAME));
    admin.attemptsTo(setEndpointVisibility());
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @When(
      "{string} deletes the endpointVisibility hide-versicherte for the endpoint extension of {string} in the healthcare service {string}")
  @Wenn(
      "{string} löscht die endpointVisibility hide-versicherte für die Endpunkt Extension von {string} im Healthcare-Service {string}")
  public void deleteEndpointVisibilityForHealthcareService(
      String orgAdmin, String client, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    FhirEndpointDTO endpoint =
        getEndpointFromInternalName(theActorCalled(client).recall(DISPLAY_NAME));
    admin.attemptsTo(deleteEndpointVisibility(endpoint));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
  }

  @And(
      "{string} sees the endpointVisibility for the endpoint extension of {string} in the healthcare service {string} as hide-versicherte")
  @Und(
      "{string} sieht die endpointVisibility für die Endpunkt Extension von {string} im Healthcare-Service {string} auf hide-versicherte")
  public void getEndpointVisibilityForHealthcareService(
      String orgAdmin, String userName, String hcsName) {
    Actor admin = theActorCalled(orgAdmin);
    Actor user = theActorCalled(userName);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(user.recall(DISPLAY_NAME));
    FhirEndpointDTO endpoint = admin.asksFor(getEndpoint());
    assertCorrectEndpointNameAndNotVisible(endpoint, user);
  }

  @And(
      "{string} sees the endpointVisibility for the endpoint extension of {string} in the healthcare service {string} NO longer as hide-versicherte [Retry {long} - {long}]")
  @Und(
      "{string} sieht die endpointVisibility für die Endpunkt Extension von {string} im Healthcare-Service {string} NICHT mehr auf dem Wert hide-versicherte [Retry {long} - {long}]")
  public void doNotGetEndpointVisibilityForHealthcareService(
      String orgAdmin,
      String userName,
      String hcsName,
      Long customTimeout,
      Long customPollInterval) {
    Actor admin = theActorCalled(orgAdmin);
    Actor user = theActorCalled(userName);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hcsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(user.recall(DISPLAY_NAME));
    admin.asksFor(
        endpointVisibleInFhirDirectory().withCustomInterval(customTimeout, customPollInterval));
  }

  private void assertCorrectEndpointNameAndNotVisible(
      FhirEndpointDTO endpoint, Actor searchedActor) {
    String endpointName = getEndpointFromInternalName(searchedActor.recall(DISPLAY_NAME)).getName();
    assertThat(endpoint.getExtension())
        .as(format("Could not find any extension for endpoint %s", endpointName))
        .isNotEmpty();

    List<FhirCodingDTO> valueCodes =
        endpoint.getExtension().stream().map(FhirExtensionDTO::getValueCoding).toList();
    assertThat(valueCodes)
        .as(format("Could not find any valueCodes for endpoint %s", endpointName))
        .isNotEmpty();

    List<String> codes = valueCodes.stream().map(FhirCodingDTO::getCode).toList();
    assertThat(codes)
        .as("No allowed code was found for endpoint %s", endpointName)
        .contains("hide-versicherte");
  }

  @Then(
      "{string} creates a healthcare service {string} and sets a functional account endpoint to {string}")
  @Dann(
      "{string} erstellt einen Healthcare-Service {string} und setzen einen Funktionsaccount-Endpunkt auf {string}")
  public void createHealthcareServiceWithFunctionAccount(
      String orgAdmin, String hcsName, String userName) {
    createHealthcareServiceWithName(orgAdmin, hcsName);
    Actor admin = theActorCalled(orgAdmin);
    Actor endpointActor = theActorCalled(userName);
    String endpointMxId = endpointActor.recall(MX_ID);
    admin.attemptsTo(
        addHealthcareServiceEndpoint(endpointActor.recall(DISPLAY_NAME))
            .withMxIdAsUri(mxidToUri(endpointMxId))
            .withConnectionTypeCode("tim-fa")
            .forHealthcareService(hcsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @Then(
      "{string} finds {string} in the healthcare service {string} with the ConnectionType {string}")
  @Dann("{string} findet {string} im Healthcare-Service {string} mit dem ConnectionType {string}")
  public void findHealthcareServiceWithFunctionAccount(
      String actorName, String userName, String hcsName, String connectionType) {
    Actor actor = theActorCalled(actorName);
    String mxId = theActorCalled(userName).recall(MX_ID);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints()
                .withHealthcareServiceName(hcsName)
                .havingMxIdAsUriInEndpoint(mxidToUri(mxId)));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    String endpointName =
        getEndpointFromInternalName(theActorCalled(userName).recall(DISPLAY_NAME)).getName();
    List<FhirEndpointDTO> namedEndpoints = getEndpointsByName(endpoints, endpointName);
    List<FhirEndpointDTO> endpointsWithAddress =
        getEndpointsByAddress(namedEndpoints, theActorCalled(userName).recall(MX_ID));
    List<String> connectionTypeCodes =
        endpointsWithAddress.stream()
            .map(
                endpoint -> {
                  assert endpoint.getConnectionType() != null;
                  return endpoint.getConnectionType().getCode();
                })
            .toList();
    assertThat(connectionTypeCodes).contains(connectionType);
  }

  private List<FhirEndpointDTO> getEndpointsByName(
      List<FhirEndpointDTO> endpoints, String endpointName) {
    List<FhirEndpointDTO> namedEndpoints =
        endpoints.stream().filter(endpoint -> endpoint.getName().equals(endpointName)).toList();
    assertThat(namedEndpoints)
        .as(format("Could not find endpoint with name %s", endpointName))
        .isNotEmpty();
    return namedEndpoints;
  }

  private List<FhirEndpointDTO> getEndpointsByAddress(
      List<FhirEndpointDTO> endpoints, String mxId) {
    List<FhirEndpointDTO> endpointsWithAddress =
        endpoints.stream()
            .filter(endpoint -> endpoint.getAddress().equals(mxidToUri(mxId)))
            .toList();
    assertThat(endpointsWithAddress)
        .as(format("Could not find endpoint with mxId %s", mxidToUri(mxId)))
        .isNotEmpty();
    return endpointsWithAddress;
  }
}
