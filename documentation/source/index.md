---
title: AEVI Printing API Reference

language_tabs:
  - java

toc_footers:
  - <a href='https://community.aevi.com'>Join the AEVI community</a>

includes:
  - printing
  - settings
  - statuses
  - actions
  - drivers
  - errors

search: true
---

# Introduction

The following documents describe how to use the AEVI Printing API. This API allows developers to quickly and easily integrate printing into your Android application. The API allows you to print using any AEVI enabled device and selected printer drivers. Specifically this API is designed for use with receipt/line printer type devices.

The print API itself makes extensive use of reactive (Rx) based principles. Therefore in the case of the Java API it makes heavy use of the RxJava library. To read more about Rx principles and the RxJava library itself see [the documentation here](https://github.com/ReactiveX/RxJava). For the remainder of this documentation it is assumed that the reader is familiar with asynchronous and event-based programming using observable streams.

The javadocs can be downloaded from the GitHub release pages and also see the Printing Example that is part of this project.


For further information, please see the AEVI community portal [community portal](https://community.aevi.com/).

## JavaDocs

Please see [Printing API JavaDocs](javadoc/index.html).

# Getting started

The main entry point to the SDK is to first obtain an instance of the `PrintManager`. This object can then be used to send print jobs, actions and listen to printer events.

> To get an instance of the PrintManager within your application

```java

      PrinterManager printerManager = PrinterApi.getPrinterManager(this);

```

In order to bind to the printer service your application must add the permission `com.aevi.permission.NGS_PRINT_SERVICE` to your manifest file.

```xml
<uses-permission android:name="com.aevi.permission.NGS_PRINT_SERVICE"/>
```



