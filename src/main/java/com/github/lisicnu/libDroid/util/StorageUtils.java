package com.github.lisicnu.libDroid.util;

import android.os.Environment;
import android.os.StatFs;

import com.github.lisicnu.log4android.LogManager;

import java.io.File;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class StorageUtils {
    private static final String TAG = StorageUtils.class.getSimpleName();

    // Match the code in MediaProvider.computeBucketValues().
    public static final long UNAVAILABLE = -1L;
    public static final long PREPARING = -2L;
    public static final long UNKNOWN_SIZE = -3L;

    public static long getAvailableSpace() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_CHECKING.equals(state)) {
            return PREPARING;
        }
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return UNAVAILABLE;
        }
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir.mkdirs();
        if (!dir.isDirectory() || !dir.canWrite()) {
            return UNAVAILABLE;
        }

        try {
            StatFs stat = new StatFs(dir.toString());
            return stat.getAvailableBlocks() * (long) stat.getBlockSize();
        } catch (Exception e) {
            LogManager.i(TAG, "Fail to access external storage," + e.toString());
        }
        return UNKNOWN_SIZE;
    }
}
