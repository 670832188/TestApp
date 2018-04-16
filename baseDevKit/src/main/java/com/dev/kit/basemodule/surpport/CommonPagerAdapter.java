package com.dev.kit.basemodule.surpport;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * CommonPagerAdapter
 * 可绑定数据列表
 * Created by cy on 2018/4/10.
 */

public class CommonPagerAdapter<T> extends PagerAdapter {

    private CommonPagerAdapterFactory<T> pagerAdapterFactory;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public CommonPagerAdapter(@NonNull CommonPagerAdapterFactory<T> pagerAdapterFactory) {
        this.pagerAdapterFactory = pagerAdapterFactory;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getCount() {
        return pagerAdapterFactory.getPageCont();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = pagerAdapterFactory.getPageItemView(container, position);
        container.addView(itemView);
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    public T getBindItemData(int position) {
       return pagerAdapterFactory.getBindItemData(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}
