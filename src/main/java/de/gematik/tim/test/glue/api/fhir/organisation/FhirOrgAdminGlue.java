/*
 * Copyright (c) 2023 gematik GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.gematik.tim.test.glue.api.fhir.organisation;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.CLAIMER_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.fhir.organisation.FhirSearchOrgQuestion.organizationEndpoints;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.CreateEndpointTask.addHealthcareServiceEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.DeleteEndpointTask.deleteEndPoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointListQuestion.getEndpointList;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointQuestion.getEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpoint;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UpdateEndpointTask.updateEndpointFromFile;
import static de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility.addEndpointToActor;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.CreateHealthcareServiceTask.createHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.DeleteHealthcareServicesTask.deleteHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceListQuestion.getHealthcareServiceList;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceQuestion.getHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UpdateHealthcareServiceTask.updateHealthcareService;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.CreateLocationTask.addHealthcareServiceLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.DeleteLocationTask.deleteLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationListQuestion.getLocationList;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationQuestion.getLocation;
import static de.gematik.tim.test.glue.api.fhir.organisation.location.UpdateLocationTask.updateLocation;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.readJsonFile;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.stage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.FhirGetEndpointListQuestion;
import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.FhirGetHealthcareServiceListQuestion;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.location.FhirGetLocationListQuestion;
import de.gematik.tim.test.models.FhirEndpointDTO;
import de.gematik.tim.test.models.FhirHealthcareServiceDTO;
import de.gematik.tim.test.models.FhirLocationDTO;
import de.gematik.tim.test.models.FhirOrganizationSearchResultListDTO;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.de.Angenommen;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Und;
import io.cucumber.java.de.Wenn;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;

public class FhirOrgAdminGlue {

  public static final String INVALID_HS_NAME = "INVALID";

  @Before
  public void setup() {
    setTheStage(Cast.ofStandardActors());
  }

  @After
  public void teardown() {
    stage().drawTheCurtain();
  }

  //<editor-fold desc="Create & Add">
  @Und("{string} erstellt einen Healthcare-Service {string}")
  public void createHealthcareServiceWithName(String orgAdmin, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.attemptsTo(createHealthcareService(hsName));
  }

  @Wenn("{string} erstellt Endpunkt für {string} auf Healthcare-Service {string}")
  public void createEndpointForHealthcareService(String orgAdmin, String client, String hsName) {
    Actor admin = theActorCalled(orgAdmin);
    Actor endpointActor = theActorCalled(client);
    String endpointMxId = endpointActor.recall(MX_ID);
    admin.attemptsTo(addHealthcareServiceEndpoint(endpointActor.recall(CLAIMER_NAME))
        .withMxId(endpointMxId)
        .forHealthcareService(hsName));
  }

  @Given("{string} creates a healthcare service {string} for {string}")
  @Angenommen("{string} erstellt einen Healthcare-Service {string} und setzen einen Endpunkt auf {string}")
  public void createsHealthcareServiceWithNameAndCreatEndpointWithMxIdOfActor(String orgAdmin,
      String hsName, String client) {
    createHealthcareServiceWithName(orgAdmin, hsName);
    createEndpointForHealthcareService(orgAdmin, client, hsName);
  }

  @Wenn("{string} versucht einen Healthcare-Service mit dem nicht validen JSON {string} zu erstellen")
  public void createHealthcareServiceWithInvalidJson(String adminName, String file) {
    Actor admin = theActorCalled(adminName);
    admin.attemptsTo(createHealthcareService(INVALID_HS_NAME).withStringFromFile(file));
  }

  @Wenn("{string} fügt eine Location {string} zum Healthcare-Service {string} hinzu mit JSON {string}")
  public void addLocationToHealthcareServiceWithJson(String adminName, String locationName,
      String hsName, String fileName) {
    Actor admin = theActorCalled(adminName);
    admin.attemptsTo(addHealthcareServiceLocation(locationName).fromFile(fileName)
        .forHealthcareService(hsName));
  }
  //</editor-fold>

  //<editor-fold desc="Search">
  @Given("{string} finds {string} in the healthcare service {string}")
  @Angenommen("{string} findet {string} im Healthcare-Service {string}")
  public static void findsAddressInHealthcareService(String actorName, String userName,
      String hsName) {
    Actor actor = theActorCalled(actorName);
    String mxId = theActorCalled(userName).recall(MX_ID);
    FhirOrganizationSearchResultListDTO resultList = actor.asksFor(
        organizationEndpoints().withHsName(hsName).havingMxidInEndpoint(mxId));
    List<String> mxIds = requireNonNull(resultList.getSearchResults()).stream()
        .map(res -> requireNonNull(res.getEndpoint()).getAddress())
        .filter(Objects::nonNull)
        .toList();
    assertThat(mxIds).contains(mxId);
  }
  //</editor-fold>

  //<editor-fold desc="Change">
  @When("{string} changes endpoint of healthcare service {string} to {string}")
  @Wenn("{string} ändert den Endpunkt des Healthcare-Services {string} auf {string}")
  public void changeHealthcareServiceEndpointToOtherMxid(String adminName, String hsName,
      String userName) {
    Actor admin = theActorCalled(adminName);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    FhirEndpointDTO endpoint = requireNonNull(admin.asksFor(
        organizationEndpoints().withHsName(hsName)).getSearchResults()).get(0).getEndpoint();
    Actor endpointActor = theActorCalled(userName);
    endpoint.setAddress(endpointActor.recall(MX_ID));
    addEndpointToActor(userName, endpoint.getEndpointId(), admin);
    admin.attemptsTo(updateEndpoint(endpoint).withAddress(endpointActor.recall(MX_ID)));
  }


  @Wenn("{string} ändert die Daten des von {string} angelegten Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(String adminName,
      String hsCreator,
      String hsName, String fileName) {
    Actor admin = theActorCalled(adminName);
    UseHealthcareServiceAbility ability = theActorCalled(hsCreator).abilityTo(
        UseHealthcareServiceAbility.class);
    admin.can(ability);
    changeDataFromHealthcareServiceWithJson(adminName, hsName, fileName);
  }

  @Und("{string} ändert die Daten des Healthcare-Service {string} mit JSON {string}")
  public void changeDataFromHealthcareServiceWithJson(String adminName, String hsName,
      String fileName) {
    Actor admin = theActorCalled(adminName);

    admin.attemptsTo(updateHealthcareService().withName(hsName).withFile(fileName));
  }

  @Dann("{string} ändert die Location {string} für den Healthcare-Service {string} mit JSON {string}")
  public void changeLocationForHealthcareServiceWithJson(String adminName, String locationName,
      String hsName, String fileName) {
    Actor admin = theActorCalled(adminName);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    FhirLocationDTO location = admin.asksFor(getLocation().withName(locationName));
    admin.attemptsTo(updateLocation(location).withFile(fileName));
  }

  @Und("{string} ändert den Endpunkt von {string} im Healthcare-Service {string} mit dem JSON {string}")
  public void changeEndpointForHealthcareServiceWithJson(String orgAdmin, String client,
      String hsName, String fileName) {
    Actor admin = theActorCalled(orgAdmin);
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(client);
    admin.attemptsTo(updateEndpointFromFile(fileName));
  }
  //</editor-fold>

  //<editor-fold desc="Delete">
  @Und("{string} löscht die Location {string} für den Healthcare-Service {string}")
  public void deleteLocationForHealthcareService(String adminName, String locationName,
      String hsName) {
    Actor admin = theActorCalled(adminName);
    admin.attemptsTo(deleteLocation().withName(locationName).forHealthcareService(hsName));
    assertThat(lastResponse().getStatusCode()).isEqualTo(204);
  }

  @Und("{string} löscht den Endpoint von {string} für den Healthcare-Service {string}")
  public void deleteEndpointOfHealthcareService(String adminName, String userName, String hsName) {
    Actor admin = theActorCalled(adminName);
    admin.attemptsTo(deleteEndPoint().withName(userName).forHealthcareService(hsName));
    assertThat(lastResponse().getStatusCode()).isEqualTo(204);
  }

  @Und("{string} löscht den Healthcare-Service {string}")
  public void actorDeletesHealthcareService(String adminName, String hsName) {
    Actor admin = theActorCalled(adminName);
    admin.attemptsTo(deleteHealthcareService().withName(hsName));
    assertThat(lastResponse().getStatusCode()).isEqualTo(204);
  }
  //</editor-fold>

  //<editor-fold desc="Checks">
  @Dann("entspricht die Location {string} zum Healthcare-Service {string} dem JSON {string}")
  public void checkHealthcareServiceLocationMatchesJson(String locationName, String hsName,
      String jsonFile) {
    FhirLocationDTO location = theActorInTheSpotlight().asksFor(
        getLocation().withName(locationName).forHealthcareService(hsName));

    FhirLocationDTO expectedLocation = readJsonFile(jsonFile, FhirLocationDTO.class);

    assertThat(location).usingRecursiveComparison()
        .withEqualsForType((a, b) -> a.subtract(b).abs().doubleValue() < 0.00001, BigDecimal.class)
        .ignoringFields("locationId")
        .isEqualTo(expectedLocation);
  }

  @Dann("existiert keine Location {string} für den Healthcare-Service {string}")
  public void noHealthcareServiceLocationWithName(String locationName, String hsName) {
    FhirGetLocationListQuestion locationList = getLocationList().filterForName(locationName)
        .forHealthcareService(hsName);
    theActorInTheSpotlight().should(seeThat(locationList, is(empty())));
  }

  @Dann("existiert kein Endpoint von {string} für den Healthcare-Service {string}")
  public void noHealthcareServiceEndpointWithName(String endpointName, String hsName) {
    FhirGetEndpointListQuestion endpointList = getEndpointList().filterForName(endpointName)
        .forHealthcareService(hsName);
    theActorInTheSpotlight().should(seeThat(endpointList, is(empty())));
  }

  @Dann("existiert kein Healthcare-Service {string}")
  public void noHealthcareServiceWithName(String hsName) {
    FhirGetHealthcareServiceListQuestion hsList = getHealthcareServiceList().filterForHealthcareService(
        hsName);
    theActorInTheSpotlight().should(seeThat(hsList, is(empty())));
  }

  @Dann("vergleicht {string} den Healthcare-Service {string} mit dem JSON {string}")
  public void compareHealthcareServiceWithJson(String adminName, String hsName, String fileName) {
    Actor actor = theActorCalled(adminName);
    FhirHealthcareServiceDTO healthcareService = actor.asksFor(
        getHealthcareService().withName(hsName));
    FhirHealthcareServiceDTO jsonHs = readJsonFile(fileName, FhirHealthcareServiceDTO.class);

    jsonHs.setEndpoint(jsonHs.getEndpoint() == null ? List.of() : jsonHs.getEndpoint());
    jsonHs.setLocation(jsonHs.getLocation() == null ? List.of() : jsonHs.getLocation());

    assertThat(healthcareService).usingRecursiveComparison()
        .ignoringFields("healthcareServiceId", "providedBy", "telecom", "endpoint.endpointId",
            "location.locationId")
        .isEqualTo(jsonHs);
  }

  @Und("entspricht der Endpunkt von {string} im Healthcare-Service {string} dem JSON {string}")
  public void entsprichtDerEndpunktVonImHealthcareServiceDemJSON(String client, String hsName,
      String fileName) {
    Actor admin = theActorInTheSpotlight();
    admin.abilityTo(UseHealthcareServiceAbility.class).setActive(hsName);
    admin.abilityTo(UseEndpointAbility.class).setActive(client);
    FhirEndpointDTO endpoint = admin.asksFor(getEndpoint());
    FhirEndpointDTO jsonEndpoint = readJsonFile(fileName, FhirEndpointDTO.class);
    assertThat(endpoint)
        .usingRecursiveComparison()
        .ignoringFields("endpointId")
        .isEqualTo(jsonEndpoint);
  }

  //</editor-fold>
}
