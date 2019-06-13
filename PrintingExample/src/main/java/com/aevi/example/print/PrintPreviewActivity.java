package com.aevi.example.print;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.aevi.print.PrintPreview;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterSettings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintPreviewActivity extends AppCompatActivity {

    public static final String KEY_PAYLOAD = "payload";
    public static final String KEY_PRINTER_SETTINGS = "settings";

    private PrintPayload payload;
    private PrinterSettings printerSettings;

    @BindView(R.id.print_preview_image)
    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_PAYLOAD) && intent.hasExtra(KEY_PRINTER_SETTINGS)) {
            payload = PrintPayload.fromJson(intent.getStringExtra(KEY_PAYLOAD));
            printerSettings = PrinterSettings.fromJson(intent.getStringExtra(KEY_PRINTER_SETTINGS));
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPreview();
    }

    private void setupPreview() {
        PrintPreview printPreview = new PrintPreview(payload, printerSettings);
        Bitmap scaledBitmap = printPreview.getScaledBitmap(this);
        preview.setImageBitmap(scaledBitmap);
        preview.setMinimumWidth(printerSettings.getPaperWidth());
    }
}