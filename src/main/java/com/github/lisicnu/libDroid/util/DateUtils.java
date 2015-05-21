package com.github.lisicnu.libDroid.util;

import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class DateUtils {
    /**
     * get formated date String
     *
     * @param time
     * @param format if is null or empty, will use "yyyy-MM-dd"
     */
    public static String getShortDateString(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd";
        }

        SimpleDateFormat smpf = new SimpleDateFormat(format);
        return smpf.format(new Date(time));
    }

    /**
     * @param time
     * @param format if is null or empty, will use "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getLongDateString(long time, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat smpf = new SimpleDateFormat(format);
        return smpf.format(new Date(time));
    }

    public static final int DEFAULT_PORT = 37;//NTP服务器端口
    public static final String DEFAULT_HOST = "time-nw.nist.gov";//NTP服务器地址

    public static long syncCurrentTime() throws Exception {
        // The time protocol sets the epoch at 1900,
        // the java Date class at 1970. This number
        // converts between them.
        long differenceBetweenEpochs = 2208988800L;

        // If you'd rather not use the magic number uncomment
        // the following section which calculates it directly.

        /*
         * TimeZone gmt = TimeZone.getTimeZone("GMT"); Calendar epoch1900 =
         * Calendar.getInstance(gmt); epoch1900.set(1900, 01, 01, 00, 00, 00);
         * long epoch1900ms = epoch1900.getTime().getTime(); Calendar epoch1970
         * = Calendar.getInstance(gmt); epoch1970.set(1970, 01, 01, 00, 00, 00);
         * long epoch1970ms = epoch1970.getTime().getTime();
         *
         * long differenceInMS = epoch1970ms - epoch1900ms; long
         * differenceBetweenEpochs = differenceInMS/1000;
         */

        InputStream raw = null;
        Socket theSocket = null;
        try {
            theSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            raw = theSocket.getInputStream();

            long secondsSince1900 = 0;
            for (int i = 0; i < 4; i++) {
                secondsSince1900 = (secondsSince1900 << 8) | raw.read();
            }

            long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
            return secondsSince1970 * 1000;
        } finally {
            if (raw != null) raw.close();
            if (theSocket != null) theSocket.close();
        }
    }
}
