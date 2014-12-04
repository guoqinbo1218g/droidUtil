package com.github.lisicnu.libDroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 单行滚动文本框. <br/>
 * 可使用属性 {@link #ATTR_MODE} 决定滚动模式, 滚动方式有来回滚动[0]和从右往左滚动[1].默认为
 * {@link #MODE_LEFT_AND_RIGHT} = {@value #MODE_LEFT_AND_RIGHT} <br/>
 * 可使用属性 {@link #ATTR_CANSCROLL} 决定是否启用滚动[true|false].默认为true <br/>
 * <blockquote> 說明<br/>
 * 1. 使用此类时, 应该<font color='red'>显式设置控件的高度</font>, 防止在滾動模式下控件的高度會超高<br/>
 * 2. 当控件处于滚动状态的时候, gravity属性无效, 此时y轴方向自动居中. </blockquote>
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class ScrollTextView extends TextView {
    public final static String TAG = ScrollTextView.class.getSimpleName();

    public static final String ATTR_CANSCROLL = "Scroll";

    /**
     * 默认为来回滚动 0
     */
    public static final String ATTR_MODE = "Mode";
    /**
     * 左右来回
     */
    public final static int MODE_RIGHT_TO_LEFT = 0;
    /**
     * 从右往左
     */
    public final static int MODE_LEFT_AND_RIGHT = 1;

    public ScrollTextView(Context context) {
        super(context);
        setScrollModeFromAttr(null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollModeFromAttr(attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScrollModeFromAttr(attrs);
    }

    private void setScrollModeFromAttr(AttributeSet attrs) {

        boolean set = false;
        if (attrs != null)
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                if (ATTR_MODE.equals(attrs.getAttributeName(i))) {
                    int tmp = attrs.getAttributeIntValue(i, 0);
                    setScrollMode(tmp);
                    set = true;
                } else {
                    if (ATTR_CANSCROLL.equals(attrs.getAttributeName(i))) {
                        setEnableScroll(attrs.getAttributeBooleanValue(i, true));
                    }
                }
            }
        if (!set)
            setScrollMode(MODE_RIGHT_TO_LEFT);

    }

    /**
     * 是否支持滚动, 默认支持
     */
    private boolean canScroll = true;
    int scrollMode = MODE_LEFT_AND_RIGHT;
    private float textLength = 0f, viewWidth = 0f;
    private float x = 0f, y = 0f;
    private boolean shouldScroll = false, first = true;
    private String text = "";
    float minLeft, maxRight, curL = 0, curStep = 0.5f, distance = 100;
    int offset = -1; // 显示方向偏移 1 or -1
    final static float MAX_RIGHT = 80f;

    /**
     * 更新显示的MODE, 如果mode 非法将设置失败, 不做任何更改
     *
     * @param mode
     * @return 返回设置是否成功
     */
    public boolean setScrollMode(int mode) {
        if (mode != MODE_LEFT_AND_RIGHT && mode != MODE_RIGHT_TO_LEFT) {
            return false;
        }
        scrollMode = mode;
        if (mode == MODE_RIGHT_TO_LEFT) {
            distance = 100;
        } else {
            offset = -1;
            if (getWidth() > MAX_RIGHT) {
                distance = MAX_RIGHT;
            } else {
                distance = getWidth() / 2;
            }
        }

        curL = distance;
        first = true;
        return true;
    }

    /**
     * 获取当前的显示MODE
     *
     * @return
     */
    public int getScrollMode() {
        return scrollMode;
    }

    public boolean isScrollEnabled() {
        return canScroll;
    }

    public void setEnableScroll(boolean enabled) {
        this.canScroll = enabled;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before,
                                 int after) {
        first = true;
        super.onTextChanged(text, start, before, after);
    }

    public void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            // super.onDraw(canvas);
            // return;
            if (text == null || text.isEmpty()) {
                updateText(super.getText().toString());
            }
        }

        if (!isScrollEnabled()) {
            super.onDraw(canvas);
            return;
        }

        if (first) {
            viewWidth = getWidth();
            text = super.getText().toString();
            updateText(text);
            first = false;
        }

        if (!shouldScroll) {
            // canvas.drawText(text, x, y, getPaint());
            // 采用父类绘制的好处可以明显的得到
            super.onDraw(canvas);
        } else {
            canvas.save();
            if (getTextColors() != null && getPaint() != null) {
                getPaint().setColor(getTextColors().getDefaultColor());
            }
            if (MODE_LEFT_AND_RIGHT == scrollMode) {
                float leftMoveEdge = (textLength + curL) + getPaddingLeft();

                // TODO 最大显示长度. 右边边缘位置计算
                if (leftMoveEdge > (viewWidth - getPaddingLeft() - getPaddingRight())) {
                    leftMoveEdge = viewWidth - getPaddingRight();
                }
                canvas.clipRect(getPaddingLeft(), getPaddingTop(),
                        leftMoveEdge, getBottom() - getTop());

                canvas.drawText(text, curL, y, getPaint());
                canvas.restore();

                canvas.save();
                canvas.clipRect(textLength + curL + distance, getPaddingTop(),
                        viewWidth - getPaddingRight(), getBottom() - getTop());

                canvas.drawText(text, textLength + curL + distance, y,
                        getPaint());
                canvas.restore();

                curL += (curStep * offset);

                if (Math.abs(curL) >= (textLength + distance)) {
                    curL = textLength + curL + distance;
                }

            } else {
                // 左右来回滚动
                canvas.drawText(text, curL, y, getPaint());
                curL += (curStep * offset);
                if (curL <= minLeft) {
                    offset = 1;
                }
                if (curL > maxRight) {
                    offset = -1;
                }

            }
            if (first) {
                sendMSG(600);
                first = false;
            } else {
                sendMSG(0);
            }
        }
    }

    private void updateText(String text) {
        if (!isScrollEnabled()) {
            return;
        }

        // 此处调用此方法是为了防止在在初始化的时候, viewWidth=0, 而导致的绘制有问题
        setScrollMode(scrollMode);

        this.text = text;
        textLength = getPaint().measureText(text);
        // y = getTextSize() + getPaddingTop();
        x = getPaddingLeft();

        // 计算文字高度
        FontMetrics fontMetrics = getPaint().getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字baseline, 居中
        y = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom;

        if (textLength > (viewWidth - getPaddingLeft() - getPaddingRight())) {
            shouldScroll = true;

            if (scrollMode == MODE_RIGHT_TO_LEFT) {
                minLeft = viewWidth - textLength - MAX_RIGHT / 2;

                // 这是以前的最大右边滚动距离计算方式, 此种方式不会有错,
                // 但是当viewWidth和MAX_RIGHT差不多的時候就會很難看

                // maxRight = -minLeft;
                //
                // if (maxRight > MAX_RIGHT)
                // maxRight = MAX_RIGHT;
                //

                maxRight = MAX_RIGHT / 2;

                // 确保屏幕位置上不会出现空白
                if (maxRight > viewWidth) {
                    maxRight = viewWidth / 2;
                }
            }
            // 确保屏幕位置上不会出现空白
            if (curL > viewWidth / 2) {
                curL = viewWidth / 2;
            }
        } else {

            shouldScroll = false;

            // if (scrollMode == MODE_LEFT_AND_RIGHT) {

            if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
                x = getPaddingLeft();
            } else if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT) {
                x = viewWidth - textLength;
            } else {
                x += (viewWidth - textLength) / 2;
            }

            // } else if (scrollMode == MODE_RIGHT_TO_LEFT) {
            // x = (viewWidth - textLength - getPaddingLeft() -
            // getPaddingRight()) / 2
            // + getPaddingLeft();
            //
            // if (x <= getPaddingLeft()) {
            // x = getPaddingLeft();
            // }
            // }
        }

        invalidate();
    }

    final int REFRESH_MSG = 0x1;

    private void sendMSG(int delay) {
        handler.removeMessages(REFRESH_MSG);
        handler.sendEmptyMessageDelayed(REFRESH_MSG, delay);
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == REFRESH_MSG)
                invalidate();
        }
    };

}
