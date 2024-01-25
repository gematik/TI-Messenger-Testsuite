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

import static de.gematik.tim.test.glue.api.devices.UseDeviceAbility.TEST_CASE_ID_HEADER;
import static de.gematik.tim.test.glue.api.utils.TestcasePropertiesManager.getTestcaseId;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.HTTP_TIMEOUT;

import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.test.tiger.proxy.TigerProxy;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

public class ClientFactory {

  private static final String ACCEPT = "*/*";
  private static final String CONTENT_TYPE = "application/json";
  private static ClientFactory instance;
  private static final UnirestInstance client = Unirest.spawnInstance();

  private ClientFactory() {
    TigerProxy proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail();
    client.config()
        .proxy("localhost", proxy.getProxyPort())
        .addDefaultHeader("Accept", ACCEPT)
        .addDefaultHeader("Content-Type", CONTENT_TYPE)
        .addDefaultHeader(TEST_CASE_ID_HEADER, getTestcaseId())
        .interceptor(new CurlInterceptor())
        .connectTimeout(HTTP_TIMEOUT * 1000)
        .socketTimeout(HTTP_TIMEOUT * 1000)
        .sslContext(proxy.buildSslContext());
  }

  public static UnirestInstance getClient() {
    if (instance == null) {
      instance = new ClientFactory();
    }
    return ClientFactory.client;
  }

}