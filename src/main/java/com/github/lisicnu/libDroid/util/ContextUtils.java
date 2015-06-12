package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class ContextUtils {

    final static String TAG = ContextUtils.class.getSimpleName();
    public static final int COPY_SUCCESS = 0;
    public static final int COPY_DST_EXIST = 1;
    public static final int COPY_NO_ENOUGH_STORAGE = 2;
    public static final int COPY_ERROR = 3;

    /**
     * copy assets file to destinations.
     *
     * @param context
     * @param srcFile source file in assets.
     * @param dstFile destination files, full path with folder. if folder is empty,
     *                means copy to context's files folder.
     * @return {@link #COPY_SUCCESS} means success.{@link #COPY_DST_EXIST},
     * {@link #COPY_ERROR}
     */
    public static int CopyAssets(Context context, String srcFile, String dstFile) {

        int result = COPY_SUCCESS;
        InputStream in = null;
        OutputStream out = null;
        try {

            File fi;
            if (dstFile.equals(FileUtils.getFileName(dstFile))) {
                fi = new File(context.getFilesDir(), dstFile);
            } else {
                fi = new File(dstFile);
            }

            in = context.getAssets().open(srcFile, AssetManager.ACCESS_STREAMING);

            if (fi.exists() && in.available() <= fi.length()) {
                result = COPY_DST_EXIST;
            } else {
                out = new FileOutputStream(fi);
                byte[] buffer = new byte[10240];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                buffer = null;
            }

        } catch (IOException e) {
            Log.e(TAG, "", e);

            result = COPY_ERROR;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            in = null;
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            out = null;
        }

        return result;
    }

    /**
     * check whether is launcher or not.
     *
     * @param intent
     * @return
     */
    public static boolean isLauncherActivity(Intent intent) {
        if (intent == null)
            return false;

        if (StringUtils.isNullOrEmpty(intent.getAction())) {
            return false;
        }

        if (intent.getCategories() == null)
            return false;

        return Intent.ACTION_MAIN.equals(intent.getAction())
                && intent.getCategories().contains(Intent.CATEGORY_LAUNCHER);
    }

}
