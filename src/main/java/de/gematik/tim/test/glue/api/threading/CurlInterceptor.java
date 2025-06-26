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

import kong.unirest.Config;
import kong.unirest.HttpRequest;
import kong.unirest.Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CurlInterceptor implements Interceptor {

  @Override
  public void onRequest(HttpRequest<?> request, Config config) {
    String headers = extractHeadersFrom(request);
    String body = extractBody(request);
    log.info(
        "PARALLEL cURL command: curl -v {}{} -X {} {}",
        headers,
        body,
        request.getHttpMethod(),
        request.getUrl());
  }

  @NotNull
  private static String extractHeadersFrom(HttpRequest<?> request) {
    StringBuilder headers = new StringBuilder();
    request
        .getHeaders()
        .all()
        .forEach(
            header ->
                headers
                    .append("-H \"")
                    .append(header.getName())
                    .append(": ")
                    .append(header.getValue())
                    .append("\" "));
    return headers.toString();
  }

  private String extractBody(HttpRequest<?> request) {
    return request.getBody().map(body -> "-d " + body.uniPart().getValue()).orElse("");
  }
}
