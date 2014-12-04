package com.github.lisicnu.libDroid.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.github.lisicnu.libDroid.util.MiscUtils;
import com.github.lisicnu.log4android.LogManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.github.lisicnu.libDroid.R;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = CrashHandler.class.getSimpleName();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Map<String, String> infos = new HashMap<String, String>();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private boolean allowCollectDeviceInfo = true;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (!handleException(ex) && mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
        } catch (Exception e) {
            Log.e(TAG, "error : ", e);
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        if (isShowErrorToast()) {
            MiscUtils.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, R.string.utils_crash_exitInfo,
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            });
        }

        if (isAllowCollectDeviceInfo()) {
            addVersionInfo(mContext);
            collectDeviceInfo(mContext);
        } else {
            infos.clear();
            addVersionInfo(mContext);
            infos.put("DISPLAY", Build.DISPLAY);
            infos.put("MODEL", Build.MODEL);
            infos.put("MANUFACTURER", Build.MANUFACTURER);
        }
        String errorMsg = getErrorMessage(ex);
        LogManager.e("", errorMsg);

        return true;
    }

    void addVersionInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occurred when collect package info", e);
        }
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        infos.clear();

        addVersionInfo(ctx);

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                // Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    private String getErrorMessage(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = writer.toString();
        sb.append(result);
        try {
            printWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private boolean showErrorToast = true;

    public boolean isAllowCollectDeviceInfo() {
        return allowCollectDeviceInfo;
    }

    /**
     * Enable collect device info or not. this will be used when app crash.
     *
     * @param allowCollectDeviceInfo
     */
    public void setAllowCollectDeviceInfo(boolean allowCollectDeviceInfo) {
        this.allowCollectDeviceInfo = allowCollectDeviceInfo;
    }

    /**
     * @return the showErrorToast
     */
    public boolean isShowErrorToast() {
        return showErrorToast;
    }

    /**
     * @param showErrorToast the showErrorToast to set
     */
    public void setShowErrorToast(boolean showErrorToast) {
        this.showErrorToast = showErrorToast;
    }
}
