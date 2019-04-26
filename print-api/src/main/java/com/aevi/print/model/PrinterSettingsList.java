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

public class PrinterSettingsList implements Jsonable {

    private final PrinterSettings[] printerSettings;

    public PrinterSettingsList(PrinterSettings[] printerSettings) {
        this.printerSettings = printerSettings;
    }

    public PrinterSettings[] getPrinterSettings() {
        return printerSettings;
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static PrinterSettingsList fromJson(String json) {
        return JsonConverter.deserialize(json, PrinterSettingsList.class);
    }
}
