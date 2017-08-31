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

public interface PrinterMessages {

    String ERROR_NO_DEFAULT = "noDefault";
    String ERROR_PRINT_FAILED = "printFailed";
    String ERROR_PRINTER_NOT_FOUND = "printerNotFound";
    String ERROR_SERVICE_NOT_AVAILABLE = "serviceNotAvailable";
    String ERROR_GET_SETTINGS_FAILED = "getSettingsFailed";
    String ERROR_GET_STATUS_FAILED = "getStatusFailed";
    String ERROR_BUSY = "busy";

    String PRINTER_READY = "printerReady";
    String PRINTER_OFFLINE = "printerOffline";
    String COVER_OPENED = "coverOpened";
    String OUT_OF_PAPER = "outOfPaper";
    String LOW_BATTERY = "lowBattery";
    String MECHANICAL_ERROR = "mechanicalError";
    String UNRECOVERABLE_ERROR = "unrecoverableError";
    String PRINTER_OVERHEATED = "printerOverheated";
    String WRONG_PAPER = "wrongPaper";
    String DRAWER_SET_TO_HIGH = "drawerHigh";
    String DRAWER_SET_TO_LOW = "drawerLow";
    String UNKNOWN = "unknown";
    String TIMEOUT = "timeout";

    String WARN_PAPER_NEAR_END = "paperNearEnd";
    String WARN_LOW_BATTERY = "warnLowBattery";

    String ACTION_CUT_PAPER = "cutPaper";
    String ACTION_OPEN_CASH_DRAWER = "openCashDrawer";
}
