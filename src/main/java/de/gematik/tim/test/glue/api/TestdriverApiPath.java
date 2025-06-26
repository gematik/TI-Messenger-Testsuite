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

package de.gematik.tim.test.glue.api;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@SuppressWarnings("java:S1075")
@NoArgsConstructor(access = PRIVATE)
public final class TestdriverApiPath {

  // VARIABLES
  public static final String PATH_VARIABLE = "{%s}";

  public static final String DEVICE_ID_VARIABLE = "deviceId";
  public static final String ROOM_ID_VARIABLE = "roomId";
  public static final String MESSAGE_ID_VARIABLE = "messageId";
  public static final String HEALTHCARE_SERVICE_ID_VARIABLE = "hsId";
  public static final String ENDPOINT_ID_VARIABLE = "endId";
  public static final String MXID_VARIABLE = "mxid";
  public static final String MEDIA_ID_VARIABLE = "mediaId";

  public static final String INFO_PATH = "/";
  public static final String WELL_KNOWN_HOMESERVER_PATH = "/.well-known/matrix/client";
  public static final String SUPPORTED_VERSIONS_PATH = "/_matrix/client/versions";

  // DEVICE
  public static final String DEVICES_PATH = "/devices";
  public static final String DEVICE_ID_PATH =
      DEVICES_PATH + "/" + PATH_VARIABLE.formatted(DEVICE_ID_VARIABLE);

  public static final String CLAIM_DEVICE_PATH = DEVICE_ID_PATH + "/claim";
  public static final String UNCLAIM_DEVICE_PATH = DEVICE_ID_PATH + "/unclaim";
  public static final String ACCOUNT_PATH = DEVICE_ID_PATH + "/account";
  public static final String KEY_PATH = ACCOUNT_PATH + "/key";

  // ROOM
  public static final String ROOMS_PATH = DEVICE_ID_PATH + "/rooms";
  public static final String ROOM_ID_PATH =
      ROOMS_PATH + "/" + PATH_VARIABLE.formatted(ROOM_ID_VARIABLE);
  public static final String ROOM_INVITE_PATH = ROOM_ID_PATH + "/invite";
  public static final String ROOM_JOIN_PATH = ROOM_ID_PATH + "/join";
  public static final String ROOM_LEAVE_PATH = ROOM_ID_PATH + "/leave";
  public static final String ROOM_STATE_PATH = ROOM_ID_PATH + "/state";

  // MESSAGE
  public static final String DIRECT_MESSAGE_PATH = DEVICE_ID_PATH + "/sendMessage";
  public static final String MESSAGE_PATH = ROOM_ID_PATH + "/messages";
  public static final String MESSAGE_ID_PATH =
      MESSAGE_PATH + "/" + PATH_VARIABLE.formatted(MESSAGE_ID_VARIABLE);

  // LOGIN
  public static final String LOGIN_PATH = DEVICE_ID_PATH + "/login";
  public static final String LOGOUT_PATH = DEVICE_ID_PATH + "/logout";
  public static final String LOGOUT_WITH_SYNC_PATH = LOGOUT_PATH + "/synced";

  // HOMESERVER
  public static final String HOMESERVER_SEARCH_PATH = DEVICE_ID_PATH + "/homeserver/search";

  // FHIR PRACTIONER
  public static final String FHIR_BASE_PATH = DEVICE_ID_PATH + "/fhir";
  public static final String FHIR_SEARCH_PRACTITIONER_PATH =
      FHIR_BASE_PATH + "/searchPractitionerInFhirDirectory";
  public static final String FHIR_PRACTITIONER_PATH = FHIR_BASE_PATH + "/mxidInFhirDirectory";
  public static final String FHIR_VISIBILITY_PATH = FHIR_BASE_PATH + "/endpointVisibility";
  public static final String FHIR_AUTHENTICATE_PATH = FHIR_BASE_PATH + "/fhirAuthenticate";
  public static final String FHIR_ORG_PATH = FHIR_BASE_PATH + "/org";

  // FHIR ORG_ADMIN
  public static final String FHIR_SEARCH_ORG_PATH =
      FHIR_BASE_PATH + "/searchOrganizationInFhirDirectory";
  public static final String FHIR_SEARCH_ENDPOINT_PATH =
      FHIR_BASE_PATH + "/searchHealthcareServiceEndpointInFhirDirectory";
  public static final String FHIR_HEALTHCARE_SERVICE_PATH = FHIR_ORG_PATH + "/healthcareService";
  public static final String FHIR_HS_ADMIN_PATH =
      FHIR_HEALTHCARE_SERVICE_PATH + "/" + PATH_VARIABLE.formatted(HEALTHCARE_SERVICE_ID_VARIABLE);

  public static final String FHIR_ENDPOINT_PATH = FHIR_HS_ADMIN_PATH + "/endpoint";
  public static final String FHIR_ENDPOINT_ADMIN_PATH =
      FHIR_ENDPOINT_PATH + "/" + PATH_VARIABLE.formatted(ENDPOINT_ID_VARIABLE);

  public static final String FHIR_ENDPOINT_ADMIN_PATH_VISIBILITY =
      FHIR_HS_ADMIN_PATH + "/endpointVisibility/" + PATH_VARIABLE.formatted(ENDPOINT_ID_VARIABLE);

  // CONTACT-MANAGEMENT
  public static final String CONTACT_PATH = DEVICE_ID_PATH + "/contacts";
  public static final String DELETE_CONTACT_PATH =
      CONTACT_PATH + "/" + PATH_VARIABLE.formatted(MXID_VARIABLE);

  // MEDIA
  public static final String MEDIA_PATH = DEVICE_ID_PATH + "/media";
  public static final String MEDIA_DOWNLOAD_PATH =
      MEDIA_PATH + "/" + PATH_VARIABLE.formatted(MEDIA_ID_VARIABLE);

  public static final String AUTHENTICATED_MEDIA_DOWNLOAD_PATH =
      MEDIA_PATH + "/authenticated/" + PATH_VARIABLE.formatted(MEDIA_ID_VARIABLE);

  // AUTHORIZATION
  public static final String AUTHORIZATION_PATH = DEVICE_ID_PATH + "/authorizationMode";
  public static final String BLOCKED_USERS_PATH = DEVICE_ID_PATH + "/blockedUsers";
  public static final String ALLOWED_USERS_PATH = DEVICE_ID_PATH + "/allowedUsers";
}
