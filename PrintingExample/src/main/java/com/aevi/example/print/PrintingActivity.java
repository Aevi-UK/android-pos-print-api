package com.aevi.example.print;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aevi.print.PrinterApi;
import com.aevi.print.PrinterManager;
import com.aevi.print.model.PrintJob;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.PrinterSettingsList;
import com.aevi.print.model.PrinterStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Setter;
import butterknife.ViewCollections;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.aevi.example.print.PrintPreviewActivity.KEY_PAYLOAD;
import static com.aevi.example.print.PrintPreviewActivity.KEY_PRINTER_SETTINGS;
import static com.aevi.print.model.PrinterMessages.ACTION_OPEN_CASH_DRAWER;

public class PrintingActivity extends AppCompatActivity {

    static final Setter<View, Boolean> ENABLED = (view, value, index) -> view.setEnabled(value);
    private static final String TAG = PrintingActivity.class.getSimpleName();
    private static final String KEY_POS_STORE = "posStore";
    private static final int MAX_STATUS_LENGTH = 3;
    private static final SimpleDateFormat STATUS_TIME_FORMAT = new SimpleDateFormat("dd-MM HH:mm:ss");
    @BindView(R.id.print_driver_spinner)
    Spinner driversSpinner;

    @BindView(R.id.print_codepage_spinner)
    Spinner codepageSpinner;

    @BindView(R.id.print_example_spinner)
    Spinner exampleSpinner;

    @BindView(R.id.printer_status)
    TextView printerStatusDisplay;

    @BindViews({R.id.button_print,
            R.id.button_preview,
            R.id.button_codepage_preview,
            R.id.button_codepage_print,
            R.id.button_open_drawer})
    List<Button> buttons;

    @BindView(R.id.button_open_drawer)
    Button openDrawerButton;

    @BindView(R.id.codepages)
    ViewGroup codepagesLayout;
    private PrinterManager printerManager;
    private PrintPayloadData printPayloadData;
    private String subscribedStatusId;
    private PrinterSettings[] printerSettingsList;
    private PrinterSettings selectedPrinter;
    private Disposable printerStatusDisposable;
    private Disposable printerSettingsDisposable;
    private SparseArray spinnerPosStore;
    private List<StatusRecord> latestPrinterStatus = new ArrayList<>(MAX_STATUS_LENGTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            spinnerPosStore = savedInstanceState.getSparseParcelableArray(KEY_POS_STORE);
        } else {
            spinnerPosStore = new SparseArray();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        printPayloadData = new PrintPayloadData(this);
        enableButtons(false);
        clearPrinterStatus();
        setupPrintDrivers();
    }

    @Override
    protected void onPause() {
        unsubscribeFromPrinterSettings();
        unsubscribeFromPrinterStatus();
        super.onPause();
    }

    private void clearPrinterStatus() {
        latestPrinterStatus.clear();
        printerStatusDisplay.setText("");
    }

