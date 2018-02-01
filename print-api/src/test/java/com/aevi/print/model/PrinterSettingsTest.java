package com.aevi.print.model;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP, manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class PrinterSettingsTest {

    private PrinterSettings getPrinterSettings() {
        return new TestPrinterSettingsBuilder("printer-id", 80, 75, 7.68f)
                .withPaperKind(PaperKind.THERMAL)
                .build();
    }

    @Test
    public void printerSettingsDefaultsToPrinterIdIfNotSet() {
        PrinterSettings printerSettings = getPrinterSettings();
        assertThat(printerSettings.getPrinterId()).isEqualTo("printer-id");
        assertThat(printerSettings.getDisplayName()).isEqualTo("printer-id");

        printerSettings.setDisplayName("display-name");
        assertThat(printerSettings.getDisplayName()).isEqualTo("display-name");
        assertThat(printerSettings.getPrinterId()).isEqualTo("printer-id");
    }
}
