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

package de.gematik.tim.test.glue.api.threading;

import static de.gematik.tim.test.glue.api.ActorMemoryKeys.DEVICE_ID;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.ROOM_ID;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.DEVICE_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.TestdriverApiPath.ROOM_ID_VARIABLE;
import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;

import de.gematik.tim.test.glue.api.TestdriverApiEndpoint;
import de.gematik.tim.test.glue.api.TestdriverApiEndpoint.HttpMethod;
import de.gematik.tim.test.glue.api.utils.ParallelUtils;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class ParallelRequest {

  private static final String ACCEPT = "*/*";
  private static final String CONTENT_TYPE = "application/json";
  private static final MediaType MEDIA_TYPE = MediaType.get(CONTENT_TYPE);
  private Request.Builder builder;
  private final ActorsNotes notes;
  private final HttpMethod method;
  private String resourcePath;

  public ParallelRequest(TestdriverApiEndpoint endpoint, ActorsNotes notes) {
    this.notes = notes;
    this.resourcePath = endpoint.getPath();
    this.method = endpoint.getHttpMethod();
    this.builder = new Request.Builder()
        .header("Accept", ACCEPT)
        .header("Content-Type", CONTENT_TYPE)
        .header(TEST_CASE_ID_HEADER, getTestcaseId());
  }

  private String composeUrl() {
    if (notes.containsKey(DEVICE_ID)) {
      resourcePath = resourcePath.replaceAll(
          "\\{" + DEVICE_ID_VARIABLE + "\\}", notes.recall(DEVICE_ID).toString());
    }
    if (notes.containsKey(ROOM_ID)) {
      resourcePath = resourcePath.replaceAll(
          "\\{" + ROOM_ID_VARIABLE + "\\}", notes.recall(ROOM_ID).toString());
    }
    if (resourcePath.contains("{")) {
      throw new IllegalStateException(
          "Path could not be resolved correctly, there is a path variable still to replace: "
              + resourcePath);
    }
    return notes.getApi() + resourcePath;
  }

  public Request build() {
    return build(RequestBody.create("", MEDIA_TYPE));
  }

  @SneakyThrows
  public Request build(@NonNull Object body) {
    String content = ParallelUtils.toJson(body);
    return build(RequestBody.create(content, MEDIA_TYPE));
  }

  public Request build(RequestBody body) {
    final String url = composeUrl();
    builder = switch (method) {
      case GET -> builder.get();
      case POST -> builder.post(body);
      case DELETE -> builder.delete(body);
      case PUT -> throw new TestRunException(
          "HttpMethod '%s' is not supported for endpoint %s".formatted(method, url));
    };
    return builder.url(url).build();
  }
}