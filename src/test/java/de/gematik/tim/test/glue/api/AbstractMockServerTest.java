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

import static java.nio.charset.StandardCharsets.UTF_8;

import de.gematik.test.tiger.lib.TigerDirector;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractMockServerTest {

  private static final String TIGER_CFG_FILE_PROPERTY = "TIGER_TESTENV_CFGFILE";
  private static final String TIGER_UNIT_TEST_YAML =
      "/de/gematik/tim/test/glue/api/utils/tiger-unit-test.yml";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  protected static MockWebServer server;
  protected static String url;

  @BeforeAll
  static void startTigerAndMockServer() throws URISyntaxException, IOException {
    System.setProperty(
        TIGER_CFG_FILE_PROPERTY,
        Path.of(
                Objects.requireNonNull(
                        AbstractMockServerTest.class.getResource(TIGER_UNIT_TEST_YAML))
                    .toURI())
            .toString());
    TigerDirector.start();

    server = new MockWebServer();
    QueueDispatcher dispatcher = new QueueDispatcher();
    dispatcher.setFailFast(
        new MockResponse()
            .setResponseCode(599)
            .setBody(
                """
                {
                    "errorCode": "599",
                    "errorMessage": "MockWebServer: no response enqueued"
                }
                """)
            .setHeader(CONTENT_TYPE, APPLICATION_JSON));
    server.setDispatcher(dispatcher);
    server.start();
    url = server.url("/mock").toString();
  }

  @AfterAll
  static void stopMockServerAndTiger() throws IOException {
    server.shutdown();
    TigerDirector.testUninitialize();
    System.clearProperty(TIGER_CFG_FILE_PROPERTY);
  }

  protected static void mockResponse(String body) {
    server.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(CONTENT_TYPE, APPLICATION_JSON)
            .setBody(body));
  }

  protected void mockResponseFromResource(String resourceName) throws IOException {
    mockResponse(readResource(resourceName));
  }

  protected String readResource(String resourceName) throws IOException {
    return new String(
        Objects.requireNonNull(getClass().getResourceAsStream(resourceName)).readAllBytes(), UTF_8);
  }
}
