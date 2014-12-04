package com.github.lisicnu.libDroid.widget;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * call {@link #setAdjustSize(int)} before set data source, if parameter is equal or less than 0,
 * means not use auto adjust. otherwise use parameter to adjust.
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class BaseAdapterEx<T> extends BaseAdapter {
    int mAdjustSize = 0;

    /**
     * when set this value bigger than zero. {@link #getCount()} will return multiple of {@link
     * #getAdjustSize()} .
     *
     * @param adjustSize
     */
    public void setAdjustSize(int adjustSize) {
        this.mAdjustSize = adjustSize;
    }

    public int getAdjustSize() {
        return mAdjustSize;
    }

    @Override
    public boolean isEnabled(int position) {
        return dataItems == null || dataItems.size() > position;
    }

    protected final Object locker = new Object();
    /**
     * saves adapter's data. do not operation it.
     */
    protected List<T> dataItems = new ArrayList<T>();

    public void clear() {
        synchronized (locker) {
            if (!dataItems.isEmpty()) {
                dataItems.clear();
                notifyDataSetChanged();
            }
        }
    }

    public List<T> getDataSource() {
        return dataItems;
    }

    public void setDataSource(Collection<T> items) {
        synchronized (locker) {
            dataItems.clear();
            if (items != null) {
                for (T t : items) {
                    dataItems.add(t);
                }
            }
            analysisData();
            notifyDataSetChanged();
        }
    }

    public void setDataSource(T[] items) {
        synchronized (locker) {
            dataItems.clear();
            if (items != null) {
                for (T t : items) {
                    dataItems.add(t);
                }
            }
            analysisData();
            notifyDataSetChanged();
        }
    }

    /**
     * used for analysis data. <br/>
     * <br/>
     * This method calls during {@link #setDataSource(Collection)} and
     * {@link #setDataSource(Object[])} for user handle data.<br/>
     * e.g. change order
     */
    protected void analysisData() {

    }

    /**
     * used for analysis data. <br/>
     * <br/>
     * This method calls during {@link #addData(java.util.Collection)} and
     * {@link #addData(Object[])}  for user handle data.<br/>
     * e.g. change order
     *
     * @return return true if want to added to data source. otherwise return false.
     */
    protected boolean onAddData(T t) {

        return true;
    }

    /**
     * @param items
     */
    public void addData(Collection<T> items) {
        synchronized (locker) {
            if (items != null) {
                for (T t : items) {
                    if (onAddData(t))
                        dataItems.add(t);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * @param items
     */
    public void addData(T[] items) {
        synchronized (locker) {
            if (items != null) {
                for (T t : items) {
                    if (onAddData(t))
                        dataItems.add(t);
                }
            }
            notifyDataSetChanged();
        }
    }

    protected <M extends View> M getAdapterView(View convertView, int id) {
        if (convertView == null) return null;

        SparseArray<View> viewHolder;
        if (convertView.getTag() == null) {
            viewHolder = null;
        } else {
            viewHolder = ((SparseArray<View>) convertView.getTag());
        }

        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }

        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (M) childView;
    }


    public T getItem(int position) {
        if (position >= 0 && dataItems != null && position < dataItems.size()) {
            return dataItems.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (dataItems == null)
            return 0;

        if (mAdjustSize > 0) {
            int tmp = dataItems.size() % mAdjustSize;
            if (tmp == 0) {
                return dataItems.size();
            } else {
                return dataItems.size() + (mAdjustSize - tmp);
            }
        }

        return dataItems.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdjustSize <= 0 && super.areAllItemsEnabled();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
