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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.aevi.print.model.Alignment;
import com.aevi.print.model.FontStyle;
import com.aevi.print.model.ImageRow;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrintRow;
import com.aevi.print.model.PrinterFont;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.TextRow;
import com.aevi.print.model.Underline;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.width;
import static android.graphics.Bitmap.createScaledBitmap;

public final class PrintPreview {

    private static final String TAG = PrintPreview.class.getSimpleName();

    // FIXME these should probably be defined in printer settings
    private static final int VERTICAL_MARGIN = 8;

    private final PrintPayload printPayload;
    private final PrinterSettings printerSettings;
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final int availableWidth;
    private int cursor = VERTICAL_MARGIN;

    // font to be used for preview if printer driver returns no font details
    private PrinterFont UNKNOWN_FONT = new PrinterFont(PrinterFont.DEFAULT_FONT, "Unknown font", 12, 24, true, 48, 32, FontStyle.values());

    public PrintPreview(PrintPayload printPayload, PrinterSettings printerSettings) {
        this.availableWidth = printerSettings.getPaperWidth();
        this.printPayload = printPayload;
        this.printerSettings = printerSettings;
        bitmap = Bitmap.createBitmap(availableWidth, determineHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        fillBitmap();
    }

    public Bitmap getBitmap() {
        byte[] bytes = getCompressedBitmap();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private byte[] getCompressedBitmap() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);
        return out.toByteArray();
    }

    private void fillBitmap() {
        for (PrintRow row : processRowsBeforePreviewing(printPayload.getRows())) {
            try {
                if (row instanceof TextRow) {
                    drawTextRow((TextRow) row);
                } else if (row instanceof ImageRow) {
                    drawImageRow((ImageRow) row);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to add item to print receipt", e);
            }
        }
    }

    private List<PrintRow> processRowsBeforePreviewing(PrintRow[] rows) {
        List<PrintRow> result = new ArrayList<PrintRow>();
        for (PrintRow row : rows) {
            if (row instanceof TextRow) {
                result.addAll(splitLongTextRow((TextRow) row));
            } else {
                result.add(row);
            }
        }
        return result;
    }

    protected List<TextRow> splitLongTextRow(TextRow textRow) {
        List<TextRow> result = new ArrayList<TextRow>();
        PrinterFont font = getFont(textRow.getPrinterFontId());
        String line = textRow.getText();
        String remaining = "";
        while (line.length() > 0) {
            if (line.length() > font.getNumColumns()) {
                remaining = line.charAt(line.length() - 1) + remaining;
                line = line.substring(0, line.length() - 1);
            } else {
                TextRow newRow = new TextRow(line)
                        .align(textRow.getAlignmentStyle())
                        .fontStyle(textRow.getFontStyle())
                        .underline(textRow.getUnderlineStyle())
                        .setFont(font);
                result.add(newRow);
                line = remaining;
                remaining = "";
            }
        }
        return result;
    }

    /**
     * Returns the estimated height of the receipt in dots/pixels
     *
     * @return The estimated height in pixels
     */
    public int determineHeight() {
        int height = 0;
        for (PrintRow row : processRowsBeforePreviewing(printPayload.getRows())) {
            if (row instanceof TextRow) {
                PrinterFont font = getFont(((TextRow) row).getPrinterFontId());
                height += font.getLineHeight();
            } else if (row instanceof ImageRow) {
                height += ((ImageRow) row).getImage().getHeight() + VERTICAL_MARGIN * 2;
            }
        }
        return height + VERTICAL_MARGIN * 2;
    }

    private PrinterFont getFont(int printerFontId) {
        if (printerSettings.getPrinterFonts() != null) {
            for (PrinterFont font : printerSettings.getPrinterFonts()) {
                if (font.getId() == printerFontId) {
                    return font;
                }
            }
        }
        return UNKNOWN_FONT;
    }

    private void drawTextRow(TextRow textRow) {
        drawBitMap(createTextRowBitMap(textRow), textRow.getAlignmentStyle());
    }

    private Bitmap createTextRowBitMap(TextRow textRow) {
        PrinterFont font = getFont(textRow.getPrinterFontId());
        Paint paint = getPaint(textRow, font);
        String txt = textRow.getText();
        Rect bounds = new Rect();
        paint.getTextBounds(txt, 0, txt.length(), bounds);
        int textBaseline = bounds.height();
        if (textRow.getUnderlineStyle() == Underline.DOUBLE) {
            bounds.set(bounds.left, bounds.top, bounds.right, bounds.bottom + VERTICAL_MARGIN);
        }

        Bitmap textRowBitmap = Bitmap.createBitmap(bounds.width() + 4, bounds.height(), Bitmap.Config.ARGB_8888);
        Canvas textRowCanvas = new Canvas(textRowBitmap);

        if (textRow.getFontStyle() == FontStyle.INVERTED || textRow.getFontStyle() == FontStyle.INVERTED_EMPHASIZED) {
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.BLACK);
            bgPaint.setStyle(Paint.Style.FILL);
            textRowCanvas.drawPaint(bgPaint);
        }

        textRowCanvas.drawText(textRow.getText(), 0, textBaseline, paint);

        if (textRow.getUnderlineStyle() == Underline.DOUBLE) {
            int linepos = textRowCanvas.getHeight() - 1;
            textRowCanvas.drawLine(0f, linepos, width, linepos, paint);
        }

        return textRowBitmap;
    }

    private Paint getPaint(TextRow textRow, PrinterFont font) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        paint.setFakeBoldText(textRow.getFontStyle() == FontStyle.EMPHASIZED || textRow.getFontStyle() == FontStyle.INVERTED_EMPHASIZED);
        paint.setUnderlineText(textRow.getUnderlineStyle() != Underline.NONE);
        paint.setTextSize(font.getHeight());
        paint.setTextAlign(Paint.Align.LEFT);
        if (textRow.getFontStyle() == FontStyle.INVERTED || textRow.getFontStyle() == FontStyle.INVERTED_EMPHASIZED) {
            paint.setColor(Color.WHITE);
        } else {
            paint.setColor(Color.BLACK);
        }

        return paint;
    }

    private void drawImageRow(ImageRow imageRow) {
        Bitmap image = imageRow.getImage();
        if (image.getWidth() > availableWidth) {
            float aspectRatio = image.getWidth() / (float) image.getHeight();
            int height = Math.round(availableWidth / aspectRatio);
            image = createScaledBitmap(image, availableWidth, height, false);
        }
        Alignment alignment = imageRow.getAlignmentStyle();
        drawBitMap(image, alignment);
    }

    private void drawBitMap(Bitmap image, Alignment alignment) {
        if (bitmap.getWidth() > availableWidth) {
            throw new IllegalArgumentException("Image is larger than the available width of the paper");
        }
        canvas.drawBitmap(image, xPosition(alignment, image.getWidth()), cursor, new Paint());
        cursor += image.getHeight() + VERTICAL_MARGIN;
    }

    private int xPosition(Alignment alignment, int width) {
        int x = 0;
        int remainingSpace = availableWidth - width;
        switch (alignment) {
            case CENTER:
                x = remainingSpace / 2;
                break;
            case LEFT:
                x = 0;
                break;
            case RIGHT:
                x = remainingSpace;
                break;
        }
        return x;
    }
}
