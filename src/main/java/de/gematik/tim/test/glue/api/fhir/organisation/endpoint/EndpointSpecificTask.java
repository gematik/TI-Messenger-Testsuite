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

package de.gematik.tim.test.glue.api.fhir.organisation.endpoint;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.HealthcareSpecificTask;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import lombok.Getter;
import net.serenitybdd.screenplay.Actor;

@Getter
public abstract class EndpointSpecificTask extends HealthcareSpecificTask {

  private String endpointName;

  @SuppressWarnings("unchecked")
  public <T extends EndpointSpecificTask> T withName(String endpointName) {
    this.endpointName = endpointName;
    return (T) this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    super.performAs(actor);
    UseEndpointAbility useEndpointAbility = actor.abilityTo(UseEndpointAbility.class);
    UseHealthcareServiceAbility hsAbility =
        requireNonNull(actor.abilityTo(UseHealthcareServiceAbility.class));
    hsAbility.setActive(useEndpointAbility.getActive().hsName());
    if (isNotBlank(getEndpointName())) {
      useEndpointAbility.setActive(getEndpointName());
    } else {
      endpointName = useEndpointAbility.getActiveKey();
    }
  }
}
