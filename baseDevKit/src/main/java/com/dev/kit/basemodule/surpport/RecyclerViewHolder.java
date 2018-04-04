package com.dev.kit.basemodule.surpport;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * RecyclerView.ViewHolder
 * Created by cuiyan on 16-10-20.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    /**
     * View容器，用于存放Holder中的View
     */
    private SparseArray<View> views;
    /**
     * Item布局View
     */
    private View itemView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<>();
    }

    /**
     * 获取ViewHolder
     *
     * @param layoutId 布局layout Id
     */
    public static RecyclerViewHolder getViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    /**
     * 获取Holder中的ItemView
     *
     * @param viewId id
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {

        View item = views.get(viewId);
        if (item == null) {
            item = itemView.findViewById(viewId);
            views.put(viewId, item);
        }
        return (T) item;
    }

    public View getItemView() {
        return itemView;
    }

    /**
     * 为指定的TextView设置文本
     *
     * @param textViewId textView的索引id
     * @param text       文本
     */
    public void setText(@IdRes int textViewId, String text) {
        TextView textView = getView(textViewId);
        textView.setText(text);
    }

    /**
     * 为指定的TextView设置文本
     *
     * @param textViewId textView的索引id
     * @param strResId   文本资源id
     */
    public void setText(@IdRes int textViewId, @StringRes int strResId) {
        TextView textView = getView(textViewId);
        textView.setText(strResId);
    }

    /**
     * 为指定的view设置点击事件监听
     *
     * @param viewId   view的索引id
     * @param listener 监听器
     */
    public void setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        getView(viewId).setOnClickListener(listener);
    }

    /**
     * 为指定的ImageViewId图片
     *
     * @param imageViewId imageView的索引id
     * @param resId       文本
     */
    public void setImageResource(@IdRes int imageViewId, int resId) {
        ImageView imageView = getView(imageViewId);
        imageView.setImageResource(resId);
    }

    public void setEnable(@IdRes int viewId, boolean enable) {
        getView(viewId).setEnabled(enable);
    }

    public void setVisibility(@IdRes int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }

    public void setVisibility(@IdRes int viewId, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        getView(viewId).setVisibility(visibility);
    }
}
