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
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.HOME_SERVER;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_LOGGED_IN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.IS_ORG_ADMIN;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.LOGIN;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.fhir.practitioner.FhirAuthenticateTask.authenticateOnFhirVzd;
import static de.gematik.tim.test.glue.api.login.IsLoggedInAbility.logOut;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static de.gematik.tim.test.glue.api.room.tasks.ForgetRoomTask.forgetRoom;
import static de.gematik.tim.test.glue.api.room.tasks.LeaveRoomTask.leaveRoom;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.getParallelClient;
import static de.gematik.tim.test.glue.api.threading.ParallelExecutor.saveLastResponseCode;
import static de.gematik.tim.test.glue.api.utils.GlueUtils.getHomeServerWithoutHttpAndPort;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.fromJson;
import static de.gematik.tim.test.glue.api.utils.ParallelUtils.toJson;
import static de.gematik.tim.test.glue.api.utils.RequestResponseUtils.parseResponse;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.models.AuthStageNameDTO.BASICAUTH;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.tim.test.glue.api.fhir.practitioner.CanDeleteOwnMxidAbility;
import de.gematik.tim.test.glue.api.rawdata.RawDataStatistics;
import de.gematik.tim.test.glue.api.threading.ParallelTaskRunner;
import de.gematik.tim.test.glue.api.utils.TestsuiteInitializer;
import de.gematik.tim.test.models.AccountDTO;
import de.gematik.tim.test.models.LoginDTO;
import java.util.Optional;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.UnirestInstance;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class LoginTask extends ParallelTaskRunner implements Task {

  private boolean clearRooms = TestsuiteInitializer.CLEAR_ROOMS;

  public static LoginTask login() {
    return new LoginTask();
  }

  public LoginTask withoutClearingRooms() {
    this.clearRooms = false;
    return this;
  }

  @Override
  public void runParallel() {
    UnirestInstance client = getParallelClient().get();
    Optional<LoginDTO> loginDto = getLoginDto(actor);

    HttpResponse<JsonNode> resp;
    if (loginDto.isPresent()) {
      resp =
          client
              .post(LOGIN.getResolvedPath(actor))
              .header(TEST_CASE_ID_HEADER, getTestcaseId())
              .body(toJson(loginDto.get()))
              .asJson();
    } else {
      resp =
          client
              .post(LOGIN.getResolvedPath(actor))
              .header(TEST_CASE_ID_HEADER, getTestcaseId())
              .asJson();
    }
    AccountDTO account = fromJson(resp.getBody().toString(), AccountDTO.class);
    if (actor.recall(IS_ORG_ADMIN) == null) {
      RawDataStatistics.login(resp.getStatus(), resp.getStatusText());
    }
    saveLastResponseCode(actor.getName(), resp.getStatus());
    cleanRoomAndSetProperties(actor, account);
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
    if (actor.recall(IS_ORG_ADMIN) == null) {
      RawDataStatistics.login();
    }
    cleanRoomAndSetProperties(actor, account);
  }

  private <T extends Actor> void cleanRoomAndSetProperties(T actor, AccountDTO account) {
    if (actor.recall(IS_ORG_ADMIN) == null) {
      assertThat(account.getMxid())
          .as(
              "The client mxid (%s) does not match the home server (%s)",
              account.getMxid(), actor.recall(HOME_SERVER))
          .contains(getHomeServerWithoutHttpAndPort(actor));
      actor.remember(MX_ID, account.getMxid());
      actor.remember(ACCOUNT_PASSWORD, account.getPassword());
      actor.remember(DISPLAY_NAME, account.getDisplayName());
    }
    actor.can(logOut());
    actor.remember(IS_LOGGED_IN, true);

    if (clearRooms) {
      ownRooms()
          .withActor(actor)
          .run()
          .forEach(
              room -> {
                leaveRoom().withName(room.getName()).withActor(actor).run();
                forgetRoom().withName(room.getName()).withActor(actor).run();
              });
    }
    if (nonNull(actor.abilityTo(CanDeleteOwnMxidAbility.class))) {
      actor.attemptsTo(authenticateOnFhirVzd());
    }
  }

  private <T extends Actor> Optional<LoginDTO> getLoginDto(T actor) {
    if (actor.recall(MX_ID) != null) {
      return Optional.of(
          new LoginDTO()
              .authStage(BASICAUTH)
              .username(actor.recall(MX_ID))
              .password(actor.recall(ACCOUNT_PASSWORD)));
    }
    return Optional.empty();
  }
}
