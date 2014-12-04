package com.github.lisicnu.libDroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.github.lisicnu.libDroid.R;

/**
 * 带下划线的编辑框. [输入的字符下面有实心下划线]<br/>
 * 可使用 {@link com.github.lisicnu.libDroid.R.styleable#UnderLineTextView_UnderLineColor} 设置下划线的颜色.
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class UnderLineEditText extends EditText {

    Paint mPaint;
    Rect r = new Rect();

    public UnderLineEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTextPaint();
        init(attrs);
    }

    public UnderLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextPaint();
        init(attrs);
    }

    public UnderLineEditText(Context context) {
        super(context);
        initTextPaint();
        init(null);
    }

    protected Paint getUnderLinePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    void init(AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UnderLineTextView);

        if (a == null) return;
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            if (R.styleable.UnderLineTextView_UnderLineColor == attr) {
                setUnderLineColor(a.getColor(attr, Color.BLUE));
            }
        }
        a.recycle();
    }


    private void initTextPaint() {
        mPaint = getUnderLinePaint();
        setUnderLineColor(Color.BLUE);
    }

    public void setUnderLineColor(int color) {
        if (mPaint != null)
            mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = getLineCount();

        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);
            canvas.drawLine(r.left + getScrollX(), baseline + 5, r.right + getScrollX(),
                    baseline + 5, mPaint);
        }

    }
}
