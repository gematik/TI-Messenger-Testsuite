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

package de.gematik.tim.test.glue.api.rawdata;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

public class RawDataEventCounter {

  private final AtomicInteger success = new AtomicInteger();
  @Getter private final Collection<String> errors = new LinkedBlockingQueue<>();

  public void countSuccess() {
    success.incrementAndGet();
  }

  public int getSuccessCount() {
    return success.get();
  }

  public void addError(String info) {
    errors.add(info);
  }

  public RawDataEventCounter copy() {
    RawDataEventCounter newCounter = new RawDataEventCounter();
    errors.forEach(newCounter::addError);
    newCounter.success.set(this.success.get());
    return newCounter;
  }

  public RawDataEventCounter getDiff(RawDataEventCounter counter) {
    RawDataEventCounter diffCounter = new RawDataEventCounter();
    Collection<String> newFoundErrors = this.copy().getErrors();
    counter.getErrors().forEach(newFoundErrors::remove);
    newFoundErrors.forEach(diffCounter::addError);
    diffCounter.success.set(this.success.get() - counter.getSuccessCount());
    return diffCounter;
  }
}
