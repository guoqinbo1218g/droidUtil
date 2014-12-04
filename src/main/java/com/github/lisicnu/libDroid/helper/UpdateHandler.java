package com.github.lisicnu.libDroid.helper;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class UpdateHandler {

    final static String TAG = UpdateHandler.class.getSimpleName();

    public static final long DEFAULT_UPDATE_TIME = 1000;

    private long updateTime = DEFAULT_UPDATE_TIME;
    private final static int REFRESH = 0x333;
    private final List<onUpdateListener> objList = new ArrayList<onUpdateListener>();
    private boolean exit = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH: {
                    synchronized (objList) {
                        for (Iterator<onUpdateListener> ite = objList.iterator(); ite.hasNext(); ) {
                            final onUpdateListener tmp = ite.next();
                            if (tmp == null || exit)
                                break;

                            tmp.onUpdate();
                        }
                    }

                    sendEmptyMessageDelayed(msg.what, updateTime);
                }
                break;
            }
        }

    };

    /**
     * @param v 需实现 onUpdateListener 接口, 如果没实现此接口, 将返回失败
     * @see onUpdateListener
     */
    public boolean add(onUpdateListener v) {
        if (v == null) {
            return false;
        }

        synchronized (objList) {
            if (!objList.contains(v)) {
                objList.add(v);
            }
        }
        return true;
    }

    public void remove(onUpdateListener v) {
        synchronized (objList) {
            if (objList.contains(v)) {
                objList.remove(v);
            }
        }
    }

    public void setUpdateTime(long mils) {
        if (mils < 0) return;
        updateTime = mils;
    }

    public void start() {
        exit = false;
        handler.removeMessages(REFRESH);
        handler.sendEmptyMessage(REFRESH);
    }

    public void stop() {
        synchronized (objList) {
            objList.clear();
        }

        handler.removeMessages(REFRESH);
        exit = true;
    }

    public static interface onUpdateListener {
        void onUpdate();
    }

}
