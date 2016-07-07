package com.tomclaw.drawa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by ivsolkin on 06.07.16.
 */
public class HexHelper {

    private static final int LINE_SIZE = 16;

    public static void convertToHex(PrintStream out, File myFileReader) throws IOException {
        InputStream is = new FileInputStream(myFileReader);

        int value, bytesCounter = 0;

        StringBuilder sbHex = new StringBuilder();
        StringBuilder sbText = new StringBuilder();
        StringBuilder sbResult = new StringBuilder();

        while ((value = is.read()) != -1) {
            sbHex.append(String.format("%02X ", value));
            if (!Character.isISOControl(value)) {
                sbText.append((char) value);
            } else {
                sbText.append(".");
            }
            if (bytesCounter == LINE_SIZE - 1) {
                sbResult.append(sbHex).append("      ").append(sbText).append("\n");
                sbHex.setLength(0);
                sbText.setLength(0);
                bytesCounter = 0;
            } else {
                bytesCounter++;
            }
        }

        if (bytesCounter != 0) {
            for (; bytesCounter < LINE_SIZE; bytesCounter++) {
                sbHex.append("   ");
            }
            sbResult.append(sbHex).append("      ").append(sbText).append("\n");
        }

        out.print(sbResult);
        is.close();
    }
}
