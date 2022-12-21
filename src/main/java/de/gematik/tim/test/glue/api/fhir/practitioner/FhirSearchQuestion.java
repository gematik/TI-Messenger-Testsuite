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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.LAST_RESPONSE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.SEARCH_PRACTITIONER;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.models.FhirPractitionerSearchResultDTO;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import org.apache.commons.lang3.StringUtils;

public class FhirSearchQuestion implements Question<FhirPractitionerSearchResultDTO> {

  String mxid;
  String name;
  String address;
  String telematikId;
  String typeCode;
  String typeDisplay;


  public static FhirSearchQuestion practitionerInFhirDirectory() {
    return new FhirSearchQuestion();
  }

  public FhirSearchQuestion withMxid(String mxid) {
    this.mxid = mxid;
    return this;
  }

  public FhirSearchQuestion withName(String name) {
    this.name = name;
    return this;
  }

  public FhirSearchQuestion withAddress(String address) {
    this.address = address;
    return this;
  }

  public FhirSearchQuestion withTelematikId(String telematikId) {
    this.telematikId = telematikId;
    return this;
  }

  public FhirSearchQuestion withTypeCode(String typeCode) {
    this.typeCode = typeCode;
    return this;
  }

  public FhirSearchQuestion withTypeDisplay(String typeDisplay) {
    this.typeDisplay = typeDisplay;
    return this;
  }

  @Override
  public FhirPractitionerSearchResultDTO answeredBy(Actor actor) {
    actor.attemptsTo(
        SEARCH_PRACTITIONER.request().with(this::prepareQuery));

    RawDataStatistics.search();

    FhirPractitionerSearchResultDTO fhirPractitionerSearchResultDTO = lastResponse().body()
        .as(FhirPractitionerSearchResultDTO.class);
    actor.remember(LAST_RESPONSE, lastResponse());
    return fhirPractitionerSearchResultDTO;
  }

  private RequestSpecification prepareQuery(RequestSpecification request) {
    if (StringUtils.isNotBlank(mxid)) {
      request.queryParam("mxid", mxid);
    }
    if (StringUtils.isNotBlank(name)) {
      request.queryParam("name", name);
    }
    if (StringUtils.isNotBlank(address)) {
      request.queryParam("address", address);
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
    return request;
  }
}
