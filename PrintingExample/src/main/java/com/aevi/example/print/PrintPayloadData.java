package com.aevi.example.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;

import com.aevi.print.model.Alignment;
import com.aevi.print.model.FontStyle;
import com.aevi.print.model.PrintPayload;
import com.aevi.print.model.PrinterFont;
import com.aevi.print.model.PrinterSettings;
import com.aevi.print.model.Underline;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PrintPayloadData {

    // TODO these should be obtained from printer settings
    private static final int CODE_PAGE_437_USA = 0;
    private static final int CODE_PAGE_737_GREEK = 29;
    private static final int CODE_PAGE_858_EURO = 19;

    private final Context context;

    public PrintPayloadData(Context context) {
        this.context = context;
    }

    public PrintPayload createTestPayLoad(PrinterSettings printerSettings, int examplePosition) {
        switch (examplePosition) {
            default:
            case 0:
                return payloadTest1(printerSettings);
            case 1:
                return payloadTest2(printerSettings);
            case 2:
                return payloadTest3(printerSettings);
            case 3:
                return payloadTest4(printerSettings);
            case 4:
                return payloadTest5(printerSettings);
            case 5:
                return payloadTest6(printerSettings);
        }
    }

    private PrintPayload payloadTest6(PrinterSettings printerSettings) {
        PrintPayload printPayload = new PrintPayload();
        printPayload.append("-----------------------");
        printPayload.append("Common currency symbols");
        printPayload.append("   US $ GBP £ EUR €   ");
        printPayload.append("-----------------------");
        return printPayload;
    }

    // Showcases the ability to set code pages and how that reflects how symbols are printed
    public PrintPayload printCodePageSymbols(int codePagePosition) {
        int codePage;
        switch (codePagePosition) {
            default:
            case 0:
                codePage = CODE_PAGE_437_USA;
                break;
            case 1:
                codePage = CODE_PAGE_858_EURO;
                break;
            case 2:
                codePage = CODE_PAGE_737_GREEK;
                break;
        }

        PrintPayload printPayload = new PrintPayload();
        printPayload.setCodePage(codePage);
        printPayload.append("----------------------");
        printPayload.append("Printing with code page: " + codePage);
        printPayload.append("Currencies: US$ GBP£ EUR€");
        printPayload.append("Swedish: ÅÄÖ");
        printPayload.append("Greek: αβγ");
        printPayload.append("----------------------");
        return printPayload;
    }

    private PrintPayload payloadTest1(PrinterSettings printerSettings) {
        // construct the printer pay load
        PrintPayload printPayload = new PrintPayload();

        // first line, hello world
        printPayload.append("Hello world!").align(Alignment.CENTER);

        // second line, the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        Date date = new Date(System.currentTimeMillis());
        printPayload.append(String.format("The time is %s", dateFormatter.format(date))).underline(Underline.DOUBLE).fontStyle(FontStyle.EMPHASIZED);

        PrinterFont[] fonts = printerSettings.getPrinterFonts();
        if (fonts != null) {
            for (PrinterFont font : fonts) {
                printPayload.append("Some text in: " + font.getName() + " cols: " + font.getNumColumns(), font);

                String tstStr = "";
                for (int i = 0; i < font.getNumColumns(); i++) {
                    if (i % 10 == 0) {
                        tstStr += "|";
                    } else {
                        tstStr += "-";
                    }
                }
                printPayload.append(tstStr, font);
            }
        }

        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bwlogotrans);
        printPayload.append(image);

        return printPayload;
    }

    private PrintPayload payloadTest2(PrinterSettings printerSettings) {
        // construct the printer pay load
        PrintPayload printPayload = new PrintPayload();

        // text lines
        printPayload.append("Align Left");
        printPayload.append("Align Right").align(Alignment.RIGHT);
        printPayload.append("Align Center").align(Alignment.CENTER);
        printPayload.append("Emphasized").fontStyle(FontStyle.EMPHASIZED);
        printPayload.append("Inverted").fontStyle(FontStyle.INVERTED);
        printPayload.appendEmptyLine();
        printPayload.append("InvertedEmphasized").fontStyle(FontStyle.INVERTED_EMPHASIZED);
        printPayload.append("Single Underlined").underline(Underline.SINGLE);
        printPayload.append("Double Underlined").underline(Underline.DOUBLE);
        printPayload.appendEmptyLine();

        // graphic lines
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.bwlogotrans);
        printPayload.append(image).align(Alignment.LEFT);
        printPayload.appendEmptyLine();
        printPayload.append(image).align(Alignment.RIGHT);
        printPayload.appendEmptyLine();
        printPayload.append(image).align(Alignment.CENTER);

        return printPayload;
    }

    private PrintPayload payloadTest3(PrinterSettings printerSettings) {
        PrintPayload payload = new PrintPayload();
        // blank line
        payload.appendEmptyLine();
        // logo
        // graphic lines
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.yourlogo);
        payload.appendEmptyLine();
        payload.append(logo).align(Alignment.CENTER);
        // blank line
        payload.appendEmptyLine();
        // contact no.
        payload.append("Phone: 1234567890").align(Alignment.CENTER);
        // address
        payload.append("Address: DP1 level6 ");
        // or manually
        payload.append("#   Name        Price ").underline(Underline.DOUBLE).align(Alignment.CENTER).fontStyle(FontStyle.EMPHASIZED);
        payload.append("1   Doughnut    $1.02 ").align(Alignment.CENTER);
        payload.append("2   Beer        $6.00 ").align(Alignment.CENTER);
        payload.appendEmptyLine();
        payload.append("    Total       $7.02 ").align(Alignment.CENTER).fontStyle(FontStyle.INVERTED_EMPHASIZED);
        payload.appendEmptyLine();
        payload.appendEmptyLine();
        payload.appendEmptyLine();
        return payload;
    }

    private PrintPayload payloadTest4(PrinterSettings printerSettings) {
        PrintPayload payload = new PrintPayload();

        payload.append("Canvas printer test").align(Alignment.CENTER);

        int canvasHeight = 200;
        int margin = 10;
        int width = paperWidthInDots(printerSettings) - 100;
        Bitmap bitmap = Bitmap.createBitmap(width, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        paint.setTextSize(50);
        paint.setTextSkewX(-0.25f);
        paint.setAntiAlias(true);

        paint.setColor(Color.BLACK);
        canvas.drawText("Aevi", 60, 80, paint);
        paint.setColor(Color.argb(255, 32, 32, 32)); // Thermal printer dark grey
        canvas.drawText("Aevi", 90, 110, paint);
        paint.setColor(Color.argb(255, 40, 40, 40)); // Thermal printer light grey
        canvas.drawText("Aevi", 120, 140, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        canvas.drawRect(margin, margin, width - margin, canvasHeight - margin, paint);
        payload.append(bitmap).align(Alignment.CENTER);

        payload.append("End canvas").align(Alignment.CENTER);

        return payload;
    }

    private PrintPayload payloadTest5(PrinterSettings printerSettings) {
        int lineWidth = paperWidthInDots(printerSettings);
        Paint paint = new Paint();
        paint.setTextSize(30);
        //create a bitmap that is higher than the text size
        float height = paint.descent() - paint.ascent();
        Bitmap myBitmap = Bitmap.createBitmap(lineWidth, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(myBitmap);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);

        //Text Left and Right Aligned
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("left", 20, height / 2 + 50, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("right", lineWidth - 20, height / 2 + 50, paint);

        //Slanted Parallelogram border on top and bottom
        paint.setColor(Color.BLACK);
        for (int i = 0; i < 22; i++) {
            Point point1Draw = new Point(10 + i * 21, 5);
            Point point2Draw = new Point(5 + i * 21, 10);
            Point point3Draw = new Point(10 + i * 21, 20);
            Point point4Draw = new Point(20 + i * 21, 10);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(point1Draw.x, point1Draw.y);
            path.lineTo(point2Draw.x, point2Draw.y);
            path.lineTo(point3Draw.x, point3Draw.y);
            path.lineTo(point4Draw.x, point4Draw.y);
            path.lineTo(point1Draw.x, point1Draw.y);
            path.close();
            canvas.drawPath(path, paint);

            path.reset();
            path.moveTo(point1Draw.x, 295 + point1Draw.y);
            path.lineTo(point2Draw.x, 295 + point2Draw.y);
            path.lineTo(point3Draw.x, 295 + point3Draw.y);
            path.lineTo(point4Draw.x, 295 + point4Draw.y);
            path.lineTo(point1Draw.x, 295 + point1Draw.y);
            path.close();
            canvas.drawPath(path, paint);
        }

        //3 circles
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("3 circles", 20, 140, paint);
        paint.setAlpha(255);
        canvas.drawCircle(165, 120, 50, paint);
        paint.setAlpha(160);
        canvas.drawCircle(265, 120, 30, paint);
        paint.setAlpha(127);
        canvas.drawCircle(325, 120, 10, paint);

        //Image and text in same line

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.bwlogotrans);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(20);
        canvas.drawText("Powered By", 20, 270, paint);
        canvas.drawBitmap(logo, 140, 230, paint);

        //Sample Receipt Text
        paint.setColor(Color.BLACK);
        canvas.save();
        paint.setAlpha(127);
        canvas.rotate(-45);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Sample Receipt", 40, 300, paint);
        canvas.restore();
        PrintPayload printPayload = new PrintPayload();
        printPayload.append(myBitmap);
        return printPayload;
    }

    private int paperWidthInDots(PrinterSettings printerSettings) {
        return (int) (printerSettings.getPaperWidth() * printerSettings.getPaperDotsPerMm());
    }
}
