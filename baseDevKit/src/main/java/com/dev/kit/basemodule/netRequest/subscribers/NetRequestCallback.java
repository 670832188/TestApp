package com.dev.kit.basemodule.netRequest.subscribers;

import androidx.annotation.NonNull;

/**
 * 网络请求回调
 * Created by cuiyan on 16/6/2 14:19
 */
public class NetRequestCallback<T> {

    public void onStart() {
    }

    public void onSuccess(@NonNull T t) {
    }

    public void onResultNull() {
    }

    public void onError(Throwable throwable) {
    }

    public void onCancel() {
    }

    public void onFinish() {
    }
}
