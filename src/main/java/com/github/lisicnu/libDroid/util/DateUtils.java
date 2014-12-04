package com.github.lisicnu.libDroid.util;

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
}
