package com.github.lisicnu.libDroid.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public abstract class AdjustCursorAdapter extends CursorAdapter {

    public AdjustCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public AdjustCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public AdjustCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    int mAdjustSize = 0;

    public void setAdjustSize(int adjustSize) {
        this.mAdjustSize = adjustSize;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdjustSize <= 0 && super.areAllItemsEnabled();
    }

    @Override
    public int getCount() {
        if (mAdjustSize <= 0)
            return super.getCount();

        int count = super.getCount();

        int tmp = count % mAdjustSize;
        if (tmp == 0) {
            return count;
        }
        return count + (mAdjustSize - tmp);
    }

    @Override
    public abstract View newView(Context context, Cursor cursor, ViewGroup parent);

    @Override
    public abstract void bindView(View view, Context context, Cursor cursor);
}
