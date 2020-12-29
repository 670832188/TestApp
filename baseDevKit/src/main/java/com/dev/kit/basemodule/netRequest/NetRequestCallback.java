package com.dev.kit.basemodule.netRequest;

import androidx.annotation.NonNull;

/**
 * 网络请求回调
 * Created by cuiyan on 16/6/2 14:19
 */
public abstract class NetRequestCallback<T> {

    public void onStart() {
    }

    public abstract void onSuccess(@NonNull T t);

    public abstract void onError(Throwable throwable);

    public void onFinish() {
    }
}
