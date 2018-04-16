package com.dev.kit.basemodule.surpport;

import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * CommonPagerAdapterFactory
 * Created by cy on 2018/4/10.
 */

public abstract class CommonPagerAdapterFactory<T> {
    private SparseArray<View> viewCache = new SparseArray<>();
    private List<T> dataList;

    public CommonPagerAdapterFactory() {
    }

    protected CommonPagerAdapterFactory(List<T> dataList) {
        this.dataList = dataList;
    }

    /**
     * 若未绑定dataList或未使用有参构造方法，则实例化时需要重写该方法
     */
    public int getPageCont() {
        return dataList == null ? 0 : dataList.size();
    }

    public abstract View getPageItemView(@NonNull ViewGroup container, int position);

    protected void addCacheView(int position, View cacheView) {
        viewCache.put(position, cacheView);
    }

    protected View getCacheView(int position) {
        return viewCache.get(position);
    }

    public void bindDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public T getBindItemData(int position) {
        if (dataList != null && dataList.size() > position) {
            return dataList.get(position);
        }
        return null;
    }
}
