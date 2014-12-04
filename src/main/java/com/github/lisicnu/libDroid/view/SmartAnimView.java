package com.github.lisicnu.libDroid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.github.lisicnu.libDroid.util.BitmapUtils;

/**
 * 简单动画VIEW, 原理为 一组图片的轮询, 可设置轮询的间隔时间, 见{@link #setRefreshTime}. 默认不开始动画, 当设置了
 * {@code setAnimRes} 之后,需主动调用 {@code start} 方法, 才能开始动画
 * <p/>
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class SmartAnimView extends View {

    public interface SmartAnimationListener {
        void onSmartAnimationStart(int startIndex);

        void onSmartAnimationRepeat(int times);

        void onSmartAnimationEnd();
    }

    public SmartAnimView(Context context) {
        super(context);
    }

    public SmartAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SmartAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    boolean pause = true;

    public void pause() {
        this.pause = true;
    }

    /**
     * set repeated and target repeat to 0.
     */
    public void reset() {
        drawedCount = 0;
        repeatTimes = 0;
        mRepeated = 0;
    }

    private SmartAnimationListener smartAnimationListener;

    public int getRepeatTimes() {
        return repeatTimes;
    }

    /**
     * @param repeatTimes repeat times, less than or equal 0, means loop.
     */
    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    /**
     * target repeat times.
     */
    int repeatTimes = 0;

    /**
     * repeated count
     */
    int mRepeated = 0;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    public void start() {
        pause = false;
        invalidate();

        onSmartAnimationStart(drawedCount);
    }

    public void stop() {
        pause = true;
        drawedCount = 0;
    }

    void onSmartAnimationStart(int count) {
        if (getSmartAnimationListener() != null) {
            getSmartAnimationListener().onSmartAnimationStart(count);
        }
    }

    void onSmartAnimationRepeat(int times) {
        if (getSmartAnimationListener() != null)
            getSmartAnimationListener().onSmartAnimationRepeat(times);
    }

    void onSmartAnimationEnd() {
        if (getSmartAnimationListener() != null)
            getSmartAnimationListener().onSmartAnimationEnd();
    }

    /**
     * 轮询时间间隔, 默认100ms
     *
     * @return
     */
    public int getRefreshTime() {
        return refreshTime;
    }

    /**
     * 轮询时间间隔, 默认100ms
     *
     * @param refreshTime
     */
    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    /**
     * refresh time space.
     */
    private int refreshTime = 100;

    private void sendInvalidate() {
        postDelayed(new Runnable() {

            public void run() {
                invalidate();
            }
        }, getRefreshTime());
    }

    private int drawedCount = 0;

    private Bitmap[] bmpAry = null;

    private int[] animRes;

    /**
     * @param animRes
     */
    public void setAnimRes(int[] animRes) {

        recycleBmps();

        this.animRes = animRes;

        if (animRes != null && animRes.length != 0) {
            synchronized (locker) {
                bmpAry = new Bitmap[animRes.length];
            }
        }

    }

    private void loadBitmap(int count) {
        if (bmpAry == null || bmpAry.length == 0)
            return;
        int idx = count % bmpAry.length;
        if (bmpAry[idx] != null)
            return;

        bmpAry[idx] = BitmapUtils.load(getContext(), animRes[idx], getWidth(), getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (animRes == null || animRes.length == 0) {
            super.onDraw(canvas);
            return;
        }

        try {

            int counter = drawedCount % bmpAry.length;

            loadBitmap(counter);

            if (bmpAry[counter] != null) {

                canvas.drawBitmap(bmpAry[counter], 0, 0, null);

                drawedCount++;

                if (drawedCount + 1 == bmpAry.length) {
                    drawedCount = 0;
                    mRepeated++;

                    if (repeatTimes > 0 && repeatTimes == mRepeated) {
                        onSmartAnimationEnd();
                        pause();
                        return;
                    }
                    onSmartAnimationRepeat(mRepeated);
                }
            }

        } finally {
            if (!pause) {
                sendInvalidate();
            }
        }

    }

    private final Object locker = new Object();

    private void recycleBmps() {
        if (bmpAry == null) {
            return;
        }

        synchronized (locker) {

            for (int i = 0; i < bmpAry.length; i++) {
                if (bmpAry[i] == null) {
                    continue;
                }

                if (!bmpAry[i].isRecycled()) {
                    bmpAry[i].recycle();
                }

                bmpAry[i] = null;
            }
            bmpAry = null;
        }
    }

    public synchronized void onDestroy() {
        recycleBmps();
    }

    public SmartAnimationListener getSmartAnimationListener() {
        return smartAnimationListener;
    }

    public void setSmartAnimationListener(SmartAnimationListener smartAnimationListener) {
        this.smartAnimationListener = smartAnimationListener;
    }
}
