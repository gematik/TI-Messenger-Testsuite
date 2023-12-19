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

package de.gematik.tim.test.glue.api.login;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ACCOUNT_PASSWORD;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DISPLAY_NAME;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_ORG_ADMIN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ROOM_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LOGIN;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirAuthenticateTask.authenticateOnFhirVzd;
import static de.gematik.tim.test.glue.api.login.IsLoggedInAbility.logOut;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.room.tasks.ForgetRoomTask.forgetRoom;
import static de.gematik.tim.test.glue.api.room.tasks.LeaveRoomTask.leaveRoom;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.parallelClient;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.models.AuthStageNameDTO.BASICAUTH;
import static java.util.Objects.nonNull;

import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.fhir.practitioner.CanDeleteOwnMxidAbility;
import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.glue.api.threading.ActorsNotes;
import de.gematik.tim.test.glue.api.threading.Parallel;
import de.gematik.tim.test.glue.api.utils.ParallelUtils;
import de.gematik.tim.test.glue.api.utils.TestsuiteInitializer;
import de.gematik.tim.test.models.AccountDTO;
import de.gematik.tim.test.models.LoginDTO;
import java.io.IOException;
import java.util.Optional;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

public class LoginTask implements Task, Parallel<ActorsNotes> {

  private boolean clearRooms = TestsuiteInitializer.CLEAR_ROOMS;

  public static LoginTask login() {
    return new LoginTask();
  }

  @Override
  public <T extends Actor> void performAs(T actor) {
    Optional<LoginDTO> loginDto = getLoginDto(actor);
    if (loginDto.isPresent()) {
      actor.attemptsTo(LOGIN.request().with(req -> req.body(loginDto.get())));
    } else {
      actor.attemptsTo(LOGIN.request());
    }
    AccountDTO account = parseResponse(AccountDTO.class);
    actor.remember(MX_ID, account.getMxid());
    actor.remember(ACCOUNT_PASSWORD, account.getPassword());
    actor.remember(DISPLAY_NAME, account.getDisplayName());
    if (clearRooms) {
      actor.asksFor(ownRooms())
          .forEach(room -> {
            actor.attemptsTo(leaveRoom().withName(room.getName()));
            actor.attemptsTo(forgetRoom().withName(room.getName()));
          });
    }
    if (nonNull(actor.abilityTo(CanDeleteOwnMxidAbility.class))) {
      actor.attemptsTo(authenticateOnFhirVzd());
    }
    actor.can(logOut());
    actor.remember(IS_LOGGED_IN, true);
    if (actor.recall(IS_ORG_ADMIN) == null) {
      RawDataStatistics.login();
    }
  }

  private <T extends Actor> Optional<LoginDTO> getLoginDto(T actor) {
    return getLoginDto(new ActorsNotes(actor));
  }

  private Optional<LoginDTO> getLoginDto(ActorsNotes notes) {
    if (notes.recall(MX_ID) != null) {
      return Optional.of(new LoginDTO()
          .authStage(BASICAUTH)
          .username((String) notes.recall(MX_ID))
          .password((String) notes.recall(ACCOUNT_PASSWORD)));
    }
    return Optional.empty();
  }

  public LoginTask withoutClearingRooms() {
    this.clearRooms = false;
    return this;
  }

  @Override
  public ActorsNotes parallel(ActorsNotes notes) {
    String statusLine = "Undefined error";
    int code = HttpStatus.BAD_REQUEST.value();
    Request request = getLoginDto(notes)
        .map(loginDTO -> LOGIN.parallelRequest(notes).build(loginDTO))
        .orElseGet(() -> LOGIN.parallelRequest(notes).build());
    try (Response res = parallelClient().get().newCall(request).execute()) {
      code = res.code();
      if (!res.isSuccessful()) {
        statusLine = "%s %d %s".formatted(res.protocol(), res.code(), res.message());
        throw new TestRunException("Login failed: " + res.body().string());
      }
      AccountDTO account = ParallelUtils.fromJson(res.body().string(), AccountDTO.class);
      notes.remember(MX_ID, account.getMxid());
      notes.remember(ACCOUNT_PASSWORD, account.getPassword());
      notes.remember(DISPLAY_NAME, account.getDisplayName());
    } catch (IOException e) {
      throw new TestRunException("login failed for actor '%s'".formatted(notes.getName()), e);
    } finally {
      if (notes.recall(IS_ORG_ADMIN) == null) {
        RawDataStatistics.login(code, statusLine);
      }
    }

    if (clearRooms) {
      ownRooms().parallel(notes)
          .forEach(room -> {
            notes.remember(ROOM_ID, room.getRoomId());
            leaveRoom().parallel(notes);
            forgetRoom().parallel(notes);
          });
    }
    notes.addAbility(logOut());
    notes.remember(IS_LOGGED_IN, true);
    return notes;
  }
}
