/*
 * Copyright 20023 gematik GmbH
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

package de.gematik.tim.test.glue.api.utils;

import static com.nimbusds.jose.util.X509CertUtils.parse;
import static de.gematik.tim.test.glue.api.ActorMemoryKeys.MX_ID;
import static de.gematik.tim.test.glue.api.room.questions.GetRoomsQuestion.ownRooms;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.gson.Gson;
import de.gematik.test.tiger.common.data.config.tigerProxy.TigerRoute;
import de.gematik.test.tiger.lib.TigerDirector;
import de.gematik.test.tiger.proxy.TigerProxy;
import de.gematik.tim.test.glue.api.exceptions.RequestedRessourceNotAvailable;
import de.gematik.tim.test.models.MessageDTO;
import de.gematik.tim.test.models.RoomDTO;
import de.gematik.tim.test.models.RoomMemberDTO;
import io.cucumber.java.ParameterType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.Actor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.awaitility.core.ConditionTimeoutException;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class GlueUtils {

  public static final String TEST_RESOURCES_JSON_PATH = "src/test/resources/json/";
  public static final String MVN_PROPERTIES_LOCATION = "./target/classes/mvn.properties";
  private static final String RUN_WITHOUT_RETRY_PROPERTY_NAME = "runWithoutRetry";
  public static final String POLL_INTERVAL_PROPERTY_NAME = "pollInterval";
  public static final String TIMEOUT_PROPERTY_NAME = "timeout";
  private static final String SAVE_CONNECTIONS_PROPERTY_NAME = "saveConnections";
  public static final Boolean SAVE_CONNECTIONS;
  private static final String CLAIM_DURATION_PROPERTYNAME = "claimDuration";
  public static final Integer CLAIM_DURATION;
  public static final String CERT_CN;

  private static final List<String> knownUrls = new ArrayList<>();
  private static final String KEY_STORE = "TIM_KEYSTORE";
  private static final String KEY_STORE_PW = "TIM_KEYSTORE_PW";
  private static Long timeout;
  private static final Long TIMEOUT_DEFAULT = 10L;
  private static Long pollInterval;
  private static final Long POLL_INTERVAL_DEFAULT = 1L;
  private static final boolean RUN_WITHOUT_RETRY;


  static {
    Properties p = new Properties();
    try {
      FileInputStream is = FileUtils.openInputStream(new File(MVN_PROPERTIES_LOCATION));
      p.load(is);
    } catch (IOException e) {
      log.error("Could not find any maven properties at " + MVN_PROPERTIES_LOCATION);
      throw new IllegalArgumentException(e);
    }
    String timeoutString = p.getProperty(TIMEOUT_PROPERTY_NAME);
    String pollIntervalString = p.getProperty(POLL_INTERVAL_PROPERTY_NAME);
    RUN_WITHOUT_RETRY = Boolean.parseBoolean(p.getProperty(RUN_WITHOUT_RETRY_PROPERTY_NAME));
    SAVE_CONNECTIONS = Boolean.parseBoolean(p.getProperty(SAVE_CONNECTIONS_PROPERTY_NAME));
    CLAIM_DURATION = Integer.parseInt(p.getProperty(CLAIM_DURATION_PROPERTYNAME, "180"));
    CERT_CN = parseCn();
    try {
      timeout = Long.parseLong(timeoutString);
      pollInterval = Long.parseLong(pollIntervalString);
    } catch (Exception ex) {
      timeout = TIMEOUT_DEFAULT;
      pollInterval = POLL_INTERVAL_DEFAULT;
      log.info(format(
          "Could not parse timeout (%s) or pollInterval (%s). Will use default -> timeout: %s, pollInterval: %s",
          timeoutString, pollIntervalString, timeout, pollInterval));
    }
  }

  // Utils
  public static RoomDTO getRoomBetweenTwoActors(Actor actor, String senderName) {
    String actorId = actor.recall(MX_ID);
    String senderId = theActorCalled(senderName).recall(MX_ID);
    List<String> membersIds = List.of(actorId, senderId);
    List<RoomDTO> rooms = actor.asksFor(ownRooms());
    return requireNonNull(filterForRoomWithSpecificMembers(rooms, membersIds));
  }

  public static RoomDTO filterForRoomWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    List<RoomDTO> filteredRooms = filterForRoomsWithSpecificMembers(rooms, memberIds);
    assertThat(filteredRooms).as(
        "%s matching room for this members (%s) have been found.", filteredRooms.size(),
        StringUtils.join(memberIds, ","))
        .hasSize(1);
    return filteredRooms.get(0);
  }

  @NotNull
  public static List<RoomDTO> filterForRoomsWithSpecificMembers(List<RoomDTO> rooms,
      List<String> memberIds) {
    return rooms.stream()
        .filter(r -> requireNonNull(r.getMembers()).size() >= memberIds.size())
        .filter(
            r -> new HashSet<>(
                r.getMembers().stream()
                    .map(RoomMemberDTO::getMxid)
                    .filter(Objects::nonNull)
                    .toList()).containsAll(memberIds)).toList();
  }

  public static MessageDTO filterMessageForSenderAndText(String messageText, String userName,
      List<MessageDTO> messages) {
    List<MessageDTO> filteredMessages = filterMessagesForSenderAndText(messageText, userName,
        messages);
    assertThat(filteredMessages).as(
        "%s matching messages for this members (%s) have been found.", filteredMessages.size(),
        filteredMessages.size())
        .hasSize(1);
    return filteredMessages.get(0);
  }

  @NotNull
  public static List<MessageDTO> filterMessagesForSenderAndText(String message, String userName,
      List<MessageDTO> messages) {
    return messages.stream()
        .filter(e -> requireNonNull(e.getBody()).equals(message))
        .filter(e -> requireNonNull(e.getAuthor()).equals(theActorCalled(userName).recall(MX_ID)))
        .toList();
  }

  @ParameterType(value = "(?:.*)", preferForRegexMatch = true)
  public List<String> listOfStrings(String arg) {
    return stream(arg.split(",\\s?"))
        .map(str -> str.replace("\"", ""))
        .toList();
  }

  public static <T> T readJsonFile(String file, Class<T> type) {
    return new Gson().fromJson(readJsonFile(file), type);
  }

  @SneakyThrows
  public static String readJsonFile(String file) {
    FileReader fileReader = new FileReader(TEST_RESOURCES_JSON_PATH + file);
    return IOUtils.toString(fileReader);
  }

  public static boolean isSameHomeserver(String id1, String id2) {
    return homeserverFromMxId(id1).equals(homeserverFromMxId(id2));
  }

  public static String homeserverFromMxId(String mxId) {
    String[] splitted = mxId.split(":");
    if (splitted.length != 2) {
      throw new IllegalArgumentException(mxId + " is not a valid MxId");
    }
    return splitted[1];
  }

  private static String parseCn() {
    try (InputStream stream = new FileInputStream(System.getenv(KEY_STORE))) {
      KeyStore store = KeyStore.getInstance("PKCS12");
      store.load(stream, System.getenv(KEY_STORE_PW).toCharArray());
      X509Certificate cert = parse(
          store.getCertificate(store.aliases().nextElement()).getEncoded());
      RDN cn = new JcaX509CertificateHolder(cert).getSubject().getRDNs(BCStyle.CN)[0];
      return cn.getFirst().getValue().toString();
    } catch (Exception ex) {
      log.error("Could not parse certificate");
    }
    return "";
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request) {
    return repeatedRequest(request, "resource");
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request, String resourceType) {
    return repeatedRequest(request, resourceType, timeout, pollInterval);
  }

  public static <T> T repeatedRequestWithLongerTimeout(Supplier<Optional<T>> request,
      String resourceType, int factor) {
    return repeatedRequest(request, resourceType, timeout * factor, pollInterval);
  }

  public static <T> T repeatedRequest(Supplier<Optional<T>> request, String resourceType,
      Long customTimeout, Long customPollInterval) {
    if (customTimeout == null || customPollInterval == null) {
      customTimeout = timeout;
      customPollInterval = pollInterval;
    }
    if (customTimeout <= 1 || RUN_WITHOUT_RETRY) {
      return request.get()
          .orElseThrow(() -> new ConditionTimeoutException(
              format("Asked for %s, but could not be found", resourceType)));
    }
    return await()
        .atMost(Duration.of(customTimeout, SECONDS))
        .pollDelay(0L, TimeUnit.SECONDS)
        .pollInSameThread()
        .pollInterval(Duration.of(customPollInterval, SECONDS))
        .until(request::get, Optional::isPresent)
        .orElseThrow(
            () -> new RequestedRessourceNotAvailable(
                format("Asked for %s, but could not be found", resourceType)));
  }

  public static void addHostToTigerProxy(String url) {
    if (knownUrls.contains(url)) {
      return;
    }
    String host = getHost(url);
    TigerProxy proxy = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail();
    proxy.addAlternativeName(host);
    knownUrls.add(url);
  }

  @SneakyThrows
  public static String getHost(final String finalUrl) {
    Optional<TigerRoute> route = TigerDirector.getTigerTestEnvMgr().getLocalTigerProxyOrFail()
        .getRoutes()
        .stream()
        .filter(r -> r.getFrom().toLowerCase().endsWith(finalUrl.toLowerCase()))
        .findFirst();
    String domain = new URI(route.isPresent() ? route.get().getTo() : finalUrl).getHost();
    return domain.startsWith("www.") ? domain.substring(4) : domain;
  }
}