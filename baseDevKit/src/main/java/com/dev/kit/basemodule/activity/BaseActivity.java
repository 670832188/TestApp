package com.dev.kit.basemodule.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.kit.basemodule.util.PermissionRequestUtil;
import com.dev.kit.basemodule.util.ToastUtil;

public class BaseActivity extends RxActivity {

    private OnBackPressedListener onBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        onBackPressedListener = onBackPressedListener;
    }

    public OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        hideKeyBoard();
        super.onDestroy();
    }

    public void showToast(final int msgResId) {
        ToastUtil.showToast(this, msgResId);
    }

    public void showToast(final String msg) {
        ToastUtil.showToast(this, msg);
    }

    public void hideKeyBoard() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        findViewById(viewId).setOnClickListener(listener);
    }

    public void setText(@IdRes int viewId, @StringRes int resId) {
        TextView textView = (TextView) findViewById(viewId);
        textView.setText(resId);
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        TextView textView = (TextView) findViewById(viewId);
        textView.setText(text);
    }

    public void setImageSrc(@IdRes int viewId, @DrawableRes int resId) {
        ImageView imageView = (ImageView) findViewById(viewId);
        imageView.setImageResource(resId);
    }

    public void setImageBitmap(@IdRes int viewId, @NonNull Bitmap bitmap) {
        ImageView imageView = (ImageView) findViewById(viewId);
        imageView.setImageBitmap(bitmap);
    }

    public void setVisibility(@IdRes int viewId, int visibility) {
        findViewById(viewId).setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionRequestUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
