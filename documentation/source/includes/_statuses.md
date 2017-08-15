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
connected | The printer driver has just connected to this printer
disconnected | The printer driver has just disconnected from this printer
coverOpened | The cover has been opened on the printer
coverClosed | The cover has been closed on the printer
outOfPaper | The printer has just run out of paper
lowBattery | The printers battery is low
