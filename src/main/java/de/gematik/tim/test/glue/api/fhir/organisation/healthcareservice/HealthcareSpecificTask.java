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

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HAS_REG_SERVICE_TOKEN;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import lombok.Getter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

@Getter
public abstract class HealthcareSpecificTask implements Task {

  protected String hsName;

  @SuppressWarnings("unchecked")
  public <T extends HealthcareSpecificTask> T forHealthcareService(String hsName) {
    this.hsName = hsName;
    return (T) this;
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    UseHealthcareServiceAbility useHealthcareServiceAbility =
        actor.abilityTo(UseHealthcareServiceAbility.class);
    if (isNotBlank(getHsName())) {
      useHealthcareServiceAbility.setActive(getHsName());
    } else {
      hsName = useHealthcareServiceAbility.getActiveKey();
    }
    if (actor.recall(HAS_REG_SERVICE_TOKEN) == null) {
      RawDataStatistics.getRegTokenForVZDEvent();
      actor.remember(HAS_REG_SERVICE_TOKEN, true);
    }
  }
}
