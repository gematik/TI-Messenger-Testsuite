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

package de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice;

import static de.gematik.tim.test.glue.api.TestdriverApiPath.HEALTHCARE_SERVICE_ID_VARIABLE;

import de.gematik.tim.test.glue.api.TestdriverApiAbility;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LookForDeletedHealthcareServiceAbility implements TestdriverApiAbility {

  private final String hsId;

  public static LookForDeletedHealthcareServiceAbility lookUpDeleted(String hsId) {
    return new LookForDeletedHealthcareServiceAbility(hsId);
  }

  @Override
  public RequestSpecification apply(RequestSpecification requestSpecification) {
    return requestSpecification.pathParam(HEALTHCARE_SERVICE_ID_VARIABLE, hsId);
  }
}
