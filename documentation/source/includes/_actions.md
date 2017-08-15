# Printer actions

```java

String action = "cutPaper"; // String value obtained from PrinterSettings
printerManager.sendAction(action);

```

Actions can be sent to a printer. This method is used to send any custom actions a printer can perform, common examples of action would be "cutPaper" and "openCashDrawer". Each printer is free to define its own list of available actions. These can be obtained by reading the actions from the `PrinterSettings` object. Different printer drivers may choose to implement there own set of action commands but default/common actions are defined in the `PrinterMessages` interface.
