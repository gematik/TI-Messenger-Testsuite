/*
 * Copyright (c) 2022 gematik GmbH
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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_ORG;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.models.FhirOrganizationSearchResultListDTO;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.commons.lang3.StringUtils;

public class FhirSearchOrgQuestion implements Question<FhirOrganizationSearchResultListDTO> {

  private String hsName;
  private String orgName;
  private String address;
  private String telematikId;
  private String typeCode;
  private String typeDisplay;
  private String contactMxid;
  private String contactName;
  private String contactPurpose;


  public static FhirSearchOrgQuestion organizationEndpoints() {
    return new FhirSearchOrgQuestion();
  }

  public FhirSearchOrgQuestion withHsName(String hsName) {
    this.hsName = hsName;
    return this;
  }

  public FhirSearchOrgQuestion withOrgName(String orgName) {
    this.orgName = orgName;
    return this;
  }

  public FhirSearchOrgQuestion withAddress(String address) {
    this.address = address;
    return this;
  }

  public FhirSearchOrgQuestion withTelematikId(String telematikId) {
    this.telematikId = telematikId;
    return this;
  }

  public FhirSearchOrgQuestion withTypeCode(String typeCode) {
    this.typeCode = typeCode;
    return this;
  }

  public FhirSearchOrgQuestion withTypeDisplay(String typeDisplay) {
    this.typeDisplay = typeDisplay;
    return this;
  }

  public FhirSearchOrgQuestion withContactMxid(String contactMxid) {
    this.contactMxid = contactMxid;
    return this;
  }

  public FhirSearchOrgQuestion withContactName(String contactName) {
    this.contactName = contactName;
    return this;
  }

  public FhirSearchOrgQuestion withContactPurpose(String contactPurpose) {
    this.contactPurpose = contactPurpose;
    return this;
  }

  @Override
  public FhirOrganizationSearchResultListDTO answeredBy(Actor actor) {
    actor.attemptsTo(
        SEARCH_ORG.request().with(this::prepareQuery));
    FhirOrganizationSearchResultListDTO resp = lastResponse().body()
        .as(FhirOrganizationSearchResultListDTO.class);
    actor.remember(LAST_RESPONSE, lastResponse());
    return resp;
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (StringUtils.isNotBlank(hsName)) {
      request.queryParam("healthcareServiceName", hsName);
    }
    if (StringUtils.isNotBlank(address)) {
      request.queryParam("address", address);
    }
    if (StringUtils.isNotBlank(orgName)) {
      request.queryParam("name", orgName);
    }
    if (StringUtils.isNotBlank(telematikId)) {
      request.queryParam("telematikId", telematikId);
    }
    if (StringUtils.isNotBlank(typeCode)) {
      request.queryParam("typeCode", typeCode);
    }
    if (StringUtils.isNotBlank(typeDisplay)) {
      request.queryParam("typeDisplay", typeDisplay);
    }
    if (StringUtils.isNotBlank(contactMxid)) {
      request.queryParam("contactMxid", contactMxid);
    }
    if (StringUtils.isNotBlank(contactName)) {
      request.queryParam("contactName", contactName);
    }
    if (StringUtils.isNotBlank(contactPurpose)) {
      request.queryParam("contactPurpose", contactPurpose);
    }
    return request;
  }
}
