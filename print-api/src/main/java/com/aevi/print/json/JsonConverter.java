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
package com.aevi.print.json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public final class JsonConverter {

    public static String serialize(Jsonable object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Bitmap.class, new BitmapSerialiser())
                .create();
        return gson.toJson(object);
    }

    public static JsonElement serializeToTree(Jsonable object) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Bitmap.class, new BitmapSerialiser()).create();
        return gson.toJsonTree(object);
    }

    public static <T extends Jsonable> T deserialize(String json, Class<T> type) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(JsonOption.class, new ExtrasDeserialiser())
                .registerTypeAdapter(Bitmap.class, new BitmapDeserialiser())
                .create();
        return type.cast(gson.fromJson(json, type));
    }

    private static class BitmapSerialiser implements JsonSerializer<Bitmap> {

        @Override
        public JsonElement serialize(Bitmap src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(writeBitmap(src));
        }
    }

    private static class BitmapDeserialiser implements JsonDeserializer<Bitmap> {

        @Override
        public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return readBitmap(json.getAsString());
        }
    }

    private static class ExtrasDeserialiser implements JsonDeserializer<JsonOption> {

        public JsonOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            JsonElement entry = obj.get("value");
            String className = obj.get("type").getAsString();
            try {
                Class clazz = Class.forName(className);
                return new JsonOption(context.deserialize(entry, clazz));
            } catch (ClassNotFoundException e) {
                return new JsonOption(entry, className);
            }
        }
    }

    private static String writeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 70, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    private static Bitmap readBitmap(String in) {
        byte[] pngBytes = Base64.decode(in.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.length);
    }
}
