package com.dev.kit.basemodule.netRequest.model;

import com.dev.kit.basemodule.activity.RxActivity;
import com.dev.kit.basemodule.fragment.RxFragment;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cuiyan on 16/6/3 10:36
 */

public class BaseModel {
    /**
     * @param subscriber 订阅者
     */
    @SuppressWarnings("unchecked")
    public static synchronized void sendRequest(final NetRequestSubscriber subscriber, Observable observable) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * @param activity   用于与observable绑定,activity生命周期结束时,自动取消订阅
     * @param observable 被观察者
     * @param subscriber 订阅者
     */
    @SuppressWarnings("unchecked")
    public static synchronized void sendRequest(RxActivity activity, final NetRequestSubscriber subscriber, Observable observable) {
        observable.subscribeOn(Schedulers.io())
                .compose(activity.bindToLifecycle()) //防止内存泄漏,activity生命周期结束后取消订阅
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * @param fragment   用于与observable绑定,fragment生命周期结束时,自动取消订阅
     * @param subscriber 订阅者
     */
    @SuppressWarnings("unchecked")
    public static synchronized void sendRequest(RxFragment fragment, final NetRequestSubscriber subscriber, Observable observable) {
        observable.compose(fragment.bindToLifecycle()) //防止内存泄漏,fragment生命周期结束后取消订阅
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
