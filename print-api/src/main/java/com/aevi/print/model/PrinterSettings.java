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
import com.aevi.android.rxmessenger.SendableId;

import java.util.Map;

/**
 * Contains information such as name, DPI and paper width for a specific PrinterSettings on the Device.
 */
public class PrinterSettings extends SendableId {

    public static final String OPTION_DEFAULT = "default";

    private final String printerId;
    private final int paperWidth;
    private final PaperKind paperKind;
    private final String[] commands;
    private final int[] codepages;
    private final Map<String, String> options;
    private final boolean canHandleCommands;
    private final boolean doesReportStatus;
    private final boolean doesSupportCodepages;
    private final String[] supportedLanguages;

    private final PrinterFont[] printerFonts;

    PrinterSettings(String printerId, int paperWidth, PaperKind paperKind, PrinterFont[] printerFonts, boolean canHandleCommands, String[] commands,
                    boolean doesReportStatus, int[] codepages, boolean doesSupportCodepages, Map<String, String> options,
                    String[] supportedLanguages) {

        if (printerId == null) {
            throw new IllegalArgumentException("printerId must not be null");
        }

        if (paperKind == null) {
            throw new IllegalArgumentException("paperKind must not be null");
        }

        this.printerId = printerId;
        this.paperWidth = paperWidth;
        this.paperKind = paperKind;
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
     * Gets the unique Id of this printer.
     *
     * @return the id of the printer, must be unique.
     */
    public String getPrinterId() {
        return printerId;
    }

    /**
     * Gets the width of the paper in dots.
     *
     * @return the width of the paper in dots.
     */
    public int getPaperWidth() {
        return paperWidth;
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

