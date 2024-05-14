<img align="right" width="200" height="37" src="doc/images/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/>

# Release notes

## Link to TI-Messenger-testsuite [`docu`](https://github.com/gematik/TI-Messenger-Testsuite/tree/main/doc/userguide/Testsuite.adoc#docu)

- - -

## Release 1.1.0

- Adds new test step for healthcare service search by name
- New folder structure in src/test/resources/templates/FeatureFiles
- New feature files for OrgOnly tests in src/test/resources/templates/FeatureFiles/TI-M_11X/ZusatztestsOrgOnly
  Note: For the purpose and usage please read chapter 9.3 in testsuite.adoc
- New documentation file for TI-Messenger (ePA) (Testsuite_ePA.adoc)

## Release 1.0.1

### Changes

- Removes null checks for required properties received from test driver
- Updated Polarion integration
- Updated TestDriverApi to Release-1.1.1-8

## Release 0.10.10

### Changes

- Introduced negative questions and fails early for unexpected results

## Release 0.10.9

### Bugs

- Corrected TF 8.14, 8.15, 10.14, 10.15
- Polarion upload path corrected

## Release 0.10.8

### Bugs

- Search for endpoint in test case 03.03 is now done on updated healthcareservice

## Release 0.10.7

### Bugs

- Added a check whether a user is an org admin so it does not require a mxid
- sends an error message if test 11.01 fails

### Features

- sends a more helpful error message for the leave room task

## Release 0.10.6

### Bugs

- Get status message 403 instead of 401 when a person may not be invited to a room
- Check practitioner name instead of endpoint name, when searching by name in VZD

### Features

