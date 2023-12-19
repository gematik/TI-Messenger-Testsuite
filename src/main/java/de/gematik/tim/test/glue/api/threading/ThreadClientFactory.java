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

import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.HTTP_TIMEOUT;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.KEY_STORE_ENV_VAR;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.KEY_STORE_PW_ENV_VAR;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.TRUST_STORE_ENV_VAR;
import static de.gematik.tim.test.glue.api.utils.TestsuiteInitializer.TRUST_STORE_PW_ENV_VAR;
import static java.net.Proxy.Type.HTTP;
import static java.time.temporal.ChronoUnit.SECONDS;

import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.tim.test.glue.api.exceptions.TestRunException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@SuppressWarnings("java:S5527")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadClientFactory {

  private static final SSLContext mTlsContext;
  private static final TrustManagerFactory tmf;
  private static final Duration timeout = Duration.of(HTTP_TIMEOUT, SECONDS);
  private static final OkHttpClient client;

  static {
    try {
      String clientKeyStorePath = System.getenv(KEY_STORE_ENV_VAR);
      String clientKeyStoreType = clientKeyStorePath.endsWith("p12") ? "PKCS12" : "JKS";
      KeyStore clientKeyStore = KeyStore.getInstance(clientKeyStoreType);
      try (InputStream stream = new FileInputStream(clientKeyStorePath)) {
        clientKeyStore.load(stream, System.getenv(KEY_STORE_PW_ENV_VAR).toCharArray());
      }
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(clientKeyStore, System.getenv(KEY_STORE_PW_ENV_VAR).toCharArray());

      String trustStorePath = System.getenv(TRUST_STORE_ENV_VAR);
      String trustStoreType = trustStorePath.endsWith("p12") ? "PKCS12" : "JKS";
      KeyStore trustStore = KeyStore.getInstance(trustStoreType);
      try (InputStream stream = new FileInputStream(trustStorePath)) {
        trustStore.load(stream, System.getenv(TRUST_STORE_PW_ENV_VAR).toCharArray());
      }
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(trustStore);

      mTlsContext = SSLContext.getInstance("TLS");
      mTlsContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

      URI proxyUri = new URI(TigerDirector.getLocalTigerProxyUrl());

      //noinspection KotlinInternalInJava
      client = new Builder()
          .sslSocketFactory(mTlsContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
          .hostnameVerifier((hostname, session) -> true)
          .proxy(new Proxy(HTTP, new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort())))
          .addInterceptor(
              /* interceptor can be used for logging all parallel requests */
              chain -> {
                String headers = extractHeadersFrom(chain);
                String body = extractBodyFrom(chain);
                log.info("PARALLEL cURL command: curl -v {}{}-X {} {}",
                    headers, body, chain.request().method(), chain.request().url());
                return chain.proceed(chain.request());
              })
          .readTimeout(timeout)
          .callTimeout(timeout)
          .connectTimeout(timeout)
          .build();
    } catch (IOException | GeneralSecurityException | URISyntaxException e) {
      throw new TestRunException("something went wrong during sslContext setup", e);
    }
  }

  public static OkHttpClient getOkHttpClient() {
    return client;
  }

  @NotNull
  private static String extractHeadersFrom(Chain chain) {
    StringBuilder headers = new StringBuilder();
    chain.request().headers().forEach(
        h -> headers.append("-H \"").append(h.getFirst())
            .append(": ").append(h.getSecond()).append("\" "));
    return headers.toString();
  }

  @SneakyThrows
  private static String extractBodyFrom(Chain chain) {
    Request copy = chain.request().newBuilder().build();
    if (copy.body() != null) {
      Buffer buffer = new Buffer();
      copy.body().writeTo(buffer);
      String s = buffer.readUtf8().strip();
      return s.equals("null") || s.isBlank() ? "" : "-d '%s' ".formatted(s);
    } else {
      return "";
    }
  }
}
