# Introduction

The following documents describe how to use the AEVI Printing API. This API allows developers to
quickly and easily integrate printing for receipt printers into your Android application. The API
allows you to print using any AEVI enabled device and selected printer drivers. Specifically this
API is designed for use with receipt/line printer type devices.

The print API itself makes extensive use of reactive (Rx) based principles. Therefore in the case
of the Java API it makes heavy use of the RxJava library. To read more about Rx principles and the
RxJava library itself see [the documentation here](https://github.com/ReactiveX/RxJava).
For the remainder of this documentation it is assumed that the reader is familiar with asynchronous
and event-based programming using observable streams.

Go register at our amazing developer portal here [developer portal](https://developer.aevi.com/).

# Prerequisites

This API is an entry point to the AEVI Printing Service. In order to use this API sucessfully on a
device the printing service application must be installed along with printer driver applications
that will handle the actual print process with the physical devices.

# Getting started

The main entry point to the SDK is to first obtain an instance of the `PrintManager`. This object
can then be used to send print jobs, actions and listen to printer events.

> To get an instance of the PrintManager within your application

```java

      PrinterManager printerManager = PrinterApi.getPrinterManager(this);

```

In order to bind to the printer service your application must also request the
permission `com.aevi.permission.NGS_PRINT_SERVICE`

```xml
<uses-permission android:name="com.aevi.permission.NGS_PRINT_SERVICE"/>
```
