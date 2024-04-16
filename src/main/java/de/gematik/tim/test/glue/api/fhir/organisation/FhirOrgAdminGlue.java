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

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.GeneralStepsGlue.checkResponseCode;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirSearchOrgQuestion.organizationEndpoints;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.CreateEndpointTask.addHealthcareServiceEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointTask.deleteEndPoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointListQuestion.getEndpointList;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointQuestion.getEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpointFromFile;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.CreateHealthcareServiceTask.createHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.DeleteHealthcareServicesTask.deleteHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceQuestion.getHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirIsHealthcareServiceDeletedQuestion.getDeletedHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UpdateHealthcareServiceTask.updateHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.CreateLocationTask.addHealthcareServiceLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.DeleteLocationTask.deleteLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationListQuestion.getLocationList;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationQuestion.getLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.UpdateLocationTask.updateLocation;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertCorrectEndpointNameAndMxid;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.assertMxIdsInEndpoint;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.createUniqueEndpointName;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getResourcesFromSearchResult;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.mxidToUri;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.addEndpoint;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getEndpointFromInternalName;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.removeInternalEndpointWithName;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationListQuestion;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirLocationDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.List;
import net.serenitybdd.screenplay.Actor;

public class FhirOrgAdminGlue {

  public static final String INVALID_HS_NAME = "INVALID";

