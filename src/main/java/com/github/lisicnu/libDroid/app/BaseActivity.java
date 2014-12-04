package com.github.lisicnu.libDroid.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.github.lisicnu.libDroid.R;
import com.github.lisicnu.libDroid.util.ContextUtils;
import com.github.lisicnu.libDroid.util.StringUtils;
import com.github.lisicnu.log4android.LogManager;

import java.lang.ref.WeakReference;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public abstract class BaseActivity extends Activity {

    public final static int MSG_SHOW_TOAST = 0x9899;
    /**
     * enable views or not.<br/>
     * <br/>
     * Note: if enabled, set arg1 = 0, otherwise set arg1 != 0.
     */
    public final static int MSG_ENABLE_VIEWS = 0x9898;
    public final static long DEFAULT_DELAY_TIME = 300l;
    private final Handler mHandler = new InnerHandler(new WeakReference<BaseActivity>(this));
    AlertDialog.Builder mBuilder = null;
    AlertDialog exitDialog;

    private ExitMode exitMode = ExitMode.Default;

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * this is for handler handle message.
     *
     * @param msg
     */
    public abstract void handleHandlerMessage(Message msg);

    /**
     * when some views can't be clicked at same time or too fast, use this
     * method. should override {@link #setViewsEnability(boolean)}
     */
    public void delayEnableViews() {
        Message msg = Message.obtain(mHandler, MSG_ENABLE_VIEWS, 1, 0);
        if (msg != null)
            msg.sendToTarget();

        msg = Message.obtain(mHandler, MSG_ENABLE_VIEWS, 0, 0);
        if (msg != null)
            mHandler.sendMessageDelayed(msg, DEFAULT_DELAY_TIME);
    }

    /**
     * enable views or not
     *
     * @param enabled enable or not.
     */
    public abstract void setViewsEnability(boolean enabled);

    public void showToast(int resId) {
        showToast(getString(resId));
    }

    /**
     * show message, delay = 0, removeFront = true
     *
     * @param msgToShow
     */
    public void showToast(String msgToShow) {
        showToast(msgToShow, 0, true);
    }

    /**
     * show message, delay = 0
     *
     * @param msgToShow
     */
    public void showToast(String msgToShow, boolean removeFront) {
        showToast(msgToShow, 0, removeFront);
    }

    public void showToast(String msgToShow, long delayTime, boolean removeFront) {
        if (removeFront)
            mHandler.removeMessages(MSG_SHOW_TOAST);

        Message msg = Message.obtain(mHandler, MSG_SHOW_TOAST, msgToShow);
        if (delayTime > 0) {
            mHandler.sendMessageDelayed(msg, delayTime);
        } else {
            msg.sendToTarget();
        }
    }

    /**
     * show message, delay = {@link #DEFAULT_DELAY_TIME}, removeFront = true
     *
     * @param msgToShow
     */
    public void showToastDelay(String msgToShow) {
        showToast(msgToShow, DEFAULT_DELAY_TIME, true);
    }

    protected void setExitBuilder(AlertDialog.Builder builder) {
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss();
            exitDialog = null;
        }
        mBuilder = builder;
    }

    public void showExitDlg() {
        if (exitDialog != null && exitDialog.isShowing()) {
            return;
        }

        if (exitDialog == null) {
            if (mBuilder == null) {
                mBuilder = new AlertDialog.Builder(BaseActivity.this);
                mBuilder.setIcon(android.R.drawable.ic_dialog_info);
                mBuilder.setTitle(R.string.utils_exit_title);
                mBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                finish();
                            }
                        }
                );

                mBuilder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );

                exitDialog = mBuilder.create();
            } else {
                exitDialog = mBuilder.create();
            }
        }
        exitDialog.show();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (getExitMode() == ExitMode.Default) {
            super.onBackPressed();
        } else if (getExitMode() == ExitMode.DoubleClick) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = true;
                }
            }, 2000);
        } else if (getExitMode() == ExitMode.Dialog) {
            showExitDlg();
        }
    }

    public ExitMode getExitMode() {
        return exitMode;
    }

    /**
     * set Exit mode.
     *
     * @param exitMode
     */
    public void setExitMode(ExitMode exitMode) {
        this.exitMode = exitMode;
    }


    private void log(String msg) {
        LogManager.d("lifeStatus", getClass().getSimpleName() + "  " + msg);
    }

    /**
     * 配置日志文件信息, 如果需要程序自己配置, 覆盖这个方法, 如果不设置此方法 直接调用, 则会输出到LOGCAT 中.
     */
    protected void configLog() {
        LogManager.init(getApplicationContext());
        LogManager.getLogger().setClientID(getPackageName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ContextUtils.isLauncherActivity(getIntent())) {
            configLog();
            setExitMode(ExitMode.DoubleClick);
        } else {
            setExitMode(ExitMode.Default);
        }

        super.onCreate(savedInstanceState);

        log("onCreate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        log("onSaveInstanceState");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        log("onConfigurationChanged");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log("onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        log("onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        log("onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        log("onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        log("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        log("onStop");
        super.onStop();
    }

    private class InnerHandler extends Handler {
        WeakReference<BaseActivity> target;

        private InnerHandler(WeakReference<BaseActivity> target) {
            this.target = target;
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity tmp = target.get();
            if (tmp == null)
                return;

            switch (msg.what) {
                case MSG_SHOW_TOAST:
                    if (msg.obj != null && !StringUtils.isNullOrEmpty(msg.obj.toString())) {
                        Toast.makeText(tmp, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_ENABLE_VIEWS:
                    tmp.setViewsEnability(msg.arg1 == 0);
                    break;
                default:
                    tmp.handleHandlerMessage(msg);
                    break;
            }
        }
    }
}
