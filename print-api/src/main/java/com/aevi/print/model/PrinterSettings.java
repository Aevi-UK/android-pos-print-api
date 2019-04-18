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

import com.aevi.util.json.JsonConverter;
import com.aevi.util.json.Jsonable;

import java.util.Map;

import static com.aevi.print.util.Preconditions.checkNotNull;

/**
 * Contains information such as name, DPI and paper width for a specific PrinterSettings on the Device.
 */
public class PrinterSettings implements Jsonable {

    public static final String OPTION_DEFAULT = "default";

    private final String printerId;
    private final int paperWidth;
    private final int printableWidth;
    private final float paperDotsPmm;
    private final PaperKind paperKind;
    private final String[] commands;
    private final int[] codepages;
    private final Map<String, String> options;
    private final boolean canHandleCommands;
    private final boolean doesReportStatus;
    private final boolean doesSupportCodepages;
    private final String[] supportedLanguages;

    private final PrinterFont[] printerFonts;
    private String displayName;

    PrinterSettings(String printerId, int paperWidth, int printableWidth, float paperDotsPmm,
                    PaperKind paperKind, PrinterFont[] printerFonts,
                    boolean canHandleCommands, String[] commands,
                    boolean doesReportStatus,
                    int[] codepages, boolean doesSupportCodepages,
                    Map<String, String> options,
                    String[] supportedLanguages) {



        this.printerId = checkNotNull(printerId, "printerId must not be null") ;
        this.paperKind = checkNotNull(paperKind, "paperKind must not be null");

        this.paperWidth = paperWidth;
        this.printableWidth = printableWidth;
        this.paperDotsPmm = paperDotsPmm;
        this.commands = commands;
        this.codepages = codepages;
        this.options = options;
        this.canHandleCommands = canHandleCommands;
        this.doesReportStatus = doesReportStatus;
        this.doesSupportCodepages = doesSupportCodepages;
        this.printerFonts = printerFonts;
        this.supportedLanguages = supportedLanguages;
    }

    /**
     * Gets the unique ID of this printer.
     *
     * @return the ID of the printer, must be unique.
     */
    public String getPrinterId() {
        return printerId;
    }

    /**
     * Gets the printer name that will be displayed to the users. If this has not been set the printer ID will be used
     *
     * @return the display name of the printer.
     */
    public String getDisplayName() {
        if (displayName== null || displayName.isEmpty()) {
            return printerId;
        }
        return displayName;
    }

    /**
     * Sets the printer name that will be displayed to the users.
     *
     * @param displayName The name of the printer to be shown to the user. This parameter must not be null.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the width of the paper in mm.
     *
     * @return the width of the paper in mm.
     */
    public int getPaperWidth() {
        return paperWidth;
    }

    /**
     * Gets the actual available printing width in mm. This is usually the width of the paper minus any left/right margins
     *
     * @return The available printing width in mm
     */
    public int getPrintableWidth() {
        return printableWidth;
    }

    /**
     * Gets the resolution of the printer in dots per mm (dpmm).
     *
     * @return the dots per mm this printer has for the given paper size above.
     */
    public float getPaperDotsPerMm() {
        return paperDotsPmm;
    }

    /**
     * Gets the kind of paper this printer prints on.
     *
     * @return the kind of paper this printer prints on.
     */
    public PaperKind getPaperKind() {
        return paperKind;
    }

    /**
     * Gets a list of fonts this printer supports
     *
     * @return A List of printer fonts
     */
    public PrinterFont[] getPrinterFonts() {
        return printerFonts;
    }

    /**
     * A list of commands this printer can execute
     *
     * @return A list of string commands that can be sent to this printer to perform printer specific functionality
     */
    public String[] getCommands() {
        return commands;
    }

    /**
     * A list of codepages supported by this printer
     *
     * @return A list of codepage ids that can be used with this printer. See printer specific documentation for details
     */
    public int[] getCodepages() {
        return codepages;
    }

    /**
     * @return True if this printer makes use of codepages which will be given in the list {@link #getCodepages()}
     */
    public boolean doesSupportCodePages() {
        return doesSupportCodepages;
    }

    /**
     * A map of key/value pair options that have been setup for this printer
     *
     * @return Map of printer specific key value pair settings
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * @return True if this printer can handle commands/actions sent via {@link com.aevi.print.PrinterManager#sendAction(String, String)}
     */
    public boolean canHandleCommands() {
        return canHandleCommands;
    }

    /**
     * @return True if this printer will report its status to {@link com.aevi.print.PrinterManager#status(String)}
     */
    public boolean doesReportPrinterStatus() {
        return doesReportStatus;
    }

    /**
     * Returns a list of two letter (ISO 639) language codes that are supported by this printer
     *
     * @return A list of two letter ISO 639 codes
     */
    public String[] getSupportedLanguages() {
        return supportedLanguages;
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static PrinterSettings fromJson(String json) {
        return JsonConverter.deserialize(json, PrinterSettings.class);
    }

}

