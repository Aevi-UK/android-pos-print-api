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
* paperwidth - The paper width supported by this printer in mm
* printableWidth - The maximum width this printer can print on the paper size given above in mm
* dotsPerMm - The number of dots this printer can support per mm (to convert to dpi multiply by 25.4)
* paperkind - The type of paper supported by this printer e.g. THERMAL, NORMAL
* codepages - The character codepages a printer will accept
* actions - The actions that can be sent to this printer to perform a task e.g. "cutPaper"
* options - A `Map` of String key value pairs that describe any printer specific options this printer exposes
* fonts - A list of fonts the printer supports
* languages - A list of language code supported by the printer. This allows text characters to be sent in different languages if the printer supports it.

The `PrinterSettings` object is a graph of data that contains information about the printer at a given point in time. This data may be updated by the printer driver at any time. In particular this data change could occur in response to a user settings change or perhaps the printer configuration is changed (e.g. different sized paper inserted). Therefore, the `PrinterSettings` object(s) should not be stored permanently or cached. Instead you should subscribe to the settings stream as shown in the example and ensure that if the data is updated you use the latest values.

## Printer fonts

```java

    PrinterFont[] fonts = printerSettings.getPrinterFonts();
    if(fonts != null && fonts.length > 0) {
        int cols = fonts[0].getNumColumns();
        // In this font you can now print `cols` columns of text per line
    }

```

A list of fonts available can (optionally) be returned along with the `PrinterSettings` object above. These fonts provide details of the various sizes of text a printer can print at. In particular the `PrinterFont` object contains the number of columns (per line) that can be printed. This is useful in determining table or columnar layouts as well as maximum number of characters per line. If this number of characters is exceeded then each printer driver may choose to truncate or wrap the text sent in the `PrintPayload`.

> NB: It is assumed that all printers supported will use ONLY monospace fonts.

### Using fonts

```java

     String tstStr = "Hello World!";
     PrinterFont font = fonts[0];
     printPayload.append(tstStr, font);

```

To use a font in your `PrintPayload` simply call the overloaded `append` method passing in your text and the font you wish to use. If a font is not passed into the `append` method then the printer default font will be used.