package com.dev.kit.basemodule.netRequest.subscribers;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.dev.kit.basemodule.BuildConfig;
import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.View.NetProgressDialog;
import com.dev.kit.basemodule.netRequest.Configs.Config;
import com.dev.kit.basemodule.netRequest.util.OnNetProgressCancelListener;
import com.dev.kit.basemodule.result.BaseResult;
import com.dev.kit.basemodule.util.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 网络请求订阅者
 * Created by cuiyan on 16/6/2 14:09
 */
public class NetRequestSubscriber<T> implements Observer<T> {
    private Dialog progressDialog;
    private Disposable disposable;
    private NetRequestCallback<T> netRequestCallback;
    private Context context;

    /**
     * @param netRequestCallback 网络请求回调
     */
    public NetRequestSubscriber(@NonNull NetRequestCallback<T> netRequestCallback, Context context) {
        this(netRequestCallback, context, false, null);
    }

    /**
     * @param netRequestCallback    网络请求回调
     * @param showProgress          是否显示网络请求加载对话框
     * @param progressTip           loading提示语
     * @see NetProgressDialog
     */
    public NetRequestSubscriber(@NonNull final NetRequestCallback<T> netRequestCallback, Context context, boolean showProgress, String progressTip) {
        this.netRequestCallback = netRequestCallback;
        this.context = context;
        if (showProgress) {
            progressDialog = NetProgressDialog.getInstance(context, progressTip, new OnNetProgressCancelListener() {
                @Override
                public void onCancelRequest() {
                    cancelRequest();
                }
            });
        }
    }

    /**
     * @param netRequestCallback 网络请求回调
     * @param progressDialog     dialog 自定义对话框
     */
    public NetRequestSubscriber(@NonNull NetRequestCallback<T> netRequestCallback, Context context, @NonNull Dialog progressDialog) {
        this.netRequestCallback = netRequestCallback;
        this.context = context;
        this.progressDialog = progressDialog;
    }


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        this.disposable = d;
        showProgress();
        onRequestStart();
    }

    @Override
    public synchronized void onNext(final T t) {
        if (t == null) {
            onRequestResultNull();
        } else {
            if (t instanceof BaseResult && !Config.REQUEST_SUCCESS_CODE.equals(((BaseResult) t).getCode())) {
                ToastUtil.showToast(context, ((BaseResult) t).getMessage());
            }
            onRequestSuccess(t);
        }
    }

    @Override
    public synchronized void onError(Throwable throwable) {
        dismissProgress();
        onRequestError(throwable);
        if (throwable instanceof HttpException) {
            ToastUtil.showToast(context, ((HttpException) throwable).message() + ((HttpException) throwable).code());
        } else {
            if (BuildConfig.DEBUG) {
                ToastUtil.showToast(context, "error:" + throwable.getMessage());
            } else {
                ToastUtil.showToast(context, context.getString(R.string.error_net_request_failed));
            }
        }
    }

    /**
     * {@link NetRequestSubscriber#onError(Throwable)}
     * {@link Observer#onError(Throwable)}
     * {@link Observer#onComplete()} (Throwable)}
     * 该方法与onError方法互斥
     */
    @Override
    public void onComplete() {
        dismissProgress();
        netRequestCallback.onFinish();
    }

    private void onRequestStart() {
        if (netRequestCallback != null) {
            if (Looper.myLooper() != context.getMainLooper()) {
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        netRequestCallback.onStart();
                    }
                });
            } else {
                netRequestCallback.onStart();
            }
        }
    }

    private void onRequestSuccess(final T t) {
        if (Looper.myLooper() != context.getMainLooper()) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    netRequestCallback.onSuccess(t);
                }
            });
        } else {
            netRequestCallback.onSuccess(t);
        }
    }

    private void onRequestResultNull() {
        if (Looper.myLooper() != context.getMainLooper()) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    netRequestCallback.onResultNull();
                }
            });
        } else {
            netRequestCallback.onResultNull();
        }
    }

    private void onRequestError(final Throwable throwable) {
        throwable.printStackTrace();
        if (Looper.myLooper() != context.getMainLooper()) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    netRequestCallback.onError(throwable);
                    netRequestCallback.onFinish();
                }
            });
        } else {
            netRequestCallback.onError(throwable);
            netRequestCallback.onFinish();
        }
    }

    /**
     *
     *
     */
    private void showProgress() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void cancelRequest() {
        dismissProgress();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        netRequestCallback.onCancel();
        netRequestCallback.onFinish();
    }
}
