<img align="right" width="200" height="37" src="doc/images/Gematik_Logo_Flag.png" alt="gematik logo"/> <br/>

# Release notes

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