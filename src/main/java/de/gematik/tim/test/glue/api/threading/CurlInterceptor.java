/*
 * Copyright (Change Date see Readme) gematik GmbH
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

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

@Slf4j
public class CurlInterceptor implements HttpRequestInterceptor {

  @Override
  public void process(final HttpRequest request, final HttpContext context) {
    final StringBuilder curl = new StringBuilder("curl -v ");
    for (final Header header : request.getAllHeaders()) {
      curl.append("-H \"")
          .append(header.getName())
          .append(": ")
          .append(header.getValue())
          .append("\" ");
    }
    if (request instanceof final HttpEntityEnclosingRequest entityEnclosingRequest) {
      final HttpEntity entity = entityEnclosingRequest.getEntity();
      if (entity != null) {
        try (final InputStream is = entity.getContent()) {
          final String body = new String(is.readAllBytes());
          curl.append("-d '").append(body).append("' ");
        } catch (final IOException e) {
          log.warn("couldn't read request body", e);
        }
      }
    }
    curl.append("-X ").append(request.getRequestLine().getMethod()).append(" ");
    curl.append(request.getRequestLine().getUri());
    log.info("PARALLEL cURL command: {}", curl);
  }
}