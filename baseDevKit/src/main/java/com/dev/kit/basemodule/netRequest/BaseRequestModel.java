package com.dev.kit.basemodule.netRequest;

import com.trello.rxlifecycle4.LifecycleProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author cuiyan
 * Created on 2020/12/24.
 */
public abstract class BaseRequestModel<T> extends ViewModel {
    private MutableLiveData<BaseResult<T>> result = new MutableLiveData<>();
    private Disposable disposable;

    public final MutableLiveData<BaseResult<T>> getResult() {
        return result;
    }

    public final void requestData() {
        requestData(null);
    }

    @SuppressWarnings("unchecked")
    public final void requestData(@Nullable LifecycleProvider provider) {
        Observable<BaseResult<T>> observable = getObservable();
        if (provider != null) {
            observable = observable.compose(provider.<BaseResult<T>>bindToLifecycle());
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<BaseResult<T>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                        result.postValue(BaseResult.<T>generateStartResult());
                    }

                    @Override
                    public void onNext(@NonNull BaseResult<T> baseResult) {
                        result.postValue(baseResult);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        result.postValue(BaseResult.<T>generateErrorResult(e));
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });

    }

    public final void cancelRequest() {
        if (disposable == null) {
            return;
        }
        disposable.dispose();
        result.postValue(BaseResult.<T>generateCancelResult());
    }

    protected abstract Observable<BaseResult<T>> getObservable();

    public final void observeResult(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<BaseResult<T>> observer) {
        result.observe(owner, observer);
    }
}
