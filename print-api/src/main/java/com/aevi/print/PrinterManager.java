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
package com.aevi.print;

import com.aevi.print.model.PrintJob;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.PrinterSettingsList;
import com.aevi.print.model.PrinterStatus;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface PrinterManager {

    /**
     * @return True if the printing service is installed and available
     */
    boolean isPrinterServiceAvailable();

    /**
     * Send a payload for printing
     *
     * @param printPayload The payload to print
     * @return An observable stream of {@link PrintJob} which indicates the status of the printout
     */
    Observable<PrintJob> print(PrintPayload printPayload);

    /**
     * Send an action to a printer
     *
     * @param printerId The id of the printer to send the action to
     * @param action    The action to perform (see {@link PrinterSettings#getCommands() for a list of commands the printer supports}
     */
    void sendAction(String printerId, String action);

    /**
     * A stream of {@link PrinterStatus} indicating the current state of the printer
     *
     * @param printerId The printerId to listen to
     * @return An observable stream of {@link PrinterStatus}
     */
    Observable<PrinterStatus> status(String printerId);

    /**
     * Returns the current settings for the default printer
     *
     * @return A Single which will emit the default printer settings if available
     */
    Single<PrinterSettings> getDefaultPrinterSettings();

    /**
     * Returns an observable stream which will emit settings for all the available printers
     *
     * @return An observable stream containing a list of {@link PrinterSettings} objects contained in a {@link PrinterSettingsList} object
     */
    Observable<PrinterSettingsList> getPrintersSettings();
}
