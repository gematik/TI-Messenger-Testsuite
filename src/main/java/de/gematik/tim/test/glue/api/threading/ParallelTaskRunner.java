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

package de.gematik.tim.test.glue.api.threading;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public abstract class ParallelTaskRunner implements Task {

  protected Actor actor;

  public void run() {
    if (actor == null) {
      throw new TestRunException("Please provide an actor to perform parallel tasks");
    }
    if (TestcasePropertiesManager.isRunningParallel()) {
      runParallel();
    } else {
      actor.attemptsTo(this);
    }
  }

  protected abstract void runParallel();

  public ParallelTaskRunner withActor(Actor actor) {
    this.actor = actor;
    return this;
  }
}
