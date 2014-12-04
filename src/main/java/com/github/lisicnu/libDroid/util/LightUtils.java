package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.os.PowerManager;

/**
 * need Permission "android.permission.WAKE_LOCK"
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class LightUtils {
    private final static String TAG = LightUtils.class.getSimpleName();
    static PowerManager powerManager;
    static PowerManager.WakeLock wl;

    /**
     * if the keep light has been opened, will default exit.
     *
     * @param context
     */
    public static boolean acquireLock(Context context) {

        if (wl != null && wl.isHeld()) {
            return true;
        }

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        if (wl == null) {
            return false;
        }

        wl.acquire();

        return wl.isHeld();
    }

    /**
     * if didn't request for keeps light, this will auto-return.
     *
     * @param context
     */

    public static void releaseLock(Context context) {
        if (wl == null) {
            return;
        }
        wl.release();
        wl = null;
    }

}
