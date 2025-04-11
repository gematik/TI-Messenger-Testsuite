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

package de.gematik.tim.test.glue.api;

import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod.DELETE;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod.GET;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod.POST;
import static de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod.PUT;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ACCOUNT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ALLOWED_USERS_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.AUTHENTICATED_MEDIA_DOWNLOAD_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.AUTHORIZATION_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.BLOCKED_USERS_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.CLAIM_DEVICE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.CONTACT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DELETE_CONTACT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DEVICES_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DEVICE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DIRECT_MESSAGE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_AUTHENTICATE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_ENDPOINT_ADMIN_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_ENDPOINT_ADMIN_PATH_VISIBILITY;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_ENDPOINT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_HEALTHCARE_SERVICE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_HS_ADMIN_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_ORG_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_PRACTITIONER_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_SEARCH_ENDPOINT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_SEARCH_ORG_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_SEARCH_PRACTITIONER_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.FHIR_VISIBILITY_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.HOMESERVER_SEARCH_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.INFO_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.KEY_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.LOGIN_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.LOGOUT_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.LOGOUT_WITH_SYNC_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MEDIA_DOWNLOAD_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MEDIA_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MESSAGE_ID_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.MESSAGE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOMS_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_INVITE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_JOIN_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_LEAVE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_STATE_PATH;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.UNCLAIM_DEVICE_PATH;
import static java.util.Objects.requireNonNull;

import de.gematik.tim.test.glue.api.devices.UseDeviceAbility;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import de.gematik.tim.test.glue.api.fhir.organisation.endpoint.UseEndpointAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.LookForDeletedHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.fhir.organisation.healthcareservice.UseHealthcareServiceAbility;
import de.gematik.tim.test.glue.api.room.UseRoomAbility;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.screenplay.rest.interactions.RestInteraction;

@Getter
public enum TestdriverApiEndpoint {
  GET_INFO(GET, INFO_PATH),

  // DEVICE
  GET_DEVICES(GET, DEVICES_PATH),
  CLAIM_DEVICE(POST, CLAIM_DEVICE_PATH, UseDeviceAbility.class),
  UNCLAIM_DEVICE(POST, UNCLAIM_DEVICE_PATH, UseDeviceAbility.class),

  // ACCOUNT
  GET_ACCOUNT(GET, ACCOUNT_PATH, UseDeviceAbility.class),
  GET_SESSION_KEY(GET, KEY_PATH, UseDeviceAbility.class),
  IMPORT_SESSION_KEY(POST, KEY_PATH, UseDeviceAbility.class),

  // LOGIN
  LOGIN(POST, LOGIN_PATH, UseDeviceAbility.class),
  LOGOUT(POST, LOGOUT_PATH, UseDeviceAbility.class),
  LOGOUT_WITH_SYNC(POST, LOGOUT_WITH_SYNC_PATH, UseDeviceAbility.class),

