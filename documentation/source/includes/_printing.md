# Printing

## Building a request

```java

if(printerManager.isPrinterServiceAvailable()) {

  PrintPayload payload = new PrintPayload();

  // fill out payload here. Described below

  printerManager.print(payload)
          .subscribe(new Consumer<PrintJob>() {
              @Override
              public void accept(@NonNull PrintJob printResult) throws Exception {
                  // Do something with results here
                  Log.d(TAG, "Got status from printer: " + printResult.getPrintJobState());
              }
          }, new Consumer<Throwable>() {
              @Override
              public void accept(@NonNull Throwable throwable) throws Exception {
                  Log.e(TAG, "Error while printing", throwable);
              }
          });
}

```

All printing requests require a `PrintPayload` object to be built, which is described below. Once this object is constructed it is sent to the `PrintManager` using the `print()` method. This method must be subscribed too before the payload is sent. You should also check that the printing service is installed and available before calling any print methods as can be seen opposite using the `isPrinterServiceAvailable()` method.

The subscriber should also ensure they are subscribed to any errors that may be thrown during printing. Exceptions are only thrown if an unexpected error is returned by the AEVI print service. The stream of `PrintJob` objects returned by the print service will indicate success or failure of the print itself.

By default print jobs will be sent to the default printer. However, prints can also be sent to a specific printer. This is accomplished by setting a `printerId` in the `PrintPayload` using the `setPrinterId()` method. The printerIds for available printers can be obtained via the `PrintManager.getPrintersSettings()` method. Usage of this method is shown below.

## Building print payloads

```java

PrintPayload printPayload = new PrintPayload();
printPayload.append("Align Left");
printPayload.append("Align Right").align(Alignment.RIGHT);
printPayload.append("Align Center").align(Alignment.CENTER);
printPayload.append("Emphasized").fontStyle(FontStyle.EMPHASIZED);
printPayload.append("Inverted").fontStyle(FontStyle.INVERTED);
printPayload.appendEmptyLine();
printPayload.append("InvertedEmphasized").fontStyle(FontStyle.INVERTED_EMPHASIZED);
printPayload.append("Single Underlined").underline(Underline.SINGLE);
printPayload.append("Double Underlined").underline(Underline.DOUBLE);
printPayload.appendEmptyLine();
// append a basket 20 columns wide
printPayload.appendLeftRight(20, "Name", "Price");
printPayload.appendLeftRight(20, "Doughnut", " $1.02");
printPayload.appendLeftRight(20, "Beer", "$6.00");
printPayload.appendEmptyLine();

```

`PrintPayload` objects can be used to configure most textual print layouts you would expect a receipt printer to handle.
This includes standard styles such as EMPHASIZED, INVERTED etc and alignment e.g. LEFT, RIGHT. See the javadocs for a full list of styles.

### Adding images

```java
Bitmap logo = BitmapFactory.decodeResource(context.getResources(),
                          R.drawable.yourlogo, bitmapFactoryOptions);
printPayload.appendEmptyLine();
printPayload.append(logo).align(Alignment.CENTER);
```

Images can also be added to the `PrintPayload` as shown in the example. By default images added will be scaled to fit the width of the printer paper.
However, you can also choose that the image added is clipped to the width of the paper and not scaled by setting the `scaleToFit` parameter to false
when calling `PrintPayload.append(Bitmap bitmap, boolean scaleToFit)`.

> NOTE: the total size of the `PrintPayload` should not exceed 1MB so that it can be sent via Android Binder mechanisms. Images will be encoded as
WEBM format and compressed slightly when sent between the print service and the printer driver.

