package com.dev.kit.testapp.serviceTest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.dev.kit.basemodule.util.LogUtil;

/**
 * Created by cuiyan on 2019/3/29.
 */

public class TestIntentService extends IntentService {
    private final String TAG = getClass().getSimpleName();


    public TestIntentService() {
        super("aaa");
    }

    @Override
    public void onCreate() {
        LogUtil.e("mytag", TAG + " onCreate in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        LogUtil.e("mytag", TAG + " onStart in main thread " + (Looper.myLooper() == Looper.getMainLooper()) + " " + startId);
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        LogUtil.e("mytag", TAG + " onStartCommand in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LogUtil.e("mytag", TAG + " onHandleIntent in main thread " + (Looper.myLooper() == Looper.getMainLooper()) + " " + Thread.currentThread().getId() + " " + Looper.myLooper().getThread().getId());
    }

    @Override
    public void onDestroy() {
        LogUtil.e("mytag", TAG + " onDestroy in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        super.onDestroy();
    }
}
