package com.github.lisicnu.libDroid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import com.github.lisicnu.libDroid.R;

/**
 * if want to load view dynamic. must implement OnViewFlipperListener.
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class DynamicFlipper extends ViewFlipper implements DynamicFlipperGestureListener.OnFlingListener {

    public interface OnViewFlipperListener {
        /**
         * to get next dynamic view.
         *
         * @return
         */
        View getNextView();

        /**
         * to get previous dynamic view.
         *
         * @return
         */
        View getPreviousView();
    }

    private OnViewFlipperListener mOnViewFlipperListener = null;

    private GestureDetector mGestureDetector = null;

    public DynamicFlipper(Context context) {
        super(context);
    }

    public DynamicFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnViewFlipperListener(OnViewFlipperListener mOnViewFlipperListener) {

        this.mOnViewFlipperListener = mOnViewFlipperListener;

        DynamicFlipperGestureListener myGestureListener = new DynamicFlipperGestureListener();
        myGestureListener.setOnFlingListener(this);
        mGestureDetector = new GestureDetector(getContext(), myGestureListener);
    }

    public OnViewFlipperListener getOnViewFlipperListener() {
        return mOnViewFlipperListener;
    }

    //
    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev){
    // if(null!= mGestureDetector) {
    // Log.i("event",
    // "★★★翻页事件----onInterceptTouchEvent---return " +
    // super.onInterceptTouchEvent(ev));
    //
    // if(ev.getPointerCount() <= 1) {
    // Log.i("event", "★★★翻页事件----onInterceptTouchEvent-----1 个手指滑动-----return "
    // + mGestureDetector.onTouchEvent(ev));
    //
    // return mGestureDetector.onTouchEvent(ev);
    // }
    // }
    //
    // return super.onInterceptTouchEvent(ev);
    // }
    //
    // @Override
    // public boolean onTouchEvent(MotionEvent event){
    // setDisplayedChild(1);
    // return super.onTouchEvent(event);
    // }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (null != mGestureDetector) {
            return mGestureDetector.onTouchEvent(ev);
        } else {
            return super.onTouchEvent(ev);
        }
    }

    public void updateCurView(View view) {
        int tmpIdx = getDisplayedChild();
        removeViewAt(tmpIdx);
        addView(view, tmpIdx);
    }

    @Override
    public void flingToNext() {
        if (null != mOnViewFlipperListener) {
            int childCnt = getChildCount();
            if (childCnt == 2) {
                removeViewAt(1);
            }
            addView(mOnViewFlipperListener.getNextView(), 0);
            if (0 != childCnt) {

                setInAnimation(getContext(), R.anim.slide_in_left);
                setOutAnimation(getContext(), R.anim.slide_out_right);
                setDisplayedChild(0);
            }
        }
    }

    @Override
    public void flingToPrevious() {
        if (null != mOnViewFlipperListener) {
            int childCnt = getChildCount();
            if (childCnt == 2) {
                removeViewAt(1);
            }
            addView(mOnViewFlipperListener.getPreviousView(), 0);
            if (0 != childCnt) {
                setInAnimation(getContext(), R.anim.slide_in_right);
                setOutAnimation(getContext(), R.anim.slide_out_left);
                setDisplayedChild(0);
            }
        }
    }

}
