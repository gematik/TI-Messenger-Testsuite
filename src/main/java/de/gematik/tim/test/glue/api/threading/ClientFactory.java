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

import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.HTTP_TIMEOUT;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.TIMEOUT;

import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.test.tiger.proxy.TigerProxy;
import java.util.Arrays;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;

public class ClientFactory {
  private ClientFactory() {}

  private static final String ACCEPT = "*/*";
  private static final String CONTENT_TYPE = "application/json";
  private static final CloseableHttpClient client;
  private static final CloseableHttpClient cleanUpClient;

  static {
    final TigerProxy proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail();
    client = HttpClients.custom()
        .setDefaultRequestConfig(
            RequestConfig.custom()
                .setConnectTimeout(HTTP_TIMEOUT * 1000)
                .setSocketTimeout(HTTP_TIMEOUT * 1000)
                .setProxy(new HttpHost("localhost", proxy.getProxyPort()))
                .build())
        .setSSLContext(proxy.buildSslContext())
        .setDefaultHeaders(Arrays.asList(
            new BasicHeader("Accept", ACCEPT),
            new BasicHeader("Content-Type", CONTENT_TYPE)
        ))
        .addInterceptorLast(new CurlInterceptor())
        .build();

    cleanUpClient =
        HttpClients.custom()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectTimeout(TIMEOUT.intValue() * 2000)
                    .setSocketTimeout(TIMEOUT.intValue() * 2000)
                    .setProxy(new HttpHost("localhost", proxy.getProxyPort()))
                    .build())
            .setSSLContext(proxy.buildSslContext())
            .setDefaultHeaders(
                Arrays.asList(
                    new BasicHeader("Accept", ACCEPT),
                    new BasicHeader("Content-Type", CONTENT_TYPE)
                    ))
            .addInterceptorLast(new CurlInterceptor())
            .build();
  }

  public static CloseableHttpClient getClient() {
    return ClientFactory.client;
  }

  public static CloseableHttpClient getCleanUpClient() {
    return ClientFactory.cleanUpClient;
  }
}
