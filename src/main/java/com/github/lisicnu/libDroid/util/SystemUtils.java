package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.util.Log;

import com.github.lisicnu.log4android.LogManager;

import java.lang.reflect.Field;

/**
 * use {@link HardwareUtils} to instead.<br/>
 * or use {@link android.os.Build} to access.
 * <p/>
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
@Deprecated()
public final class SystemUtils {
    static final String TAG = SystemUtils.class.getSimpleName();
    public static final String DEVICEINFO_MODEL = "MODEL";
    public static final String DEVICEINFO_MANUFACTURER = "MANUFACTURER";
    public static final String DEVICEINFO_DISPLAY = "DISPLAY";

    /**
     * use {@link android.os.Build} to access.<br/>
     * <strong>this API will remove in 1.0</strong>
     *
     * @return
     */
    @Deprecated()
    public static String getDeviceInfo(String key) {
        String retStr = "";
        try {
            Class<android.os.Build> build_class = android.os.Build.class;
            java.lang.reflect.Field field2 = build_class.getField(key);
            retStr = (String) field2.get(new android.os.Build());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            retStr = "";
        }
        return retStr;
    }

    /**
     * use {@link android.os.Build#MODEL} to instead.<br/>
     * <strong>this API will remove in 1.0</strong>
     *
     * @return
     */
    @Deprecated()
    public static String getModule() {
        return getDeviceInfo(DEVICEINFO_MODEL);
    }

    /**
     * use {@link android.os.Build#DISPLAY} to instead.<br/>
     * <strong>this API will remove in 1.0</strong>
     *
     * @return
     */
    @Deprecated()
    public static String getDisplay() {
        return getDeviceInfo(DEVICEINFO_DISPLAY);
    }

    /**
     * use {@link android.os.Build#MANUFACTURER} to instead.<br/>
     * <strong>this API will remove in 1.0</strong>
     *
     * @return
     */
    @Deprecated()
    public static String getManufacturer() {
        return getDeviceInfo(DEVICEINFO_MANUFACTURER);
    }

    /**
     * use {@link HardwareUtils#getStatusBarHeight(android.content.Context)} to
     * instead.<br/>
     * <strong>this API will remove in 1.0</strong>
     *
     * @param context
     * @return
     */
    @Deprecated()
    public static int getStatusBarHeight(Context context) {
        if (context == null) return 0;
        Class<?> c = null;
        Object obj = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            LogManager.e(TAG, e1);
        }
        return statusBarHeight;
    }
}
