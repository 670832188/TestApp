package com.dev.kit.basemodule.netRequest;

import com.trello.rxlifecycle4.LifecycleProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author cuiyan
 * Created on 2020/12/25.
 */
public abstract class CommonRequestModel<T, E> {
    private Disposable disposable;
    private final NetRequestCallback<T> requestCallback;

    public CommonRequestModel(@NonNull NetRequestCallback<T> callback) {
        this.requestCallback = callback;
    }

    public final void requestData() {
        requestData(null);
    }

    public final void requestData(@Nullable LifecycleProvider<E> provider) {
        Observable<T> observable = getObservable();
        if (provider != null) {
            observable = observable.compose(provider.bindToLifecycle());
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                        requestCallback.onStart();
                    }

                    @Override
                    public void onNext(@NonNull T result) {
                        requestCallback.onSuccess(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        requestCallback.onError(e);
                        requestCallback.onFinish();
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        requestCallback.onFinish();
                        disposable.dispose();
                    }
                });
    }

    public final void cancelRequest() {
        if (disposable == null) {
            return;
        }
        disposable.dispose();
        requestCallback.onFinish();
    }

    protected abstract Observable<T> getObservable();
}
