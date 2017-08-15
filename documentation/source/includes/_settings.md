# Printer settings

```java
printerManager.getPrintersSettings()
                   .subscribe(new Consumer<PrinterSettings>() {
                       @Override
                       public void accept(@NonNull PrinterSettings printerSettings) throws Exception {
                          // store or cache printer settings here
                       }
                   });
```

The settings of each printer available to your device can be obtained using the `getPrintersSettings()` method. This method will return an observable stream of `PrinterSettings` objects. The `PrinterSettings` object contains various details about the aspects of the printer and the functionality it provides. Each `PrinterSettings` object also contains the unique value of the printerId that can be used to refer to a specific printer device.

The `PrinterSettings` object contains information about:
* paperwidth - The paper width supported by this printer in mm(??)
* resolution - The resolution this printer prints at
* paperkind - The type of paper supported by this printer e.g. THERMAL, NORMAL
* codepages - The character codepages a printer will accept
* actions - The actions that can be sent to this printer to perform a task e.g. "cutPaper"
* options - A `Map` of String key value pairs that describe any printer specific options this printer exposes
