package com.github.lisicnu.libDroid.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class StringUtils {

    /**
     * get random string with a-z,A-Z, 0-9
     *
     * @param length
     * @return
     */
    public static String getRandomStr(int length) {
        String str = "abcdefghigklmnopkrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int mLen = str.length();
        StringBuffer sf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(mLen);// 0~61
            sf.append(str.charAt(number));
        }
        return sf.toString();
    }

    /**
     * 进行分割, 不支持正则式.
     *
     * @param source
     * @param separator
     * @param removeEmpty
     * @return
     */
    public static ArrayList<String> split(String source, String separator, boolean removeEmpty) {
        if (source == null || source.isEmpty())
            return null;

        ArrayList<String> values = new ArrayList<String>();
        if (separator == null || separator.isEmpty()) {
            values.add(source);
            return values;
        }

        String tmpStr = new String(source);

        int idx = 0;
        String tt;
        while (true) {
            int tmp = tmpStr.indexOf(separator, idx);
            if (tmp == -1) {
                tt = tmpStr.substring(idx);

                if (tt != null && !tt.isEmpty())
                    values.add(tmpStr.substring(idx));

                break;
            }

            tt = tmpStr.substring(idx, tmp);
            if (tt != null && !tt.isEmpty())
                values.add(tmpStr.substring(idx, tmp));

            idx = tmp + separator.length();
        }

        return values;
    }

    /**
     * use {@link android.text.TextUtils#isEmpty(CharSequence)} instead.
     *
     * @param str
     * @return
     */
    @Deprecated
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String trimEnd(String str) {
        char[] value = str.toCharArray();
        int start = 0, last = value.length - 1;
        int end = last;

        while (end >= start) {
            if (value[end] >= ' ')
                break;
            end--;
        }

        return new String(value, start, end - start + 1);
    }

    public static String trimStart(String str) {
        char[] value = str.toCharArray();
        int start = 0, last = value.length - 1;
        int end = last;

        while (start <= end) {
            if (value[start] >= ' ') {
                break;
            }
            start++;
        }
        return new String(value, start, end - start + 1);

    }

    public static String combine(String[] items, String seprator) {
        return combine(Arrays.asList(items), seprator);
    }

    public static String combine(Iterable<String> items, String seprator) {
        if (items == null)
            return "";
        if (seprator == null)
            seprator = "";

        StringBuffer buffer = new StringBuffer();

        for (Iterator<String> iterator = items.iterator(); iterator.hasNext(); ) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(seprator);
            }
        }

        return buffer.toString();
    }

    /**
     * e.g. str1= "aaaa/", str2= "/bbb" ,separator="/" will return "aaaa/bbb"
     *
     * @param str1
     * @param str2
     * @param separator
     * @return
     */
    public static String combine(String str1, String str2, String separator) {

        if (separator == null || separator.isEmpty()) {
            return str1 == null ? str2 : str1.concat(str2);
        }
        if (str1 == null)
            str1 = "";
        if (str2 == null)
            str2 = "";

        StringBuilder builder = new StringBuilder();
        if (str1.endsWith(separator)) {
            builder.append(str1.substring(0, str1.length() - separator.length()));
        } else {
            builder.append(str1);
        }
        builder.append(separator);

        if (str2.startsWith(separator)) {
            builder.append(str2.substring(separator.length()));
        } else {
            builder.append(str2);
        }

        return builder.toString();
    }
}
