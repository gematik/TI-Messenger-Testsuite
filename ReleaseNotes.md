<img align="right" width="200" height="37" src="doc/images/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/>

# Release notes

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