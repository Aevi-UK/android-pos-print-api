/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aevi.print.model;

/**
 * The messages returned by print job result see {@link PrintJob#getDiagnosticMessage()} or by printer status see {@link PrinterStatus}.
 */
public interface PrinterMessages {

    /** No default printer as been selected by the merchant. */
    String ERROR_NO_DEFAULT = "noDefault";

    /** The printing failed. See the diagnostic message for more details on the cause of the failure.   */
    String ERROR_PRINT_FAILED = "printFailed";

    /** The specified printer has not been found. */
    String ERROR_PRINTER_NOT_FOUND = "printerNotFound";

    /** The printer has not been installed or the service is not available. */
    String ERROR_SERVICE_NOT_AVAILABLE = "serviceNotAvailable";

    /** Failed to retrieve the printer settings. */
    String ERROR_GET_SETTINGS_FAILED = "getSettingsFailed";

    /** Failed to retrieve the status from the printer. */
    String ERROR_GET_STATUS_FAILED = "getStatusFailed";

    /** The printer is busy. Please wait until the printing completes before starting a second print.*/
    String ERROR_BUSY = "busy";

    /** The printer has no errors and is ready to print. */
    String PRINTER_READY = "printerReady";

    /** The printer is off line. */
    String PRINTER_OFFLINE = "printerOffline";

    /** The printer cover is open. */
    String COVER_OPENED = "coverOpened";

    /** The printer is out of paper. */
    String OUT_OF_PAPER = "outOfPaper";

    /** The printer battery is low. */
    String LOW_BATTERY = "lowBattery";

    /** There is a mechanical error with printer (e.g. a paper jam). */
    String MECHANICAL_ERROR = "mechanicalError";

    /** There has been an unrecoverable printer error. */
    String UNRECOVERABLE_ERROR = "unrecoverableError";

    /** The printer has over heated. */
    String PRINTER_OVERHEATED = "printerOverheated";

    /** The wrong size of paper has been installed in the printer. */
    String WRONG_PAPER = "wrongPaper";

    /** The cash drawer status pin is high, which may indicate that the cash drawer is closed. */
    String DRAWER_SET_TO_HIGH = "drawerHigh";

    /** The cash drawer status pin is low, which may indicate that the drawer is either open or the drawer is not plugged in. */
    String DRAWER_SET_TO_LOW = "drawerLow";

    /** The action command to open the cash drawer has completed successfully. */
    String DRAWER_OPENED = "drawerOpened";

    /** The action command to open the cash drawer failed because it is either not connected or the printer is off line */
    String DRAWER_NOT_AVAILABLE = "drawerNotAvailable";

    /** An unknown printer status has been received. */
    String UNKNOWN = "unknown";

    /** The connection to printer has timed out. */
    String TIMEOUT = "timeout";

    /** Warning the paper roll is near the end and needs replacing. */
    String WARN_PAPER_NEAR_END = "paperNearEnd";

    /** Warning the printers battery is low and needs recharging. */
    String WARN_LOW_BATTERY = "warnLowBattery";

    /** Send an action to the printer to cut the paper (see {@link com.aevi.print.PrinterManager#sendAction(String, String)} ). */
    String ACTION_CUT_PAPER = "cutPaper";

    /** Send an action to the printer to open the cash drawer (see {@link com.aevi.print.PrinterManager#sendAction(String, String)} ). */
    String ACTION_OPEN_CASH_DRAWER = "openCashDrawer";
}
