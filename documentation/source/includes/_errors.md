# Errors

Errors returned by printing service are thrown as `MessageException` objects.

The following common errors are defined as Strings in the `PrinterMessages` interface but printer drivers are free to define there own specific errors if required.

Error Code | Meaning
---------- | -------
printFailed | Printer failed to print unexpectedily
printerNotFound | Printer with the given printerId is not found
serviceNotAvailable | Printer service not available or not installed
getSettingsFailed | Failed while getting printer settings objects
getStatusFailed | Failed while getting printer status events
coverOpen | Print job failed because the cover is open
outOfPaper | Print job failed because the device is out of paper
busy | Print job failed because the printer can only handle one print job and it is in progress
