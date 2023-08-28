<img align="right" width="200" height="37" src="doc/images/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/>

# Release notes

## Link to TI-Messenger-testsuite [`docu`](https://github.com/gematik/TI-Messenger-Testsuite/tree/main/doc/userguide/Testsuite.adoc#docu)

- - -
## Release 0.9.2

### Feature

- Feature File 14 does check for TIM custom event and TIM custom roomType in create event
- Check expression of goal `check` have been edited. For apis that provide an orgAdmin and client simultaneously the amount of needed devices will be checked to be greater than four.  
- Names of healthcare-services now get generated with timestamp to be unique and shrink 
- TestCases in Feature File 13 adjusted. New steps for export/import of session keys
- Fits to TestdriverApi 0.9.2 -> CodeableConcept and telecom added

### Bugfix

- Join room will be performed multiple times till success or set timeout. This is needed to avoid false negative because of sync issues between homeservers
- Checks belonging the membership status of room members will be performed multiple times, till success or set timeout. This is needed to avoid false negative because of sync issues between homeservers
- Error in GlueUtils, could not find File, resolved
- An empty searchResult does not throw a NPE anymore
- Feature File 03 - .json files edit to fit to FHIR-datastructures. OrganizationId will be requested and replaced in request (body ca be sent directly to FhirVZD)

## Release 0.9.0

### Feature

- Changed Status of Test Cases 6.1, 6.2 & 14.2 - 14.4
- improved look up in VZD if HCS is deleted, with customizable poll-intervals
- Fits to TestdriverApi 0.9.0 -> implements the FHIR-datastructures

### Bugfix

- Search for healthcare- services with name is fixed and returns correct result
- Enter room with multiple participants do not check every invited member anymore and uses roomId

## Release 0.7.0

### Feature

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


### Bugfix


## Release 0.6.1

### Feature

- more detailed counters for invite and event-system
- http is no longer required for the values in combine-items.json
- new RawDataCounter for RegServiceToken and ID updates
- RegServiceToken-counter active
- Rework Testsuit.adoc
- New Chapter 10 & 11 in Testsuite.adoc (Reports & Error Logs)
- Delete disabled TestCases in Feature Files 3, 7, 8, 9, 10

### Bugfix

- Cleanup of mxId's fixed for tests with logout
- Filter for negative search recently deleted Healthcare-Service fixed
- New Tiger-Version used -> no more issues with / in the end of URL

## Release 0.5.3

### Features

- Response of upload media just have to be successful 

### Bugfix

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
- New version `2.9.1` of parameter plugin allows usage of [pooling](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#pooling) and [check](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#check-goal)
- Filter Adjustments in all Testcases


### Bugfix

- Testcases 13.7 & 13.8 can now create less error because of old data
- Wrong counted raw data fixed for testcases with multiple actors

## Release 0.4.4

### Features

- Transaction-Id-header is send in every request

## Release 0.4.3

### Features

 - This release makes use of [cucumber-test-combinations-maven-plugin](https://github.com/gematik/cucumber-test-combinations-maven-plugin) v 2.6.0 features 
   - Control runs for specific version (see specifics [here](https://github.com/gematik/cucumber-test-combinations-maven-plugin/blob/main/doc/userguide/GettingStarted.adoc#project-filters))
 - Remove/Comment Test Cases for editing messages in Feature Files 8 & 10
 - To every request a header is attached. This header identifies the current testcase and can be used for debugging purpose
    - The depending id can also be found in the serenity report
 - The certificate of the tiger proxy get updated automatically. It is no longer necessary to name every host under `alterenativeNames` in `tiger.yml` 
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

### Bugs

- All requests done by awaitility now appear in serenity report

## Release 0.3.1

### Features

- Not successful requests got send several times. This contributes to the possibly slow network
  synchronization
- Additionally login test for org-admin
- Rawdata are collected and exported as yml, json, csv and copied to report


### Bugs

- Profile disable prepare items does not use wrong combine_items.json anymore
- Jacoco update to build with java 18

## Release 0.3.0

This is the first prerelease release and matches the testdriver version 0.3.0.