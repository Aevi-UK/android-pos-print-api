package com.aevi.print;

import android.graphics.Bitmap;
import android.os.Build;

import com.aevi.print.model.FontStyle;
import com.aevi.print.model.PaperKind;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterFont;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.TestPrinterFontBuilder;
import com.aevi.print.model.TestPrinterSettingsBuilder;
import com.aevi.print.model.TextRow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBitmap;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class PrintPreviewTest {

    private static final PrinterFont FONT_A = new TestPrinterFontBuilder()
            .withId(1)
            .withName("Font A")
            .withSupportedFontStyles(FontStyle.values())
            .withHeight(24)
            .withWidth(12)
            .withLineHeight(32)
            .withIsDefault(true)
            .withNumColumns(48)
            .build();

    private static final PrinterFont FONT_B = new TestPrinterFontBuilder()
            .withId(2)
            .withName("Font B")
            .withSupportedFontStyles(FontStyle.values())
            .withHeight(17)
            .withWidth(9)
            .withLineHeight(25)
            .withIsDefault(false)
            .withNumColumns(64)
            .build();

    private static final PrinterFont FONT_C = new TestPrinterFontBuilder()
            .withId(3)
            .withName("Font C")
            .withSupportedFontStyles(FontStyle.values())
            .withHeight(17)
            .withWidth(9)
            .withLineHeight(25)
            .withIsDefault(false)
            .withNumColumns(64)
            .build();

    private static final PrinterFont[] DEFAULT_FONTS = new PrinterFont[]{
            FONT_A,
            FONT_B
    };

    private static final PrinterFont[] NO_DEFAULT_FONTS = new PrinterFont[]{
            FONT_B,
            FONT_C
    };

    private PrinterSettings getPrinterSettings() {
        return new TestPrinterSettingsBuilder("TestPrinterSettings", 80, 75, 7.68f)
                .withPrinterFonts(DEFAULT_FONTS)
                .withPaperKind(PaperKind.THERMAL)
                .withDoesReportStatus(true)
                .withCanHandleCommands(true)
                .withDoesSupportCodepages(false)
                .build();
    }

    private PrinterSettings getPrinterSettingsNoDefaultFont() {
        return new TestPrinterSettingsBuilder("TestPrinterSettings", 80, 75, 7.68f)
                .withPrinterFonts(NO_DEFAULT_FONTS)
                .withPaperKind(PaperKind.THERMAL)
                .withDoesReportStatus(true)
                .withCanHandleCommands(true)
                .withDoesSupportCodepages(false)
                .build();
    }

    private PrinterSettings getPrinterSettingsNoFonts() {
        return new TestPrinterSettingsBuilder("TestPrinterSettings", 80, 75, 7.68f)
                .withPaperKind(PaperKind.THERMAL)
                .build();
    }

    private PrintPreview setupValidPrintPreview(boolean withFonts) {
        PrintPayload payload = new PrintPayload();
        payload.append("Some text");
        if (withFonts) {
            return new PrintPreview(payload, getPrinterSettings());
        } else {
            return new PrintPreview(payload, getPrinterSettingsNoFonts());
        }
    }

    @Test
    public void doesSetupDefaultFontCorrectly() {
        PrintPreview printPreview = setupValidPrintPreview(true);

        assertThat(printPreview.defaultFont).isNotNull();
        assertThat(printPreview.defaultFont).isEqualTo(FONT_A);
    }

    @Test
    public void doesSetupFirstFontCorrectlyWhenNoDefaultSpecified() {
        PrintPayload payload = new PrintPayload();
        PrintPreview printPreview = new PrintPreview(payload, getPrinterSettingsNoDefaultFont());

        assertThat(printPreview.defaultFont).isNotNull();
        assertThat(printPreview.defaultFont).isEqualTo(FONT_B);
    }

    @Test
    public void doesSetupDefaultFontCorrectlyNoFonts() {
        PrintPreview printPreview = setupValidPrintPreview(false);

        assertThat(printPreview.defaultFont).isNotNull();
        assertThat(printPreview.defaultFont).isEqualTo(PrintPreview.UNKNOWN_FONT);
    }

    @Test
    public void canDetermineHeight() {
        PrintPayload payload = new PrintPayload();
        payload.append("Line1");
        payload.append("Line2");
        payload.append("Line3");
        payload.append("Line4");
        PrintPreview printPreview = new PrintPreview(payload, getPrinterSettings());

        assertThat(printPreview.determineHeight()).isEqualTo(144);
    }

    @Test
    public void canGetBitmap() {
        PrintPreview printPreview = setupValidPrintPreview(true);

        ShadowBitmap bitmap = Shadows.shadowOf(printPreview.getBitmap());
        assertThat(bitmap).isNotNull();
        assertThat(bitmap.getDescription()).isEqualTo("Bitmap for Bitmap (576 x 48)\n" +
                "Bitmap (9 x 32) compressed as PNG with quality 0");
        assertThat(printPreview.availableWidth).isEqualTo(576);
    }

    @Test
    public void canGetScaledBitmap() {
        PrintPreview printPreview = setupValidPrintPreview(true);

        ShadowBitmap bitmap = Shadows.shadowOf(printPreview.getScaledBitmap(RuntimeEnvironment.application));
        assertThat(bitmap).isNotNull();
        assertThat(bitmap.getDescription()).isEqualTo("Bitmap for Bitmap (576 x 48)\n" +
                "Bitmap (9 x 32) compressed as PNG with quality 0 scaled to 708 x 708 with filter true");
    }

    @Test
    public void handlesNoFonts() {
        PrintPreview printPreview = setupValidPrintPreview(false);

        Bitmap bitmap = printPreview.getScaledBitmap(RuntimeEnvironment.application);
        assertThat(bitmap).isNotNull();
    }

    @Test
    public void willSplitLongLineCorrectly() {
        PrintPayload payload = new PrintPayload();
        TextRow row = payload.append("this is a very long line of text that should be split across multiple lines");

        PrintPreview printPreview = new PrintPreview(payload, getPrinterSettings());

        List<TextRow> rows = printPreview.splitLongTextRow(row);

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getText()).isEqualTo("this is a very long line of text that should be ");
        assertThat(rows.get(1).getText()).isEqualTo("split across multiple lines");
        assertThat(rows.get(0).getText().length()).isLessThanOrEqualTo(FONT_A.getNumColumns());
        assertThat(rows.get(1).getText().length()).isLessThanOrEqualTo(FONT_A.getNumColumns());
    }
}
