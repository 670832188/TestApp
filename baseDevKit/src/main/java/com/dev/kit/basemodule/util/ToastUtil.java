package com.dev.kit.basemodule.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * ToastUtil
 * Created by cy on 2018/3/5.
 */

public class ToastUtil {
    private static Handler handler = new Handler();
    private static Toast toast;

    public synchronized static void showToast(Context context, @StringRes int msgResId) {
        String msg = context.getResources().getString(msgResId);
        showToast(context, msg);
    }

    public synchronized static void showToast(final Context context, final String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (toast == null) {
                if (Looper.myLooper() == context.getMainLooper()) {
                    toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (handler == null) {
                        handler = new Handler(context.getMainLooper());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    });
                }
            } else {
                if (Looper.myLooper() == context.getMainLooper()) {
                    toast.setText(msg);
                    toast.show();
                } else {
                    if (handler == null) {
                        handler = new Handler(context.getMainLooper());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            toast.setText(msg);
                            toast.show();
                        }
                    });
                }
            }
        }
    }
}
