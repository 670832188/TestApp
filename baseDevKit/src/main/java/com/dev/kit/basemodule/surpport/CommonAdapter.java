package com.dev.kit.basemodule.surpport;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.dev.kit.basemodule.util.ToastUtil;

import java.util.List;

/**
 * Author：cuiyan
 * Date:  16/6/13 13:24
 * Desc：
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    private Handler handler;
    private Context context;
    /**
     * 数据源
     */
    private List<T> dataList;
    /**
     * Item布局ID
     */
    private int layoutId;

    public CommonAdapter(Context context, List<T> dataList, int layoutId) {
        this.context = context;
        this.dataList = dataList;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    /**
     * 获取当前点击的Item的数据时用
     * 在onItemClick中 parent.getAdapter().getItem(),获取当前点击的Item的数据
     */
    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 只关心这一个方法
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getViewHolder(context, convertView, parent, layoutId);
        fillData(holder, position);
        return holder.getConvertView();
    }

    /**
     * 抽象方法，用于子类实现，填充数据
     *
     * @param holder   viewHolder
     * @param position item 的位置
     */
    protected abstract void fillData(ViewHolder holder, int position);

    /**
     * 追加数据
     */
    public void appendData(@NonNull List<T> list) {
        if (dataList != null && !list.isEmpty()) {
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 追加数据
     */
    public void appendData(@NonNull T item) {
        if (dataList != null) {
            dataList.add(item);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除数据
     */
    public void removeItem(int index) {
        if (dataList != null) {
            dataList.remove(index);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除数据
     */
    public void removeData(List<T> list) {
        if (dataList != null) {
            dataList.removeAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除数据
     */
    public void removeItem(T item) {
        if (dataList != null) {
            dataList.remove(item);
            notifyDataSetChanged();
        }
    }

    public void updateDataList(List<T> dataList) {
        if (this.dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (dataList != null) {
            dataList.clear();
            notifyDataSetChanged();
        }
    }

    public void replaceData(int index, T data) {
        if (dataList != null && dataList.size() >= index + 1) {
            dataList.remove(index);
            dataList.add(index, data);
            notifyDataSetChanged();
        }
    }

    void showToast(final String msg) {
        ToastUtil.showToast(context, msg);
    }

    void showToast(final int msgResourceId) {
        ToastUtil.showToast(context, msgResourceId);
    }

    public String getString(int stringId) {
        return context.getResources().getString(stringId);
    }

    public List<T> getDataList() {
        return dataList;
    }

    public Context getContext() {
        return context;
    }

    public void startActivity(Intent intent) {
        context.startActivity(intent);
    }
}