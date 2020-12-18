package com.dev.kit.basemodule.surpport;

import androidx.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * CommonPagerAdapter
 * 可绑定数据列表
 * Created by cy on 2018/4/10.
 */

public abstract class CommonPagerAdapter<T> extends RealPagerAdapterImp {

    private SparseArray<View> viewCache = new SparseArray<>();
    private List<T> dataList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public CommonPagerAdapter(List<T> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
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
    public View instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = getItemView(container, position);
        if (onItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });
        }
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClickListener.onItemLongClick(v, position);
                }
            });
        }
        return itemView;
    }

    /**
     * 获取ViewPager itemView
     * 若缓存中存在itemView则取之，不存在新生成
     */
    private View getItemView(@NonNull ViewGroup container, int position) {
        View itemView = viewCache.get(position);
        if (itemView == null) {
            itemView = getPageItemView(container, position);
            viewCache.put(position, itemView);
        }
        renderItemView(itemView, position);
        container.addView(itemView);
        return itemView;
    }

    /**
     * 渲染itemView
     */
    public abstract void renderItemView(@NonNull View itemView, int position);

    /**
     * 获取itemView
     *
     * @see #getItemView(ViewGroup, int)
     */
    @NonNull
    public abstract View getPageItemView(@NonNull ViewGroup container, final int position);

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public T getBindItemData(int position) {
        if (dataList != null && dataList.size() > position && position >= 0) {
            return dataList.get(position);
        }
        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}
