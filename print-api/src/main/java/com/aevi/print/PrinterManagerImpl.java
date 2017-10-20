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
import com.aevi.print.model.PrintAction;
import com.aevi.print.model.PrintJob;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.PrinterSettingsList;
import com.aevi.print.model.PrinterStatus;

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

    private final ObservableMessengerClient printingMessenger;
    private final ObservableMessengerClient printerActionMessenger;
    private final ObservableMessengerClient printSettingsMessenger;
    private final ObservableMessengerClient printerStatusMessenger;

    private final Context context;

    PrinterManagerImpl(Context context) {
        this.context = context;
        printingMessenger = new ObservableMessengerClient(context);
        printSettingsMessenger = new ObservableMessengerClient(context);
        printerStatusMessenger = new ObservableMessengerClient(context);
        printerActionMessenger = new ObservableMessengerClient(context);
    }

    @Override
    public boolean isPrinterServiceAvailable() {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentServices(getIntent(PRINT_MESSENGER_SERVICE_CLASS), PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() == 1 && resolveInfo.get(0).serviceInfo != null;
    }

    @Override
    public Observable<PrintJob> print(final PrintPayload printPayload) {
        Log.d(TAG, "About to send: " + printPayload.toJson());
        return printingMessenger.createObservableForServiceIntent(getIntent(PRINT_MESSENGER_SERVICE_CLASS), printPayload.toJson())
                .map(new Function<String, PrintJob>() {
                    @Override
                    public PrintJob apply(String json) throws Exception {
                        return PrintJob.fromJson(json);
                    }
                });
    }

    @Override
    public void sendAction(String printerId, String action) {
        PrintAction printAction = new PrintAction(printerId, action);
        printerActionMessenger.createObservableForServiceIntent(getIntent(PRINTER_ACTION_SERVICE_CLASS), printAction.toJson()).take(1).subscribe();
    }

    @Override
    public Observable<PrinterStatus> status(String printerId) {
        return printerStatusMessenger.createObservableForServiceIntent(getIntent(PRINTER_STATUS_SERVICE_CLASS), printerId)
                .map(new Function<String, PrinterStatus>() {
                    @Override
                    public PrinterStatus apply(String json) throws Exception {
                        return PrinterStatus.fromJson(json);
                    }
                });
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
        return printSettingsMessenger.createObservableForServiceIntent(getIntent(PRINT_SETTINGS_SERVICE_CLASS), printerRequest.toJson())
                .map(new Function<String, PrinterSettingsList>() {
                    @Override
                    public PrinterSettingsList apply(String json) throws Exception {
                        return PrinterSettingsList.fromJson(json);
                    }
                });
    }

    private Intent getIntent(String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PRINT_SERVICE_PACKAGE, className));
        return intent;
    }
}
