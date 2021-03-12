package com.dev.kit.basemodule.surpport;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * CommonPagerAdapter
 * 可绑定数据列表
 * Created by cy on 2018/4/10.
 */

public abstract class CommonPagerAdapter<T> extends RealPagerAdapterImp {

    private final SparseArray<View> viewCache = new SparseArray<>();
    private final boolean infiniteLoop;
    private final boolean useCache;
    private final List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public CommonPagerAdapter(List<T> dataList) {
        this(false, true, dataList);
    }

    public CommonPagerAdapter(boolean infiniteLoop, boolean useCache, List<T> dataList) {
        this.infiniteLoop = infiniteLoop;
        this.dataList = dataList;
        this.useCache = useCache;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getCount() {
        return dataList == null || dataList.isEmpty() ? 0 : infiniteLoop ? Integer.MAX_VALUE : dataList.size();
    }

    @Override
    public final int getRealCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = getItemView(container, position);
        if (onItemClickListener != null) {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(v, getRealPosition(position)));
        }
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener(v -> onItemLongClickListener.onItemLongClick(v, getRealPosition(position)));
        }
        return itemView;
    }

    /**
     * 获取ViewPager itemView
     * 若缓存中存在itemView则取之，不存在新生成
     */
    private View getItemView(@NonNull ViewGroup container, int position) {
        View itemView;
        if (useCache) {
            itemView = viewCache.get(position);
            if (itemView == null) {
                itemView = getPageItemView(container, getRealPosition(position));
                viewCache.put(position, itemView);
            }
        } else {
            itemView = getPageItemView(container, getRealPosition(position));
        }
        renderItemView(itemView, getRealPosition(position));
        container.addView(itemView);
        return itemView;
    }

    /**
     * 渲染itemView
     */
    public abstract void renderItemView(@NonNull View itemView, final int realPosition);

    /**
     * 获取itemView
     *
     * @see #getItemView(ViewGroup, int)
     */
    @NonNull
    public abstract View getPageItemView(@NonNull ViewGroup container, final int realPosition);

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public int getRealPosition(int position) {
        return position % getRealCount();
    }

    public T getDataItem(int realPosition) {
        if (dataList != null && dataList.size() > realPosition && realPosition >= 0) {
            return dataList.get(realPosition);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int realPosition);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int realPosition);
    }
}
