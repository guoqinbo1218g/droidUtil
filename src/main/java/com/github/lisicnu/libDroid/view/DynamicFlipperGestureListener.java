package com.github.lisicnu.libDroid.view;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class DynamicFlipperGestureListener extends SimpleOnGestureListener {

    public interface OnFlingListener {
        /**
         * fling current item to next.
         */
        void flingToNext();

        /**
         * fling current item to previous.
         */
        void flingToPrevious();
    }

    private OnFlingListener mOnFlingListener;

    public OnFlingListener getOnFlingListener() {
        return mOnFlingListener;
    }

    public void setOnFlingListener(OnFlingListener mOnFlingListener) {
        this.mOnFlingListener = mOnFlingListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    final static float CRITICAL_LEN = 100f, CRITICAL_SPEED = 100f;

    @Override
    public final boolean onFling(final MotionEvent e1, final MotionEvent e2, final float speedX,
                                 final float speedY) {
        if (mOnFlingListener == null) {
            return super.onFling(e1, e2, speedX, speedY);
        }

        float XFrom = e1.getX();
        float XTo = e2.getX();
        float YFrom = e1.getY();
        float YTo = e2.getY();

        // 左右滑动的X轴幅度大于100，并且X轴方向的速度大于100
        if (Math.abs(XFrom - XTo) > CRITICAL_LEN && Math.abs(speedX) > CRITICAL_SPEED) {
            // X轴幅度大于Y轴的幅度
            if (Math.abs(XFrom - XTo) >= Math.abs(YFrom - YTo)) {
                if (XFrom < XTo) {
                    // 下一个
                    mOnFlingListener.flingToNext();
                } else {
                    // 上一个
                    mOnFlingListener.flingToPrevious();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
