package com.dev.kit.basemodule.surpport;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author： cuiyan
 * Date:   16/6/13 13:32
 * Desc：
 */
public class ViewHolder {

    /**
     * View容器，用于存放Holder中的View
     */
    private SparseArray<View> views;
    /**
     * Item布局View
     */
    private View convertView;

    public ViewHolder(Context context, ViewGroup parent, int layoutId) {
        views = new SparseArray<>();
        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        convertView.setTag(this);
    }

    /**
     * 获取ViewHolder
     *
     * @param layoutId 布局layout Id
     */
    public static ViewHolder getViewHolder(Context context, View convertView,
                                           ViewGroup parent, int layoutId) {

        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId);
        }
        return (ViewHolder) convertView.getTag();
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
            item = convertView.findViewById(viewId);
            views.put(viewId, item);
        }
        return (T) item;
    }

    /**
     * 获取convertView
     */
    public View getConvertView() {
        return convertView;
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

    public void setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }
}
