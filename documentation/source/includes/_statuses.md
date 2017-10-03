# Printer statuses

```java

String printerId = "" // PrinterId obtained previously in getPrintersSettings call

printerManager.status(printerId).subscribe(new Consumer<PrinterStatus>() {
    @Override
    public void accept(@NonNull PrinterStatus printerStatus) throws Exception {
        // do something with the status if required
        String status = printerStatus.getStatus();
    }
});

```

Your application can optionally subscribe to printer events. These events can be used to notify the user that something has happened on the printer if required. It is not mandatory that the statuses are observed but they may be useful for the user in diagnosing print issues.

Status Code | Meaning
---------- | -------
printerReady | The printer driver is connected to this printer and is ready to print
printerOffline | The printer driver has just disconnected from this printer and is no-longer ready to print
coverOpened | The cover has been opened on the printer
outOfPaper | The printer has just run out of paper
lowBattery | The printers battery is low
mechanicalError | The printer has mechanically failed in some way
unrecoverableError | The printer driver has failed to connect to the printer and cannot recover
printerOverheated | The printer has overheated
wrongPaper | Wrong paper detected in the printer
drawerHigh | If the printer supports a cashdrawer this indicated the drawer electrical output has been set to high
drawerLow | If the printer supports a cashdrawer this indicated the drawer electrical output has been set to low
timeout | The printer driver timed out while attempting to print
