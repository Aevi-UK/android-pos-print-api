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

public class PrinterFont {

    public static final int DEFAULT_FONT = -1;

    private int id;
    private String name;
    private int width;
    private int height;
    private boolean isDefault;
    private int numColumns;
    private int lineHeight;
    private FontStyle[] supportedFontStyles;

    public PrinterFont(int id, String name, int width, int height, boolean isDefault, int numColumns, int lineHeight,
                       FontStyle[] supportedFontStyles) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.isDefault = isDefault;
        this.numColumns = numColumns;
        this.lineHeight = lineHeight;
        this.supportedFontStyles = supportedFontStyles;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public FontStyle[] getSupportedFontStyles() {
        return supportedFontStyles;
    }

    @Override
    public String toString() {
        return name;
    }
}
