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

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.GET_OWN_ORG;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;

import de.gematik.tim.test.models.FhirOrganizationDTO;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class FhirOwnOrganizationQuestion implements Question<FhirOrganizationDTO> {

  public static FhirOwnOrganizationQuestion ownOrgId() {
    return new FhirOwnOrganizationQuestion();
  }

  @Override
  public FhirOrganizationDTO answeredBy(Actor actor) {
    return repeatedRequest(() -> getOrg(actor));
  }

  private Optional<FhirOrganizationDTO> getOrg(Actor actor) {
    actor.attemptsTo(GET_OWN_ORG.request());
    try {
      return Optional.of(parseResponse(FhirOrganizationDTO.class));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }
}
