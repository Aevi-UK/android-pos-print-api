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

import com.aevi.android.rxmessenger.JsonConverter;
import com.aevi.android.rxmessenger.SendableId;

class PrinterSettingsRequest extends SendableId {

    private static final String ALL = "all";
    private static final String DEFAULT = "default";

    private final String type;

    private PrinterSettingsRequest(String type) {
        this.type = type;
    }

    public boolean isAllRequest() {
        return type != null && type.equals(ALL);
    }

    public boolean isDefaultRequest() {
        return type != null && type.equals(DEFAULT);
    }

    @Override
    public String toJson() {
        return JsonConverter.serialize(this);
    }

    public static PrinterSettingsRequest fromJson(String json) {
        return JsonConverter.deserialize(json, PrinterSettingsRequest.class);
    }

    public static PrinterSettingsRequest createAllRequest() {
        return new PrinterSettingsRequest(ALL);
    }

    public static PrinterSettingsRequest createDefaultRequest() {
        return new PrinterSettingsRequest(DEFAULT);
    }
}
