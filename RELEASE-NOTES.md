## Release Notes for the AEVI Android POS printer api

## Version 1.1.5

* Update underlying rx-messenger library to v5.0.2.
* Upgraded the build tools.
* Reverted to Java 7 in the Print API.

## Version 1.1.4

* Update internal JSON classes to use general purpose AEVI json-utils library in favour of local classes. 

## Version 1.1.3

* Fixed an issue that prevented two print-api methods being used at the same time. It is highly 
  recommended you upgrade to this version as soon as possible to avoid this bug.
* Improved null checking on the API parameters. 

## Version 1.1.2

* Added `PrinterSettings.getDisplayName` for the name that gets shown to the user.
* Update underlying rx-messenger library to v3.0.1

## Version 1.1.1

* Updated the PrintingExample to include open cash drawer code
* Added `DRAWER_OPENED` and `DRAWER_NOT_AVAILABLE` to `PrinterMessages`

## Version 1.1.0

* Update underlying rx-messenger library to v2.0.0
* No API changes

## Version 1.1.0

* Update underlying rx-messenger library to v2.0.0
* No API changes

## Version 1.0.2

* Fixed Printing Example paper width issue.
* Clear the Printer status box on the Printing Example activity resume.
* Removed double encode/decode of image before returning preview.

## Version 1.0.1

* Minor update to publish aar and jar binaries to bintray

## Version 1.0.0

* Initial first release