- Rename @NoParallel to @Ctl:NoParallel
- Change step @Und("{string} reserviert ein Client und meldet sich mit den Daten von {string} an der Schnittstelle
  {word} an")
- New directory for saved mvn properties

## Release 0.10.5

### Bugs

- Adjusted the AllowDoubleLineup Filter (set to ture) in Feature File 14 for TCs 14.02, 14.03, 14.04
- Regression for parsing FhirResources fixed. The custom mapper is used and therefor won't break if the send data
  contains more fields than defined in the TestDriverApi.

## Release 0.10.4

### Bugs

- Added https:// for homeserver url in the request to get the server version, if neither http nor https is given in the
  test-driver info.
- Error for parsing FhirSearchResultDTO fixed. Now the custom mapper is used and therefor don't break if the send data
  contains unexpected fields.

## Release 0.10.3

### Bugs

- Membership-state-check search for roomId and not for roomName. This caused problems because the roomName in direct
  chats differ and are not deterministic.

### Features

- Adds a teardown mechanism for contacts, deletes all registered contacts after the tests.

## Release 0.10.2

### Bugs

- Fixed two instances, where the mxid was not yet written in its uri scheme to the FHIR directory.

## Release 0.10.1

## Features

- New Tag @Ctl:BasicTest - Verification of all functions of the Testsuite/ -steps
- Testcase 00.05 request /.well-known/matrix/server endpoint to get homeserver and request /_matrix/client/versions on
  that. If not found, version request still goes to the home-server url from test-driver info

### Changes

- Changed naming of new Testcase 00.07 into 00.05

### Bugs

- It's no longer expected for an invited member, to get a full member list in the getRooms request for rooms they are
  invited to. Instead, it is sufficient when only the invited member themself is returned as member of the room they are
  invited to.

## Release 0.10.0

## Features

- Updated to new tiger version 2.3.2
- Fits to TestdriverApi 0.9.4 -> Add room version and check rooms for valid room version in the range 1 to 10
- Activates new Testcases 10.20 and 10.21 - User verlässt den Chat-Raum
- The clean-up before each test will be set as two times the property '
  timeout'  [`read more`](doc/userguide/GettingStarted.adoc)
- All glue steps now have the German and a translated English version
- The mxid to be stored in the FHIR VZD is only accepted and expected in its url form (i.e. "matrix:u/name:homeserver"),
  support for the matrix format was removed.
- Removed TFs 00.2, 00.5 & 04.2 - before all in status WIP
- New Testcase 00.07 Abfrage der Matrix-Server-Version. Request Matrix-Homeserver-Api directly
  at `/_matrix/client/versions`

### Changes

- Search for free device now just wait double the time till abort testcase

## Release 0.9.8

### Features

- Create room request: Access Lvl set to private
- Check room states in different stages. Hard fail can be switched off. [`read more`](doc/userguide/GettingStarted.adoc)
- New Testcases 10.20 and 10.21 - User verlässt den Chat-Raum
- Update Combinations-Plugin for better view of used groups in serenity report
- Reworked claim parallel

### Bugs

- Matrix url changed to correct schema like defined at https://spec.matrix.org/v1.9/appendices/#matrix-uri-scheme
- Clean step runs with correct client cert (Use tiger proxy)

## Release 0.9.7

### Features

- Testsuite checks if the address in endpoint from VZD is in url format. :warning: For this version the testsuite will
  handle both (url and matrix format). For upcoming versions this will be removed
- Adjusting Testsuite.adoc for a new Testcase
- TestCase 03.02 adjusted
- New Test 03.05 Healthcare-Service durch Org-Admin anlegen und Endpointname aktualisieren
- Created endpoint get a random name
- Fits to TestdriverApi 0.9.3 -> New api to clean system implemented. The testsuite triggers the clean-endpoint for each
  involved home-server on org-admin-api before each test-execution. Continues test as soon as all apis have sent any
  response. (Could also respond with error 404, so it is not needed to implement)
- Update Cucumber-Test-Parameter-Plugin for better error-messages and counting

## Release 0.9.6

### Features

- Tiger version update to 2.2.0
- Cutest-Combination-Plugin version update to 2.9.13 -> additional report file for all examples that should be executed
- Used testsuite-version is now saved in report directory
- Improved error messages for not parsable responses, especially errors. Last response and expected value are now shown
  also in serenity-report and do now throw an assertion exception
- Testsuite sends several cleanup requests
  for `healthcare services, unclaim device, logouts, leave room and forget room` if no 2XX response delivered. This will
  be tried for 20 seconds. After this time the testsuite continues
- HttpTimout for serentiy set to 5 minutes. Can be configured by -DhttpTimeout=\<seconds>
- If a claim device fails, the testsuite will retry to claim up to 3 times (configurable via environment
  variable <maxRetryClaimRequest> [`read more (Parameter die das Verhalten der Testsuite anpassen)`](doc/userguide/GettingStarted.adoc)

### Bugfixes

- Parsing exception for some FHIR-resources do not throw an error anymore when additionally properties delivered
- "TestcaseId is already used" - Bug is now fixed
- Failed tear downs do not throw exception and each actor will execute his teardowns

## Release 0.9.5

### Features

- HealthcareServiceNames, RoomNames and Messages are generate randomly with timestamps
- User search for complete HealthcareServiceName. This reduces the search result from FHIR-VZD
- WIP (future deleted) TC 00.02, 00.05 & 04.02
- Update cucumber-test-combinations-maven-plugin to version 2.9.12 and take advantage of the SoftFilterFeature

### Bugfixes

- Fix for not deleting testcase ID after fail login

## Release 0.9.4

### Features

- Better description for the use of the 'prefIOP' Tag in testsuite.adoc (chapter 6.3)

### Bugfixes

- Feature 3.4 -> do not cause error if _get endpoints_ of HealthcareService dos not include HealthcareService resource
- Feature 7.1, 7.2, 9.1, 9.2 -> does not look for one single address anymore. No error if one displayname comes up twice

## Release 0.9.3

### Features

- Status for test cases in Feature File 06 is now 'implemented'
- Make use of the new `breakOnFailedRequest` and `breakOnContextError` parameters of
  the [test combination plugin](https://github.com/gematik/cucumber-test-combinations-maven-plugin) -> now it's possible
  to keep the build running if an API responds with unexpected context or does not respond at all
- Toggle implemented to clean old rooms of an account. Can be activated via environment variable 'clearRooms'

### Bugfixes

- Changed the numbering in all Feature File for monadic numbers. Now they have a leading zero.
- Fix rename error for skip-saving-reports profile
- Edit and create endpoints add necessary meta-data/tag and delete "lastUpdated" (set by VZD)
- Certificate error resolved when use multiple https apis directly without routing via tiger proxy
- Szenario 03.03: Comparison between empty list and null does not fail anymore

## Release 0.9.2

### Features

- Feature File 14 does check for TIM custom event and TIM custom roomType in create event
- Check expression of goal `check` have been edited. For apis that provide an orgAdmin and client simultaneously the
  amount of needed devices will be checked to be greater than four.
- Names of healthcare-services now get generated with timestamp to be unique and shrink
- TestCases in Feature File 13 adjusted. New steps for export/import of session keys
- Fits to TestdriverApi 0.9.2 -> CodeableConcept and telecom added

### Bugfixes

- Join room will be performed multiple times till success or set timeout. This is needed to avoid false negative because
  of sync issues between homeservers
- Checks belonging the membership status of room members will be performed multiple times, till success or set timeout.
  This is needed to avoid false negative because of sync issues between homeservers
- Error in GlueUtils, could not find File, resolved
- An empty searchResult does not throw a NPE anymore
- Feature File 03 - .json files edit to fit to FHIR-datastructures. OrganizationId will be requested and replaced in
  request (body ca be sent directly to FhirVZD)

## Release 0.9.0

### Features

- Changed Status of Test Cases 6.1, 6.2 & 14.2 - 14.4
- improved look up in VZD if HCS is deleted, with customizable poll-intervals
- Fits to TestdriverApi 0.9.0 -> implements the FHIR-datastructures

### Bugfixes

- Search for healthcare- services with name is fixed and returns correct result
- Enter room with multiple participants do not check every invited member anymore and uses roomId

## Release 0.7.0

### Features

- improvements for login-counter and RegServiceToken-counter
- Deleting Test Case 3.05 incl adjusting Testsuite.adoc
- Adjusting json "03_AenderungEinesHealthcareService.json" for Test Case 3.03
- Corrected Test Cases 9.16, 9.17 & 11.3
- unWIP Test Cases 10.10 & 10.11
- Correction for Raw Data counting - OrgAdmin
- implements testdriver api 0.4.11
- improved checks for response codes for early fail
- New Feature File 6 (two testcases still in Work) incl adjustments in Testsuite.adoc
- Change of ML-123893 into ML-137902 in all Feature Files
- New Feature File 14 (RoomStateTests) incl adjustments in Testsuite.adoc
- Renamed Feature file 14 in 15 & 15 in 16

### Bugfixes

## Release 0.6.1

### Features

- more detailed counters for invite and event-system
- http is no longer required for the values in combine-items.json
- new RawDataCounter for RegServiceToken and ID updates
- RegServiceToken-counter active
- Rework Testsuit.adoc
- New Chapter 10 & 11 in Testsuite.adoc (Reports & Error Logs)
- Delete disabled TestCases in Feature Files 3, 7, 8, 9, 10

### Bugfixes

- Cleanup of mxId's fixed for tests with logout
- Filter for negative search recently deleted Healthcare-Service fixed
- New Tiger-Version used -> no more issues with / in the end of URL

## Release 0.5.3

### Features

- Response of upload media just have to be successful

### Bugfixes

- Commandline bug is fixed and testcases with tags can run without errors
- Messagetype changed `m.file` for sending media

## Release 0.5.0

### Features

- Documentation of Pooling in Testsuite.adoc
- Documentation of version filter in Testsuite.adoc
- TestCases 12.1, 12.2, 12.3 adjusted to 3 different participants/homeservers
- New Feature File 14 for group chat (IOP) with 5 participants (incl. documentation in Testsuite.adoc)
- New Feature File 15 for group chat (IOP) with 3 participants. Tests are excluded from Feature File 12
- Flag `saveConnections` implemented. Set to true and testsuite will unclaim devices only at the end of run
- New version `2.9.1` of parameter plugin allows usage
  of [pooling](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#pooling)
  and [check](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#check-goal)
- Filter Adjustments in all Testcases

### Bugfixes

- Testcases 13.7 & 13.8 can now create less error because of old data
- Wrong counted raw data fixed for testcases with multiple actors

## Release 0.4.4

### Features

- Transaction-Id-header is send in every request

## Release 0.4.3

### Features

- This release makes use
  of [cucumber-test-combinations-maven-plugin](https://github.com/gematik/cucumber-test-combinations-maven-plugin) v
  2.6.0 features
    - Control runs for specific version (see
      specifics [here](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#project-filters))
- Remove/Comment Test Cases for editing messages in Feature Files 8 & 10
- To every request a header is attached. This header identifies the current testcase and can be used for debugging
  purpose
    - The depending id can also be found in the serenity report
- The certificate of the tiger proxy get updated automatically. It is no longer necessary to name every host
  under `alterenativeNames` in `tiger.yml`
- TestCase 3.3 splitted into two TestCases (new 3.3 & 3.4)
- New Testcases for pipe data acquisition Group Chat inside and outside a homeserver (TF 12.09, 12.10)

## Release 0.4.1

This release matches testdriver api in version 0.4.1

### Features

- New Testcases for user search inside a homeserver (TF 7.22 - 7.25 & 9.22)
- New Testcases for sending events inside a homeserver (TF 8.22, 8.23)
- Change in Feature File 3: update TF 3.3 (no location update) & 3.5 is deleted
- Retry loops are configurable in negative test steps
- New api endpoints from testdriver api in testsuite implemented

## Release 0.3.2

### Features

- New rawdata counter for invitations to direct chats
- Further information for ordering test cards (testsuite.adoc)
- Search and invite for Org Users in Chat/Room changed into combined step
- New rawdata counter for invitations to direct chats
- Docu `GettingStarted` improved

### Bugfixes

- All requests done by awaitility now appear in serenity report

## Release 0.3.1

### Features

- Not successful requests got send several times. This contributes to the possibly slow network
  synchronization
- Additionally login test for org-admin
- Rawdata are collected and exported as yml, json, csv and copied to report

### Bugfixes

- Profile disable prepare items does not use wrong combine_items.json anymore
- Jacoco update to build with java 18

## Release 0.3.0

This is the first prerelease release and matches the testdriver version 0.3.0.