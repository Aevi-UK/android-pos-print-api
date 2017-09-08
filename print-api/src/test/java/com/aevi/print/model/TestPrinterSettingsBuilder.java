package com.aevi.print.model;

import java.util.Map;

public class TestPrinterSettingsBuilder {

    private String printerId;
    private int paperWidth;
    private PrinterFont[] printerFonts;
    private PaperKind paperKind;
    private int[] codePages;
    private String[] commands;
    private Map<String, String> options;
    private boolean doesReportStatus = false;
    private boolean canHandleCommands = false;
    private boolean doesSupportCodepages = false;
    private String[] supportedLanguages;

    public TestPrinterSettingsBuilder withPrinterId(String printerId) {
        this.printerId = printerId;
        return this;
    }

    public TestPrinterSettingsBuilder withPaperWidth(int paperWidth) {
        this.paperWidth = paperWidth;
        return this;
    }

    public TestPrinterSettingsBuilder withPrinterFonts(PrinterFont[] printerFonts) {
        this.printerFonts = printerFonts;
        return this;
    }

    public TestPrinterSettingsBuilder withPaperKind(PaperKind paperKind) {
        this.paperKind = paperKind;
        return this;
    }

    public TestPrinterSettingsBuilder withCodePages(int[] codePages) {
        this.codePages = codePages;
        return this;
    }

    public TestPrinterSettingsBuilder withDoesSupportCodepages(boolean doesSupportCodepages) {
        this.doesSupportCodepages = doesSupportCodepages;
        return this;
    }

    public TestPrinterSettingsBuilder withCommands(String[] commands) {
        this.commands = commands;
        return this;
    }

    public TestPrinterSettingsBuilder withOptions(Map<String, String> options) {
        this.options = options;
        return this;
    }

    public TestPrinterSettingsBuilder withCanHandleCommands(boolean canHandleCommands) {
        this.canHandleCommands = canHandleCommands;
        return this;
    }

    public TestPrinterSettingsBuilder withDoesReportStatus(boolean doesReportStatus) {
        this.doesReportStatus = doesReportStatus;
        return this;
    }

    public TestPrinterSettingsBuilder withSupportedLanguages(String[] supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
        return this;
    }

    public PrinterSettings build() {
        return new PrinterSettings(printerId, paperWidth, paperKind, printerFonts, canHandleCommands, commands, doesReportStatus, codePages,
                doesSupportCodepages, options, supportedLanguages);
    }
}
