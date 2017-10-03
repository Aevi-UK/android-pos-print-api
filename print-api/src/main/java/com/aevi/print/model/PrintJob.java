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

/**
 * Wrapper class to encapsulate the PrintJob
 */
public class PrintJob extends SendableId {

    public enum State {

        /**
         * The print job is in the print queue.
         */
        IN_PROGRESS,

        /**
         * The print job has been completed.
         */
        PRINTED,

        /**
         * The print job failed printing.
         */
        FAILED
    }

    private final State printJobState;
    private String failedReason;
    private String diagnosticMessage;

    /**
     * Construct a new PrintJob based on the number of the print job.
     *
     * @param printJobState The state of the printJob
     */
    public PrintJob(State printJobState) {
        this.printJobState = printJobState;
    }

    /**
     * Construct a new PrintJob based on the number of the print job.
     *
     * @param printJobState The state of the printJob
     * @param failedReason  The reason giving the cause of any failure
     */
    public PrintJob(State printJobState, String failedReason) {
        this(printJobState);
        this.failedReason = failedReason;
    }

    /**
     * Construct a new PrintJob based on the number of the print job.
     *
     * @param printJobState     The state of the printJob
     * @param failedReason      The reason giving the cause of any failure
     * @param diagnosticMessage A diagnostic message to include with the state
     */
    public PrintJob(State printJobState, String failedReason, String diagnosticMessage) {
        this(printJobState);
        this.failedReason = failedReason;
        this.diagnosticMessage = diagnosticMessage;
    }

    /**
     * @return The reason giving the cause of any failure {@link PrinterMessages}
     */
    public String getFailedReason() {
        if (failedReason == null) {
            return "";
        }
        return failedReason;
    }

    /**
     * @return The message (if any) that was provided along with the state of the print job
     */
    public String getDiagnosticMessage() {
        return diagnosticMessage;
    }

    /**
     * @return The state of this print job
     */
    public State getPrintJobState() {
        return printJobState;
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static PrintJob fromJson(String json) {
        return JsonConverter.deserialize(json, PrintJob.class);
    }
}
