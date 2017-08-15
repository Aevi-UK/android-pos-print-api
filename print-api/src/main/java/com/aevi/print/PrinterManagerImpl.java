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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.aevi.android.rxmessenger.ObservableMessengerClient;
import com.aevi.android.rxmessenger.SendableId;
import com.aevi.print.model.PrintAction;
import com.aevi.print.model.PrintJob;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.PrinterSettingsList;
import com.aevi.print.model.PrinterStatus;
import com.aevi.print.model.PrinterStatusRequest;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

class PrinterManagerImpl implements PrinterManager {

    private static final String PRINT_SERVICE_PACKAGE = "com.aevi.print.service";
    private static final String PRINT_MESSENGER_SERVICE_CLASS = "com.aevi.print.service.PrinterMessagingService";
    private static final String PRINT_SETTINGS_SERVICE_CLASS = "com.aevi.print.service.PrinterSettingsService";
    private static final String PRINTER_STATUS_SERVICE_CLASS = "com.aevi.print.service.PrinterStatusService";
    private static final String PRINTER_ACTION_SERVICE_CLASS = "com.aevi.print.service.PrinterActionService";

    private final ObservableMessengerClient<PrintPayload, PrintJob> printingMessenger;
    private final ObservableMessengerClient<PrintAction, SendableId> printerActionMessenger;
    private final ObservableMessengerClient<PrinterSettingsRequest, PrinterSettingsList> printSettingsMessenger;
    private final ObservableMessengerClient<PrinterStatusRequest, PrinterStatus> printerStatusMessenger;

    private final Context context;

    PrinterManagerImpl(Context context) {
        this.context = context;
        printingMessenger = new ObservableMessengerClient<>(context, PrintJob.class);
        printSettingsMessenger = new ObservableMessengerClient<>(context, PrinterSettingsList.class);
        printerStatusMessenger = new ObservableMessengerClient<>(context, PrinterStatus.class);
        printerActionMessenger = new ObservableMessengerClient<>(context, SendableId.class);
    }

    @Override
    public boolean isPrinterServiceAvailable() {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentServices(getIntent(PRINT_MESSENGER_SERVICE_CLASS), PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() == 1 && resolveInfo.get(0).serviceInfo != null;
    }

    @Override
    public Observable<PrintJob> print(final PrintPayload printPayload) {
        Log.d(TAG, "About to send: " + printPayload.toJson());
        return printingMessenger.createObservableForServiceIntent(getIntent(PRINT_MESSENGER_SERVICE_CLASS), printPayload);
    }

    @Override
    public void sendAction(String printerId, String action) {
        PrintAction printAction = new PrintAction(printerId, action);
        printerActionMessenger.createObservableForServiceIntent(getIntent(PRINTER_ACTION_SERVICE_CLASS), printAction).take(1).subscribe();
    }

    @Override
    public Observable<PrinterStatus> status(String printerName) {
        PrinterStatusRequest statusRequest = new PrinterStatusRequest(printerName);
        return printerStatusMessenger.createObservableForServiceIntent(getIntent(PRINTER_STATUS_SERVICE_CLASS), statusRequest);
    }

    @Override
    public Single<PrinterSettings> getDefaultPrinterSettings() {
        Log.d(TAG, "Getting default printer settings");
        return getSettingsServiceIntent(PrinterSettingsRequest.createDefaultRequest()).map(new Function<PrinterSettingsList, PrinterSettings>() {
            @Override
            public PrinterSettings apply(@NonNull PrinterSettingsList printerSettingsList) throws Exception {
                PrinterSettings[] printerSettingses = printerSettingsList.getPrinterSettings();
                if (printerSettingses != null && printerSettingses.length >= 1) {
                    return printerSettingses[0];
                }

                // TODO should this throw custom exception here?
                throw new RuntimeException("No default printer set");
            }
        }).take(1).observeOn(Schedulers.io()).singleOrError();
    }

    @Override
    public Observable<PrinterSettingsList> getPrintersSettings() {
        return getSettingsServiceIntent(PrinterSettingsRequest.createAllRequest());
    }

    private Observable<PrinterSettingsList> getSettingsServiceIntent(PrinterSettingsRequest printerRequest) {
        return printSettingsMessenger.createObservableForServiceIntent(getIntent(PRINT_SETTINGS_SERVICE_CLASS), printerRequest);
    }

    private Intent getIntent(String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PRINT_SERVICE_PACKAGE, className));
        return intent;
    }
}
