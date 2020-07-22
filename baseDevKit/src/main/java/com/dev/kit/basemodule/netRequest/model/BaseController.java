package com.dev.kit.basemodule.netRequest.model;

import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.trello.rxlifecycle4.LifecycleProvider;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * Created by cuiyan on 16/6/3 10:36
 */

public class BaseController {
    /**
     * @param subscriber 订阅者
     */
    @SuppressWarnings("unchecked")
    public static synchronized void sendRequest( NetRequestSubscriber subscriber, Observable observable) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * @param provider   生命周期提供者，用于绑定Activity或fragment生命周期
     * @param subscriber 订阅者
     */
    @SuppressWarnings("unchecked")
    public static synchronized void sendRequest(LifecycleProvider provider,  NetRequestSubscriber subscriber, Observable observable) {
        if (provider != null) {
            observable = observable.compose(provider.bindToLifecycle());
        }
        observable //防止内存泄漏,fragment生命周期结束后取消订阅
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
