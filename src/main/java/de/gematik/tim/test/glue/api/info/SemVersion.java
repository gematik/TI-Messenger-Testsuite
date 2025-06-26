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

package de.gematik.tim.test.glue.api.info;

import static java.lang.Integer.compare;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
@EqualsAndHashCode
public class SemVersion implements Comparable<SemVersion> {

  private final int major;
  private String versionString;
  private int minor;
  private int patch;

  public SemVersion(String version) {
    if (StringUtils.isBlank(version)) {
      throw new IllegalArgumentException("Version must not be null or empty");
    }
    versionString = version;
    version = version.replaceAll("[^0-9.]", "");
    String[] splitted = version.split("\\.");
    if (splitted.length < 1) {
      throw new IllegalArgumentException("No number found");
    }
    major = Integer.parseInt(splitted[0]);
    if (splitted.length > 1) {
      minor = Integer.parseInt(splitted[1]);
    }
    if (splitted.length > 2) {
      patch = Integer.parseInt(splitted[2]);
    }
  }

  @Override
  public int compareTo(@NotNull SemVersion version) {
    if (major == version.major) {
      return minor == version.minor ? compare(patch, version.patch) : compare(minor, version.minor);
    }
    return compare(major, version.major);
  }

  @Override
  public String toString() {
    return versionString;
  }
}