  // ROOM
  GET_ROOMS(GET, ROOMS_PATH, UseDeviceAbility.class),
  GET_ROOM(GET, ROOM_ID_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  CREATE_ROOM(POST, ROOMS_PATH, UseDeviceAbility.class),
  FORGET_ROOM(DELETE, ROOM_ID_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  INVITE_TO_ROOM(POST, ROOM_INVITE_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  JOIN_ROOM(POST, ROOM_JOIN_PATH, UseDeviceAbility.class),
  LEAVE_ROOM(POST, ROOM_LEAVE_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  DENY_ROOM(POST, ROOM_LEAVE_PATH, UseDeviceAbility.class),
  GET_ROOM_STATES(GET, ROOM_STATE_PATH, UseDeviceAbility.class, UseRoomAbility.class),

  // MESSAGE
  SEND_MESSAGE(POST, MESSAGE_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  SEND_DIRECT_MESSAGE(POST, DIRECT_MESSAGE_PATH, UseDeviceAbility.class),
  EDIT_MESSAGE(PUT, MESSAGE_ID_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  GET_MESSAGES(GET, MESSAGE_PATH, UseDeviceAbility.class, UseRoomAbility.class),
  DELETE_MESSAGE(DELETE, MESSAGE_ID_PATH, UseDeviceAbility.class, UseRoomAbility.class),

  // HOMESERVER SEARCH
  SEARCH_ON_HOMESERVER(GET, HOMESERVER_SEARCH_PATH, UseDeviceAbility.class),

  // FHIR PRACTITIONER
  SEARCH_PRACTITIONER(GET, FHIR_SEARCH_PRACTITIONER_PATH, UseDeviceAbility.class),
  AUTHENTICATE_PRACTITIONER(POST, FHIR_AUTHENTICATE_PATH, UseDeviceAbility.class),
  SET_MXID_PRACTITIONER(POST, FHIR_PRACTITIONER_PATH, UseDeviceAbility.class),
  READ_MXID_PRACTITIONER(GET, FHIR_PRACTITIONER_PATH, UseDeviceAbility.class),
  DELETE_MXID_PRACTITIONER(DELETE, FHIR_PRACTITIONER_PATH, UseDeviceAbility.class),
  SET_ENDPOINT_VISIBILITY_PRACTITIONER(POST, FHIR_VISIBILITY_PATH, UseDeviceAbility.class),
  DELETE_ENDPOINT_VISIBILITY_PRACTITIONER(DELETE, FHIR_VISIBILITY_PATH, UseDeviceAbility.class),

  // FHIR ORG_ADMIN
  SEARCH_ORG(GET, FHIR_SEARCH_ORG_PATH, UseDeviceAbility.class),
  SEARCH_ENDPOINT(GET, FHIR_SEARCH_ENDPOINT_PATH, UseDeviceAbility.class),
  GET_OWN_ORG(GET, FHIR_ORG_PATH, UseDeviceAbility.class),

  // FHIR ORG_ADMIN HEALTHCARE SERVICE
  CREATE_HEALTHCARE_SERVICE(POST, FHIR_HEALTHCARE_SERVICE_PATH, UseDeviceAbility.class),
  GET_HEALTHCARE_SERVICE(
      GET, FHIR_HS_ADMIN_PATH, UseDeviceAbility.class, UseHealthcareServiceAbility.class),
  SEARCH_DELETED_HEALTHCARE_SERVICE(
      GET,
      FHIR_HS_ADMIN_PATH,
      UseDeviceAbility.class,
      LookForDeletedHealthcareServiceAbility.class),
  UPDATE_HEALTHCARE_SERVICE(
      PUT, FHIR_HS_ADMIN_PATH, UseDeviceAbility.class, UseHealthcareServiceAbility.class),
  DELETE_HEALTHCARE_SERVICE(
      DELETE, FHIR_HS_ADMIN_PATH, UseDeviceAbility.class, UseHealthcareServiceAbility.class),

  // FHIR ORG_ADMIN ENDPOINT
  GET_ENDPOINTS(GET, FHIR_ENDPOINT_PATH, UseDeviceAbility.class, UseHealthcareServiceAbility.class),
  CREATE_ENDPOINT(
      POST, FHIR_ENDPOINT_PATH, UseDeviceAbility.class, UseHealthcareServiceAbility.class),
  GET_ENDPOINT(
      GET,
      FHIR_ENDPOINT_ADMIN_PATH,
      UseDeviceAbility.class,
      UseHealthcareServiceAbility.class,
      UseEndpointAbility.class),
  UPDATE_ENDPOINT(
      PUT,
      FHIR_ENDPOINT_ADMIN_PATH,
      UseDeviceAbility.class,
      UseHealthcareServiceAbility.class,
      UseEndpointAbility.class),
  DELETE_ENDPOINT(
      DELETE,
      FHIR_ENDPOINT_ADMIN_PATH,
      UseDeviceAbility.class,
      UseHealthcareServiceAbility.class,
      UseEndpointAbility.class),
  SET_ENDPOINT_VISIBILITY(
      POST,
      FHIR_ENDPOINT_ADMIN_PATH_VISIBILITY,
      UseDeviceAbility.class,
      UseHealthcareServiceAbility.class,
      UseEndpointAbility.class),
  DELETE_ENDPOINT_VISIBILITY(
      DELETE,
      FHIR_ENDPOINT_ADMIN_PATH_VISIBILITY,
      UseDeviceAbility.class,
      UseHealthcareServiceAbility.class,
      UseEndpointAbility.class),

  // MEDIA
  UPLOAD_MEDIA(POST, MEDIA_PATH, UseDeviceAbility.class),
  DOWNLOAD_MEDIA(GET, MEDIA_DOWNLOAD_PATH, UseDeviceAbility.class),
  DOWNLOAD_AUTHENTICATED_MEDIA(GET, AUTHENTICATED_MEDIA_DOWNLOAD_PATH, UseDeviceAbility.class),

  // CONTACT-MANAGEMENT
  GET_CONTACT(GET, CONTACT_PATH, UseDeviceAbility.class),
  ADD_CONTACT(POST, CONTACT_PATH, UseDeviceAbility.class),
  DELETE_CONTACT(DELETE, DELETE_CONTACT_PATH, UseDeviceAbility.class),

  // AUTHORIZATION
  GET_AUTHORIZATION_MODE(GET, AUTHORIZATION_PATH, UseDeviceAbility.class),
  SET_AUTHORIZATION_MODE(POST, AUTHORIZATION_PATH, UseDeviceAbility.class),

  GET_ALLOW_LIST(GET, ALLOWED_USERS_PATH, UseDeviceAbility.class),
  GET_BLOCK_LIST(GET, BLOCKED_USERS_PATH, UseDeviceAbility.class),
  ADD_ALLOWED_USERS(POST, ALLOWED_USERS_PATH, UseDeviceAbility.class),
  ADD_BLOCKED_USERS(POST, BLOCKED_USERS_PATH, UseDeviceAbility.class),
  DELETE_ALLOW_LIST(DELETE, ALLOWED_USERS_PATH, UseDeviceAbility.class),
  DELETE_BLOCK_LIST(DELETE, BLOCKED_USERS_PATH, UseDeviceAbility.class);

  private final HttpMethod httpMethod;
  private final String path;
  private final List<Class<? extends TestdriverApiAbility>> neededAbilities;

  @SafeVarargs
  TestdriverApiEndpoint(
      HttpMethod httpMethod,
      String path,
      Class<? extends TestdriverApiAbility>... neededAbilities) {
    this.httpMethod = httpMethod;
    this.path = path;
    this.neededAbilities = Arrays.stream(neededAbilities).toList();
  }

  public TestdriverApiInteraction request() {
    RestInteraction restInteraction = httpMethod.creator.apply(path);
    return new TestdriverApiInteraction(restInteraction, neededAbilities);
  }

  public String getResolvedPath(Actor actor) {
    String resourcePath = this.getPath();
    for (Class<? extends TestdriverApiAbility> clazz : this.neededAbilities) {
      if (clazz == UseDeviceAbility.class) {
        UseDeviceAbility ability = (UseDeviceAbility) actor.abilityTo(clazz);
        requireNonNull(
            ability,
            "Actor '%s' needs the '%s' to perform this request!"
                .formatted(actor.getName(), clazz.getSimpleName()));
        resourcePath =
            resourcePath.replaceAll(
                "\\{" + DEVICE_ID_VARIABLE + "\\}", String.valueOf(ability.getDeviceId()));
      } else if (clazz == UseRoomAbility.class) {
        UseRoomAbility ability = (UseRoomAbility) actor.abilityTo(clazz);
        requireNonNull(
            ability,
            "Actor '%s' needs the '%s' to perform this request!"
                .formatted(actor.getName(), clazz.getSimpleName()));
        resourcePath =
            resourcePath.replaceAll(
                "\\{" + ROOM_ID_VARIABLE + "\\}", String.valueOf(ability.getActive().getRoomId()));
      } else {
        throw new TestRunException(
            "Please implement behaviour for needed ability %s".formatted(clazz.getSimpleName()));
      }
    }
    return actor.abilityTo(CallAnApi.class).resolve("") + resourcePath;
  }

  @RequiredArgsConstructor
  public enum HttpMethod {
    GET(Get::resource),
    POST(Post::to),
    PUT(Put::to),
    DELETE(Delete::from);
    public final Function<String, RestInteraction> creator;
  }
}
