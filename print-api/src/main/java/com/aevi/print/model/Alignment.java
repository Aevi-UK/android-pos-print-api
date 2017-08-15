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

/**
 * The text-align enum specifies the horizontal alignment of a line of text in a {@link PrintPayload}.
 */
public enum Alignment {
    /**
     * Aligns the text to the left
     */
    LEFT,

    /**
     * Aligns the text to the right
     */
    RIGHT,

    /**
     * Centers the text
     */
    CENTER,
}
