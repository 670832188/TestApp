package com.dev.kit.testapp.serviceTest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.dev.kit.basemodule.util.LogUtil;

/**
 * Created by cuiyan on 2019/3/29.
 */

public class TestService extends Service {
    private boolean isRunning;
    private final String TAG = getClass().getSimpleName();
    public TestService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        LogUtil.e("mytag", TAG + " onCreate in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        LogUtil.e("mytag", TAG + " onStart in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        LogUtil.e("mytag", TAG + " onStartCommand in main thread " + (Looper.myLooper() == Looper.getMainLooper()));
        return super.onStartCommand(intent, flags, startId);
    }


    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        LogUtil.e("mytag", TAG + " onDestroy in main thread " + (Looper.myLooper() == Looper.getMainLooper()) + " " + isRunning);
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        int i = 0;
        public void log() {
            while (isRunning) {
                LogUtil.e("mytag", "count: " + (++i) + " " + isRunning);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public TestService getService() {
            return TestService.this;
        }
    }
}
