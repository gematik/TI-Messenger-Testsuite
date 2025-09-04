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

package de.gematik.tim.test.glue.api.room.tasks;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.FORGET_ROOM;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.threading.ClientFactory.getClient;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static net.serenitybdd.rest.SerenityRest.lastResponse;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import lombok.SneakyThrows;
import net.serenitybdd.screenplay.Actor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;

public class ForgetRoomTask extends RoomSpecificTask {

  public static ForgetRoomTask forgetRoom() {
    return new ForgetRoomTask();
  }

  public ForgetRoomTask withName(String roomName) {
    return forRoomName(roomName);
  }

  private static final int NO_CONTENT = 204;

  @Override
  public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(FORGET_ROOM.request());
    if (lastResponse().statusCode() == NO_CONTENT) {
      actor.abilityTo(UseRoomAbility.class).removeCurrent();
    }
  }

  @Override
  @SneakyThrows
  public void runParallel() {
    final CloseableHttpClient client = getClient();
    final HttpDelete delete = new HttpDelete(FORGET_ROOM.getResolvedPath(actor));
    delete.addHeader(TEST_CASE_ID_HEADER, getTestcaseId());
    try (final CloseableHttpResponse response = client.execute(delete)) {
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode >= 200 && statusCode < 300) {
        actor.abilityTo(UseRoomAbility.class).removeCurrent();
      } else {
        throw new TestRunException(
            "Could not forget room, response code was %d".formatted(statusCode));
      }
    }
  }
}
