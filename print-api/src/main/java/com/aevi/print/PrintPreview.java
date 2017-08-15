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
import android.graphics.Typeface;
import android.util.Log;

import com.aevi.print.model.Alignment;
import com.aevi.print.model.FontStyle;
import com.aevi.print.model.ImageRow;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrintRow;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.TextRow;
import com.aevi.print.model.Underline;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PrintPreview {

    private static final String TAG = PrintPreview.class.getSimpleName();

    // FIXME these should probably be defined in printer settings
    private static final int VERTICAL_MARGIN = 4;
    private static final int FONT_SIZE = 26;
    private static final int LINE_HEIGHT = FONT_SIZE + VERTICAL_MARGIN;

    private final PrintPayload printPayload;
    private final PrinterSettings printerSettings;
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final int availableWidth;
    private int cursor = VERTICAL_MARGIN;

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

    public byte[] getCompressedBitmap() {
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

    private List<TextRow> splitLongTextRow(TextRow textRow) {
        List<TextRow> result = new ArrayList<TextRow>();
        Paint paint = getPaint(textRow);
        String line = textRow.getText();
        String remaining = "";
        while (line.length() > 0) {
            float width = paint.measureText(line);
            if (width > availableWidth) {
                remaining = line.charAt(line.length() - 1) + remaining;
                line = line.substring(0, line.length() - 1);
            } else {
                TextRow newRow = new TextRow(line)
                        .align(textRow.getAlignmentStyle())
                        .fontStyle(textRow.getFontStyle())
                        .underline(textRow.getUnderlineStyle());
                result.add(newRow);
                line = remaining;
                remaining = "";
            }
        }
        return result;
    }

    public static float getWidthForCharacters(int numchars) {
        String row = getStringLength(numchars);
        TextRow textRow = new TextRow(row);
        Paint paint = getPaint(textRow);
        String line = textRow.getText();
        return paint.measureText(line);
    }

    private static String getStringLength(int length) {
        char[] bytes = new char[length];
        Arrays.fill(bytes, 'M');
        return new String(bytes);
    }

    public int determineHeight() {
        int height = 0;
        for (PrintRow row : processRowsBeforePreviewing(printPayload.getRows())) {
            if (row instanceof TextRow) {
                height += LINE_HEIGHT + VERTICAL_MARGIN * 2;
            } else if (row instanceof ImageRow) {
                height += ((ImageRow) row).getImage().getHeight() + VERTICAL_MARGIN * 2;
            }
        }
        return height + VERTICAL_MARGIN * 2;
    }

    private void drawTextRow(TextRow textRow) {
        drawBitMap(createBitMap(textRow), textRow.getAlignmentStyle());
    }

    private Bitmap createBitMap(TextRow textRow) {
        Paint paint = getPaint(textRow);

        Float width = paint.measureText(textRow.getText());

        Bitmap textRowBitmap = Bitmap.createBitmap(width.intValue(), LINE_HEIGHT + VERTICAL_MARGIN, Bitmap.Config.ARGB_8888);
        Canvas textRowCanvas = new Canvas(textRowBitmap);

        paint.setUnderlineText(textRow.getUnderlineStyle() != Underline.NONE);

        if (textRow.getFontStyle() == FontStyle.INVERTED || textRow.getFontStyle() == FontStyle.INVERTED_EMPHASIZED) {
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.BLACK);
            bgPaint.setStyle(Paint.Style.FILL);
            textRowCanvas.drawPaint(bgPaint);
            paint.setColor(Color.WHITE);
        } else {
            paint.setColor(Color.BLACK);
        }
        textRowCanvas.drawText(textRow.getText(), 0, LINE_HEIGHT - VERTICAL_MARGIN, paint);

        if (textRow.getUnderlineStyle() == Underline.DOUBLE) {
            textRowCanvas.drawLine(0f, LINE_HEIGHT - 3, width, LINE_HEIGHT - 3, paint);
        }

        return textRowBitmap;
    }

    private static Paint getPaint(TextRow textRow) {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        paint.setFakeBoldText(textRow.getFontStyle() == FontStyle.EMPHASIZED || textRow.getFontStyle() == FontStyle.INVERTED_EMPHASIZED);
        paint.setTextSize(FONT_SIZE);
        return paint;
    }

    private void drawImageRow(ImageRow imageRow) {
        Bitmap image = imageRow.getImage();
        if (image.getWidth() > availableWidth) {
            float aspectRatio = image.getWidth() / (float) image.getHeight();
            int height = Math.round(availableWidth / aspectRatio);
            image = Bitmap.createScaledBitmap(image, availableWidth, height, false);
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
