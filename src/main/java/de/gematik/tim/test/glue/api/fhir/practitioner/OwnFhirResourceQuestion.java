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

package de.gematik.tim.test.glue.api.fhir.practitioner;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.READ_MXID_PRACTITIONER;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.orderByResourceType;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.repeatedRequest;
import static de.gematik.tim.test.models.FhirResourceTypeDTO.ENDPOINT;

import de.gematik.tim.test.models.FhirBaseResourceDTO;
import de.gematik.tim.test.models.FhirSearchResultDTO;
import java.util.List;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class OwnFhirResourceQuestion implements Question<FhirSearchResultDTO> {

  private int amount = 0;

  public static OwnFhirResourceQuestion ownFhirResource() {
    return new OwnFhirResourceQuestion();
  }

  public OwnFhirResourceQuestion withAtLeastAmountEndpoints(int amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public FhirSearchResultDTO answeredBy(Actor actor) {
    return repeatedRequest(() -> searchOwnEntry(actor), "SearchOwnPractitionerInfos");
  }

  private Optional<FhirSearchResultDTO> searchOwnEntry(Actor actor) {
    actor.attemptsTo(READ_MXID_PRACTITIONER.request());
    FhirSearchResultDTO result = parseResponse(FhirSearchResultDTO.class);

    List<FhirBaseResourceDTO> ownInfos = orderByResourceType(result).get(ENDPOINT);
    if (ownInfos.size() >= amount) {
      return Optional.of(result);
    }
    return Optional.empty();
  }
}