    private void setupPrintDrivers() {
        printerManager = PrinterApi.getPrinterManager(this);
        if (printerManager.isPrinterServiceAvailable()) {
            subscribeToPrinterSettings();
        } else {
            Toast.makeText(this, R.string.print_service_not_installed, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private synchronized void subscribeToPrinterStatus(final String printerId) {
        if (printerId != null && !printerId.equals(subscribedStatusId)) {
            unsubscribeFromPrinterStatus();

            printerStatusDisposable = printerManager.status(printerId).subscribe(new Consumer<PrinterStatus>() {
                @Override
                public void accept(@NonNull PrinterStatus printerStatus) throws Exception {
                    Log.d(TAG, "Received status: " + printerStatus.getStatus());

                    while (latestPrinterStatus.size() >= MAX_STATUS_LENGTH) {
                        latestPrinterStatus.remove(0);
                    }

                    StatusRecord record = new StatusRecord();
                    record.dateTime = STATUS_TIME_FORMAT.format(System.currentTimeMillis());
                    record.status = printerStatus.getStatus();
                    record.printerId = printerId;
                    latestPrinterStatus.add(record);

                    StringBuilder stringBuilder = new StringBuilder();
                    for (StatusRecord statusRecord : latestPrinterStatus) {
                        stringBuilder
                                .insert(0, "\n")
                                .insert(0, statusRecord.status)
                                .insert(0, " ")
                                .insert(0, statusRecord.printerId)
                                .insert(0, " ")
                                .insert(0, statusRecord.dateTime);
                    }
                    printerStatusDisplay.setText(stringBuilder.toString());
                }
            });
            subscribedStatusId = printerId;
        }
    }

    private void subscribeToPrinterSettings() {
        Log.d(TAG, "getPrintersSettings()");

        unsubscribeFromPrinterSettings();

        printerSettingsDisposable = printerManager.getPrintersSettings()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PrinterSettingsList>() {
                    @Override
                    public void accept(@NonNull PrinterSettingsList printerSettings) throws Exception {
                        printerSettingsList = printerSettings.getPrinterSettings();
                        List<String> printerNames = new ArrayList<>();
                        for (PrinterSettings printerSetting : printerSettingsList) {
                            printerNames.add(printerSetting.getDisplayName());
                        }
                        Log.d(TAG, "Got printer settings list: " + printerSettingsList.length);
                        driversSpinner.setAdapter(new ArrayAdapter<>(PrintingActivity.this,
                                android.R.layout.simple_spinner_item,
                                printerNames));
                        Object pos = spinnerPosStore.get(R.id.print_driver_spinner);
                        if (pos != null) {
                            int position = (int) pos;
                            if (position < printerNames.size()) {
                                driversSpinner.setSelection(position);
                            }
                        }
                        enableButtons(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "Failed during get settings", throwable);
                    }
                });
    }

    private void unsubscribeFromPrinterSettings() {
        if (printerSettingsDisposable != null) {
            printerSettingsDisposable.dispose();
            printerSettingsDisposable = null;
        }
    }

    private synchronized void unsubscribeFromPrinterStatus() {
        if (printerStatusDisposable != null) {
            printerStatusDisposable.dispose();
            printerStatusDisposable = null;
            subscribedStatusId = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSpinnerPos(R.id.print_example_spinner, exampleSpinner.getSelectedItemPosition());
        saveSpinnerPos(R.id.print_driver_spinner, driversSpinner.getSelectedItemPosition());
        saveSpinnerPos(R.id.print_codepage_spinner, codepageSpinner.getSelectedItemPosition());
        outState.putSparseParcelableArray(KEY_POS_STORE, spinnerPosStore);
    }

    private void saveSpinnerPos(int print_example_spinner, int selectedItemPosition) {
        spinnerPosStore.put(print_example_spinner, selectedItemPosition);
    }

    @OnItemSelected(R.id.print_driver_spinner)
    public void selectDriver(int position) {
        if (printerSettingsList != null && printerSettingsList.length > 0) {
            if (position < printerSettingsList.length) {
                selectedPrinter = printerSettingsList[position];
            } else {
                selectedPrinter = printerSettingsList[0];
            }
            subscribeToPrinterStatus(selectedPrinter.getPrinterId());

            codepagesLayout.setVisibility(selectedPrinter.doesSupportCodePages() ? View.VISIBLE : View.GONE);
            openDrawerButton.setEnabled(doesSupportCashDrawer());
        }
    }

    private boolean doesSupportCashDrawer() {
        if (selectedPrinter.canHandleCommands()) {
            String[] commands = selectedPrinter.getCommands();
            if (commands != null) {
                for (String command : commands) {
                    if (ACTION_OPEN_CASH_DRAWER.equals(command)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @OnClick(R.id.button_print)
    public void printClick(View view) {
        print(getPrintPayload());
    }

    @OnClick(R.id.button_preview)
    public void previewClick(View view) {
        showPreview(getPrintPayload());
    }

    @OnClick(R.id.button_open_drawer)
    public void cashOpenDrawerClick(View view) {
        sendAction(ACTION_OPEN_CASH_DRAWER);
    }

    @OnClick(R.id.button_codepage_print)
    public void codepagePrintClick(View view) {
        print(getCodepagePayload());
    }

    @OnClick(R.id.button_codepage_preview)
    public void codepagePreviewClick(View view) {
        showPreview(getCodepagePayload());
    }

    public PrintPayload getCodepagePayload() {
        return printPayloadData.printCodePageSymbols(codepageSpinner.getSelectedItemPosition());
    }

    private PrintPayload getPrintPayload() {
        return printPayloadData.createTestPayLoad(selectedPrinter, exampleSpinner.getSelectedItemPosition());
    }

    private void enableButtons(boolean enable) {
        if (enable) {
            ViewCollections.set(buttons, ENABLED, enable);
        }
    }

    private void showPreview(PrintPayload payload) {
        Intent startIntent = new Intent(PrintingActivity.this, PrintPreviewActivity.class);
        startIntent.putExtra(KEY_PAYLOAD, payload.toJson());
        startIntent.putExtra(KEY_PRINTER_SETTINGS, selectedPrinter.toJson());
        startActivity(startIntent);
    }

    private void print(PrintPayload payload) {
        if (!printerManager.isPrinterServiceAvailable()) {
            Log.i(TAG, "Print manager is not installed or disabled");
            return;
        }

        Log.i(TAG, "printing a receipt with driver: " + selectedPrinter.getPrinterId());
        payload.setPrinterId(selectedPrinter.getPrinterId());
        printerManager.print(payload)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PrintJob>() {
                    @Override
                    public void accept(@NonNull PrintJob printResult) throws Exception {
                        displayPrintResultMessage(printResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "ERROR: ", throwable);
                        showToastMessage("ERROR: " + throwable.getMessage());
                    }
                });
    }

    private void sendAction(String action) {
        if (!printerManager.isPrinterServiceAvailable()) {
            Log.i(TAG, "Print manager is not installed or disabled");
            return;
        }

        Log.i(TAG, String.format("Sending action '%s' to printer with driver: %s", action, selectedPrinter.getPrinterId()));
        printerManager.sendAction(selectedPrinter.getPrinterId(), action);
    }

    private void displayPrintResultMessage(@NonNull PrintJob printResult) {
        if (printResult.getPrintJobState() == PrintJob.State.FAILED) {
            showToastMessage(getString(R.string.failure_toast_message, printResult.getPrintJobState(),
                    printResult.getFailedReason(), printResult.getDiagnosticMessage()));
            Log.d(TAG, "Printing result: " + printResult.getPrintJobState() + " : " +
                    printResult.getFailedReason() + " - " + printResult.getDiagnosticMessage());
        } else {
            showToastMessage(getString(R.string.result_toast_message, printResult.getPrintJobState()));
            Log.d(TAG, "Printing result: " + printResult.getPrintJobState());
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class StatusRecord {

        String printerId;
        String dateTime;
        String status;
    }
}
