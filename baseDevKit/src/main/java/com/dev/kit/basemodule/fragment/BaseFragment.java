package com.dev.kit.basemodule.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dev.kit.basemodule.util.ToastUtil;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * BaseFragment
 * Created by cy on 2017/11/3.
 */

public class BaseFragment extends RxFragment {
    private WeakReferenceHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showToast(final int msgResId) {
        ToastUtil.showToast(getContext(), msgResId);
    }

    public void showToast(final String msg) {
        ToastUtil.showToast(getContext(), msg);
    }

    public WeakReferenceHandler getHandler() {
        if (handler == null) {
            handler = new WeakReferenceHandler(this);
        }
        return handler;
    }

    private static class WeakReferenceHandler extends Handler {
        WeakReference<Fragment> weakReference;

        private WeakReferenceHandler(@NonNull Fragment fragment) {
            super(Looper.getMainLooper());
            weakReference = new WeakReference<>(fragment);
        }

        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            return !(weakReference == null || weakReference.get() == null) && super.sendMessageAtTime(msg, uptimeMillis);
        }
    }
}
