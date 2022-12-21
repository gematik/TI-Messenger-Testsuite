# 

![logo](doc/images/Gematik_Logo_Flag.png)

# TI-Messenger-Testsuite

## Über das Projekt

Mit der TI-Messenger-Testsuite werden Instanzen von TI-Messengern über die [Testtreiberschnittstelle](https://github.com/gematik/api-ti-messenger/blob/main/src/openapi/TiMessengerTestTreiber.yaml) gegeneinander getestet.

Der Schwerpunkt der Testsuite liegt auf Interoperabilitätstests verschiedener TI-Messenger-Instanzen. Eine Beschreibung des Testkonzepts befindet sich
[hier](doc/userguide/Testsuite.adoc)

Die Testsuite nutzt [Maven](https://maven.apache.org/) und [Tiger](https://github.com/gematik/app-Tiger) für die Ausführung der [Serenity-BDD](https://serenity-bdd.github.io/)-Tests.

Für die Ausführung der Tests werden aus einer Liste von [Testinstanzen](src/test/resources/combine_items.json) mittels eines [Maven-Plugins](https://github.com/gematik/cucumber-test-combinations-maven-plugin) Testkombinationen erstellt und in den [Cucumber-Templates](src/test/resources/templates) eingetragen.

### Releaseinformationen

In [ReleaseNotes.md](ReleaseNotes.md) befinden sich alle Informationen zu unseren (neuesten) Releases.

## Verwendung

Die Testsuite führt die Tests als Maven-Integrationstests aus. Nach der initialen Konfiguration können die Tests mit `mvn verify` ausgeführt werden. Eine Beschreibung der benötigten initialen Konfiguration befindet sich [hier](doc/userguide/GettingStarted.adoc). Erweiterte Konfigurationen sind im [Entwicklerguide](doc/userguide/DevGuide.adoc) zu finden.

Nach der Testausführung befindet sich die Auswertung unter [target/site/serenity/index.html](target/site/serenity/index.html)

## Mitwirken

Unsere Regeln für das Mitwirken an unserem Projekt befinden sich [hier](CONTRIBUTING.md).

## Lizenz

Copyright 2022 gematik GmbH

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specif ic language governing permissions and limitations under the License.
