package com.dev.kit.basemodule.util;

import android.view.View;

import com.jakewharton.rxbinding4.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;


/**
 * Author: cuiyan
 * Date:   18/10/14 18:54
 * Desc:
 */
public class ViewClickUtil {
    /**
     * 防止连续点击
     *
     * @param view            被点击的view
     * @param minTimeInterval 相邻两次点击事件最小时间间隔，单位毫秒
     */
    public static Disposable onViewClick(final View view, int minTimeInterval, final OnClickCallBack clickCallBack) {
        return RxView.clicks(view).throttleFirst(minTimeInterval, TimeUnit.MILLISECONDS)
                .subscribe((Consumer<Object>) o -> {
                    if (clickCallBack != null) {
                        clickCallBack.onClick(view);
                    }
                });
    }

    public interface OnClickCallBack {
        void onClick(View view);
    }
}
