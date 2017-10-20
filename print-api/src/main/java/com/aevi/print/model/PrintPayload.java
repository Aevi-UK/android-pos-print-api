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

import android.graphics.Bitmap;

import com.aevi.print.PrinterManager;
import com.aevi.print.json.JsonConverter;
import com.aevi.print.json.JsonOption;
import com.aevi.print.json.Jsonable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link PrintPayload} contains a collection of {@link PrintRow} that
 * represent a printable text document.
 *
 * By binding to the {@link PrinterManager} this pay load can be send to the
 * receipt printer for printing.
 */
public class PrintPayload implements Jsonable {

    private List<JsonOption> rows = new ArrayList<>();

    private int codePage = -1;
    private String printerId;
    private String languageCode;

    /**
     * Creates an empty {@link PrintPayload} object.
     */
    public PrintPayload() {
    }

    /**
     * Creates an empty {@link PrintPayload} object.
     *
     * To be sent to a specific printer driver
     *
     * @param printerId The id of the printer to send this payload to for printing
     */
    public PrintPayload(String printerId) {
        this.printerId = printerId;
    }

    /**
     * Appends the given text row to this printer pay load.
     *
     * @param text the text to append to the printer pay load. This parameter
     *             must not be null.
     * @return The new {@link TextRow} object added to the payload
     */
    public TextRow append(String text) {
        return append(text, null);
    }

    /**
     * Appends the given text row to this printer payload and ensure the printer font given is used
     *
     * The font must match a {@link PrinterFont} as provided by the printer via its settings methods. See {@link PrinterSettings#getPrinterFonts()}.
     *
     * @param text        The text to add
     * @param printerFont The printer font to use
     */
    public TextRow append(String text, PrinterFont printerFont) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        TextRow textRow = new TextRow(text, printerFont);
        rows.add(new JsonOption(textRow));
        return textRow;
    }

    private void append(PrintRow row) {
        rows.add(new JsonOption(row));
    }

    /**
     * Appends another printPayload to this printer pay load
     *
     * @param toAppendPayload The payload to append
     */
    public void append(PrintPayload toAppendPayload) {
        if (toAppendPayload == null) {
            throw new IllegalArgumentException("appending payload must not be null");
        }

        PrintRow[] rows = toAppendPayload.getRows();
        for (PrintRow row : rows) {
            append(row);
        }
    }

    /**
     * Add an empty line to the pay load
     */
    public void appendEmptyLine() {
        rows.add(new JsonOption(new TextRow(" ")));
    }

    /**
     * Appends a line of the same character to this print payload
     *
     * @param columns   The number of columns to send the character for
     * @param character The character to use for the line
     * @return The new {@link TextRow} object added to the payload
     */
    public TextRow appendLineOfChar(int columns, String character) {
        String line = character;
        while (line.length() < columns) {
            line += character;
        }
        return append(line);
    }

    /**
     * Appends two strings to a line one aligned left one right
     *
     * @param columns The number of text columns to assume for the row
     * @param left    The String to put on the left
     * @param right   The String to put on the right
     * @return The new {@link TextRow} object added to the payload
     */
    public TextRow appendLeftRight(int columns, String left, String right) {
        String space = getSpaces(columns - left.length() - right.length());
        return append(left + space + right);
    }

    private String getSpaces(int length) {
        if (length > 0) {
            char[] bytes = new char[length];
            Arrays.fill(bytes, ' ');
            return new String(bytes);
        }
        return "";
    }

    /**
     * Appends the given image to this printer pay load. The default alignment
     * of the image is left.
     *
     * @param image the image to append to this printer pay load. This parameter
     *              must not be null.
     * @return The new {@link ImageRow} object added to the payload
     */
    public ImageRow append(Bitmap image) {
        return append(image, true);
    }

    public ImageRow append(Bitmap image, boolean scaleToFit) {
        if (image == null) {
            throw new IllegalArgumentException("image must not be null");
        }
        ImageRow imageRow = new ImageRow(image, scaleToFit);
        rows.add(new JsonOption(imageRow));
        return imageRow;
    }

    /**
     * Gets the rows in this printer pay load.
     *
     * @return the rows in this printer pay load
     */
    public PrintRow[] getRows() {

        // TODO bit naff. sort this out possibly by allowing custom serializer/deserializers to be added to service messenger
        PrintRow[] printRows = new PrintRow[rows.size()];
        int count = 0;
        for (JsonOption jsonOption : rows) {
            printRows[count++] = (PrintRow) jsonOption.getValue();
        }
        return printRows;
    }

    /**
     * Get the codepage to be used when printing out this payload
     *
     * @return A code page id. -1 indicates the default code page is being used.
     */
    public int getCodePage() {
        return codePage;
    }

    /**
     * Sets the codepage to be used when printing this payload
     *
     * @param codePage The codepage. See printer specific documentation for details. Set to -1 for the default codepage.
     */
    public void setCodePage(int codePage) {
        this.codePage = codePage;
    }

    /**
     * @return Gets the ISO-639 language code set for this payload or null for the default
     */
    public String getLanguage() {
        return languageCode;
    }

    /**
     * Sets the language code to be used for this print payload
     * @param languageCode An ISO-639 two letter language code (usually obtained from {@link java.util.Locale#getLanguage()} if required)
     */
    public void setLanguage(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Sets the id of the printer that should be used to print this payload
     *
     * @param printerId The printerId. Should be an id of a printer returned
     */
    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    /**
     * Returns the id of the printert that should be used to print this payload
     *
     * @return The id of the printer
     */
    public String getPrinterId() {
        return printerId;
    }

    /**
     * @return True if a printer has been selected for this payload
     */
    public boolean hasPrinterId() {
        return printerId != null && !printerId.isEmpty();
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static PrintPayload fromJson(String json) {
        return JsonConverter.deserialize(json, PrintPayload.class);
    }
}
