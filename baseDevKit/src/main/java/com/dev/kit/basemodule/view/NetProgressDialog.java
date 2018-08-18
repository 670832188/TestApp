package com.dev.kit.basemodule.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.netRequest.util.OnNetProgressCancelListener;


/**
 * 进度对话框
 * Created by cy on 2017/9/12.
 */

public class NetProgressDialog extends Dialog {

    public synchronized static NetProgressDialog getInstance(Context context) {
        return getInstance(context, null);
    }

    public synchronized static NetProgressDialog getInstance(Context context, String tipMsg) {
        return getInstance(context, tipMsg, null);
    }

    public synchronized static NetProgressDialog getInstance(Context context, String tipMsg, OnNetProgressCancelListener listener) {
        return new NetProgressDialog(context, tipMsg, listener);
    }

    private NetProgressDialog(Context context, String tipMsg, final OnNetProgressCancelListener cancelRequestListener) {
        super(context, R.style.BaseDialogTheme);
        init(tipMsg);
        if (cancelRequestListener != null) {
            setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelRequestListener.onCancelRequest();
                }
            });
        }
    }

    private void init(String tipMsg) {
        setContentView(R.layout.layout_net_loading);
        if (!TextUtils.isEmpty(tipMsg)) {
            TextView tvTip = (TextView) findViewById(R.id.tv_tip);
            tvTip.setText(tipMsg);
            tvTip.setVisibility(View.GONE);
        }
//        Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.gravity = Gravity.CENTER;
//        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        params.dimAmount = 0.7f;
//        params.format = PixelFormat.TRANSLUCENT;

        // 触摸对话框以外的地方取消对话框
        setCanceledOnTouchOutside(false);
    }
}
