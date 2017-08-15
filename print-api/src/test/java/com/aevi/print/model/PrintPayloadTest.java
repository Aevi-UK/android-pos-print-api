package com.aevi.print.model;

import android.graphics.Bitmap;
import android.os.Build;

import com.aevi.print.model.ImageRow;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.TextRow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricTestRunner.class)
public class PrintPayloadTest {

    @Test
    public void willSetPrinterId() {
        PrintPayload payload = new PrintPayload("17676767");

        assertThat(payload.getPrinterId()).isEqualTo("17676767");
    }

    @Test
    public void checkAlignLeftRight() {
        PrintPayload payload = new PrintPayload();

        TextRow row = payload.appendLeftRight(30, "Left", "Right");

        assertThat(row.getText()).isEqualTo("Left                     Right");
    }

    @Test
    public void checkAppendLineOfChar() {
        PrintPayload payload = new PrintPayload();

        TextRow row = payload.appendLineOfChar(30, "=");

        assertThat(row.getText()).isEqualTo("==============================");
    }

    @Test
    public void canAddText() {
        PrintPayload payload = new PrintPayload();
        assertThat(payload.getRows()).hasSize(0);

        TextRow row1 = payload.append("Hello");
        payload.append("World");

        assertThat(row1).isNotNull();
        assertThat(payload.getRows()).hasSize(2);
    }

    @Test
    public void canAddImage() {
        PrintPayload payload = new PrintPayload();
        assertThat(payload.getRows()).hasSize(0);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);

        ImageRow imageRow = payload.append(bmp);

        assertThat(imageRow).isNotNull();
        assertThat(payload.getRows()).hasSize(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendNullWillThrow() {
        PrintPayload payload = new PrintPayload();

        payload.append((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendNullImageWillThrow() {
        PrintPayload payload = new PrintPayload();

        payload.append((Bitmap) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendNullPayloadWillThrow() {
        PrintPayload payload = new PrintPayload();

        payload.append((PrintPayload) null);
    }

    @Test
    public void canAppendAnotherPayload() {
        PrintPayload payload1 = new PrintPayload();
        assertThat(payload1.getRows()).hasSize(0);

        PrintPayload payload2 = new PrintPayload();
        payload2.append("Hello");
        payload2.append("World");

        payload1.append(payload2);

        assertThat(payload1.getRows()).hasSize(2);
    }

    @Test
    public void canSetCodepage() {
        PrintPayload payload = new PrintPayload();

        payload.setCodePage(23);

        assertThat(payload.getCodePage()).isEqualTo(23);
    }

    @Test
    public void canSerialise() {
        PrintPayload payload = new PrintPayload();
        payload.append("Hello");
        payload.append("World");

        String json = payload.toJson();
        assertThat(json).isEqualTo(
                "{\"rows\":[" +
                        "{\"value\":" +
                        "{\"text\":\"Hello\",\"underline\":\"NONE\",\"fontStyle\":\"NORMAL\",\"alignment\":\"LEFT\"},\"type\":\"com.aevi.print.model.TextRow\"}," +
                        "{\"value\":" +
                        "{\"text\":\"World\",\"underline\":\"NONE\",\"fontStyle\":\"NORMAL\",\"alignment\":\"LEFT\"},\"type\":\"com.aevi.print.model.TextRow\"}]," +
                        "\"codePage\":0,\"id\":\"" + payload.getId() + "\"}");
    }
}
