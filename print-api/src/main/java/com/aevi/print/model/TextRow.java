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

import com.aevi.android.rxmessenger.JsonConverter;

import static com.aevi.print.model.PrinterFont.DEFAULT_FONT;

/**
 * This class represents a single text line in a {@link com.aevi.print.model.PrintPayload}.
 */
public class TextRow implements PrintRow, Cloneable {

    private String text;
    private int printerFontId = DEFAULT_FONT;
    private Underline underline = Underline.NONE;
    private FontStyle fontStyle = FontStyle.NORMAL;
    private Alignment alignment = Alignment.LEFT;

    /**
     * Creates a left aligned text row with with no styling.
     *
     * @param text The row text. This parameter must not be null.
     */
    public TextRow(String text) {
        this(text, null);
    }

    /**
     * Creates a left aligned text row using the font given by the fontId.
     * The font must match a {@link PrinterFont} as provided by the printer via its settings methods. See {@link PrinterSettings#getPrinterFonts()}.
     *
     * @param text        The text to add
     * @param printerFont The printer font to use
     */
    public TextRow(String text, PrinterFont printerFont) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (printerFont != null) {
            this.printerFontId = printerFont.getId();
        }
        this.text = text;
    }

    /**
     * Gets the text of this text row.
     *
     * @return the text row's text.
     */
    public String getText() {
        return text;
    }

    /**
     * The id of the printer font to be used for this text row
     *
     * @return A printer font id
     */
    public int getPrinterFontId() {
        return printerFontId;
    }

    /**
     * Gets the {@link Underline} style of this text row.
     *
     * @return the underline style.
     */
    public Underline getUnderlineStyle() {
        return underline;
    }

    /**
     * Sets the {@link Underline} style of this text row.
     *
     * @param underline the underline style to set
     * @return This textrow object
     */
    public TextRow underline(Underline underline) {
        this.underline = underline;
        return this;
    }

    /**
     * Allows the font to be set for this text row
     *
     * @param font The font to set
     * @return This textRow object
     */
    public TextRow setFont(PrinterFont font) {
        if (font != null) {
            this.printerFontId = font.getId();
        }
        return this;
    }

    /**
     * Gets the {@link Alignment} style of this text row.
     *
     * @return the alignment style to set.
     */
    public Alignment getAlignmentStyle() {
        return alignment;
    }

    /**
     * Sets the {@link com.aevi.print.model.Alignment} style of this text row.
     *
     * @param alignment the alignment style to set
     * @return This textrow object
     */
    public TextRow align(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Gets the {@link FontStyle} style of this text row.
     *
     * @return the font style
     */
    public FontStyle getFontStyle() {
        return fontStyle;
    }

    /**
     * Sets the {@link FontStyle} style of this text row.
     *
     * @param fontStyle the font style to set
     * @return This textrow object
     */
    public TextRow fontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    /**
     * Returns a string representation of this object
     */
    @Override
    public String toString() {
        return String.format("text=%s,alignment=%s,underline=%s,font style=%s", text, alignment, underline, fontStyle);
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static TextRow fromJson(String json) {
        return JsonConverter.deserialize(json, TextRow.class);
    }
}