  @Given("{string} finds {string} in the healthcare service {string}")
  @Angenommen("{string} findet {string} im Healthcare-Service {string}")
  public static void findsAddressInHealthcareService(
      String actorName, String userName, String hsName) {
    Actor actor = theActorCalled(actorName);
    String mxId = theActorCalled(userName).recall(MX_ID);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints().withHsName(hsName).havingMxIdAsUriInEndpoint(mxidToUri(mxId)));
    List<FhirEndpointDTO> endpoints =
        getResourcesFromSearchResult(result, ENDPOINT, FhirEndpointDTO.class);
    assertCorrectEndpointNameAndMxid(endpoints, theActorCalled(userName));
  }

  @And("{string} creates a healthcare service {string}")
  @Und("{string} erstellt einen Healthcare-Service {string}")
  public void createHealthcareServiceWithName(String orgAdmin, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(createHealthcareService(hsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @When("{string} creates an endpoint for {string} in healthcare service {string}")
  @Wenn("{string} erstellt Endpunkt für {string} auf Healthcare-Service {string}")
  public void createEndpointForHealthcareService(String orgAdmin, String client, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    Actor endpointActor = theActorCalled(client);
    String endpointMxId = endpointActor.recall(MX_ID);
    admin.attemptsTo(
        addHealthcareServiceEndpoint(endpointActor.recall(DISPLAY_NAME))
            .withMxIdAsUri(mxidToUri(endpointMxId))
            .forHealthcareService(hsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @Given("{string} creates a healthcare service {string} with endpoint {string}")
  @Angenommen(
      "{string} erstellt einen Healthcare-Service {string} und setzen einen Endpunkt auf {string}")
  public void createsHealthcareServiceWithNameAndCreatEndpointWithMxIdOfActor(
      String orgAdmin, String hsName, String client) {
    createHealthcareServiceWithName(orgAdmin, hsName);
    createEndpointForHealthcareService(orgAdmin, client, hsName);
  }

  @When("{string} tries to create a healthcare service with an invalid JSON {string}")
  @Wenn(
      "{string} versucht einen Healthcare-Service mit dem nicht validen JSON {string} zu erstellen")
  public void createHealthcareServiceWithInvalidJson(String orgAdmin, String file) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(createHealthcareService(INVALID_HS_NAME).withStringFromFile(file));
    checkResponseCode(orgAdmin, BAD_REQUEST.value());
  }

  @When("{string} adds a location {string} to the healthcare service {string} with JSON {string}")
  @Wenn(
      "{string} fügt eine Location {string} zum Healthcare-Service {string} hinzu mit JSON {string}")
  public void addLocationToHealthcareServiceWithJson(
      String orgAdmin, String locationName, String hsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(
        addHealthcareServiceLocation(locationName).fromFile(fileName).forHealthcareService(hsName));
    checkResponseCode(orgAdmin, CREATED.value());
  }

  @Then(
      "{string} changes the location {string} for the healthcare service {string} with JSON {string}")
  @Dann(
      "{string} ändert die Location {string} für den Healthcare-Service {string} mit JSON {string}")
  public void changeLocationForHealthcareServiceWithJson(
      String orgAdmin, String locationName, String hsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    FhirLocationDTO location = admin.asksFor(getLocation().withName(locationName));
    admin.attemptsTo(updateLocation(location).withFile(fileName));
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} deletes the location {string} for the healthcare service {string}")
  @Und("{string} löscht die Location {string} für den Healthcare-Service {string}")
  public void deleteLocationForHealthcareService(
      String orgAdmin, String locationName, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(deleteLocation().withName(locationName).forHealthcareService(hsName));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
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
    assertMxIdsInEndpoint(results, actorMxids);
  }

  @When("{string} changes endpoint of healthcare service {string} to {string}")
  @Wenn("{string} ändert den Endpunkt des Healthcare-Services {string} auf {string}")
  public void changeHealthcareServiceEndpointToOtherMxId(
      String orgAdmin, String hsName, String userName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    FhirSearchResultDTO result = admin.asksFor(organizationEndpoints().withHsName(hsName));
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
  public void changeEndpointNameInHs(String orgAdmin, String client, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
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
      String orgAdmin, String client, String hsName, String fileName) {
    String endpointName = theActorCalled(client).recall(DISPLAY_NAME);
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(endpointName);
    admin.attemptsTo(updateEndpointFromFile(fileName));
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} deletes the endpoint of {string} of the healthcare service {string}")
  @Und("{string} löscht den Endpoint von {string} für den Healthcare-Service {string}")
  public void deleteEndpointOfHealthcareService(String orgAdmin, String userName, String hsName) {
    String endpointName = theActorCalled(userName).recall(DISPLAY_NAME);
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(deleteEndPoint().withName(endpointName).forHealthcareService(hsName));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
  }

  @When("{string} changes the from {string} created healthcare service {string} with JSON {string}")
  @Wenn(
      "{string} ändert die Daten des von {string} angelegten Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(
      String orgAdmin, String hsCreator, String hsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);
    UseHealthcareServiceAbility ability =
        theActorCalled(hsCreator).abilityTo(UseHealthcareServiceAbility.class);
    admin.can(ability);
    changeDataFromHealthcareServiceWithJson(orgAdmin, hsName, fileName);
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} changes the data of the healthcare service {string} with the JSON {string}")
  @Und("{string} ändert die Daten des Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(
      String orgAdmin, String hsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);

    admin.attemptsTo(
        updateHealthcareService()
            .withName(hsName)
            .withFile(fileName)
            .withOrganizationOfCurrentDevice());
    checkResponseCode(orgAdmin, OK.value());
  }

  @And("{string} deletes the healthcare service {string}")
  @Und("{string} löscht den Healthcare-Service {string}")
  public void actorDeletesHealthcareService(String orgAdmin, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(deleteHealthcareService().withName(hsName));
    checkResponseCode(orgAdmin, NO_CONTENT.value());
  }

  @Then("the location {string} to the healthcare service {string} complies with the JSON {string}")
  @Dann("entspricht die Location {string} zum Healthcare-Service {string} dem JSON {string}")
  public void checkHealthcareServiceLocationMatchesJson(
      String locationName, String hsName, String jsonFile) {
    FhirLocationDTO location =
        theActorInTheSpotlight()
            .asksFor(getLocation().withName(locationName).forHealthcareService(hsName));

    FhirLocationDTO expectedLocation = readJsonFile(jsonFile, FhirLocationDTO.class);

    assertThat(location)
        .usingRecursiveComparison()
        .withEqualsForType((a, b) -> a.subtract(b).abs().doubleValue() < 0.00001, BigDecimal.class)
        .ignoringFields("locationId")
        .isEqualTo(expectedLocation);
  }

  @Then("no location {string} for the healthcare service {string} exists")
  @Dann("existiert keine Location {string} für den Healthcare-Service {string}")
  public void noHealthcareServiceLocationWithName(String locationName, String hsName) {
    FhirGetLocationListQuestion locationList =
        getLocationList().filterForName(locationName).forHealthcareService(hsName);
    theActorInTheSpotlight().should(seeThat(locationList, is(empty())));
  }

  @Then("no endpoint exists for {string} in the health-care-service {string}")
  @Dann("existiert kein Endpoint von {string} für den Healthcare-Service {string}")
  public void noHealthcareServiceEndpointWithName(String actorName, String hsName) {
    noHealthcareServiceEndpointWithNameTiming(actorName, hsName, null, null);
  }

  @Then(
      "no endpoint exists for {string} in the health-care-service {string} [Retry {long} - {long}]")
  @Dann(
      "existiert kein Endpoint von {string} für den Healthcare-Service {string} [Retry {long} - {long}]")
  public void noHealthcareServiceEndpointWithNameTiming(
      String actorName, String hsName, Long timeout, Long pollInterval) {
    Actor actor = theActorCalled(actorName);
    FhirSearchResultDTO result =
        actor.asksFor(
            organizationEndpoints()
                .withHsName(hsName)
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

  @Then("the last deleted healthcare service does not exist anymore")
  @Dann("existiert der zuletzt gelöschte Healthcare-Service nicht mehr")
  public void lastDeletedHsDoesNotExistAnymore() {
    lastDeletedHsDoesNotExistAnymoreTiming(null, null);
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
  public void compareHealthcareServiceWithJson(String orgAdmin, String hsName, String fileName) {
    Actor actor = theActorCalled(orgAdmin);
    FhirHealthcareServiceDTO hs = actor.asksFor(getHealthcareService().withName(hsName));
    FhirHealthcareServiceDTO jsonHs = readJsonFile(fileName, FhirHealthcareServiceDTO.class);

    jsonHs.setEndpoint(jsonHs.getEndpoint() == null ? List.of() : jsonHs.getEndpoint());
    jsonHs.setLocation(jsonHs.getLocation() == null ? List.of() : jsonHs.getLocation());
    hs.setEndpoint(hs.getEndpoint() == null ? List.of() : hs.getEndpoint());
    hs.setLocation(hs.getLocation() == null ? List.of() : hs.getLocation());

    assertThat(hs)
        .usingRecursiveComparison()
        .ignoringFields("providedBy", "identifier", "meta", "resourceType", "text", "name")
        .ignoringFieldsMatchingRegexes(".*id")
        .isEqualTo(jsonHs);
  }

  @And("the endpoint of {string} in the healthcare service {string} is the JSON {string}")
  @Und("entspricht der Endpunkt von {string} im Healthcare-Service {string} dem JSON {string}")
  public void compareEndpointOfHealthcareServiceWithJson(
      String user, String hsName, String fileName) {
    Actor admin = theActorInTheSpotlight();
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(theActorCalled(user).recall(DISPLAY_NAME));
    FhirEndpointDTO endpoint = admin.asksFor(getEndpoint());
    FhirEndpointDTO jsonEndpoint = readJsonFile(fileName, FhirEndpointDTO.class);
    assertThat(endpoint)
        .usingRecursiveComparison()
        .ignoringFields("identifier", "meta", "resourceType", "text")
        .ignoringFieldsMatchingRegexes(".*id")
        .isEqualTo(jsonEndpoint);
  }
}
