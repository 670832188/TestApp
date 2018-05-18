package com.dev.kit.basemodule.activity;

import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dev.kit.basemodule.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 状态视图activity，便于状态切换及监听
 * Created by cy on 2018/1/3.
 */

public abstract class BaseStateViewActivity extends BaseActivity {
    private int contentState = STATE_INVALID;
    private View lastContentView;
    private FrameLayout flContainer;
    // 网络请求状态视图
    private View progressView;
    // 网络请求异常视图
    private View netErrorView;
    // 空数据视图
    private View dataEmptyView;
    // 数据视图
    private View dataContentView;

    // 以下为状态视图对应的状态值
    public static final int STATE_INVALID = -1;
    // 网络请求视图
    public static final int STATE_NET_PROGRESS = 0;
    // 网络请求异常视图
    public static final int STATE_NET_ERROR = 1;
    // 空数据视图
    public static final int STATE_DATA_EMPTY = 2;
    // 数据视图
    public static final int STATE_DATA_CONTENT = 3;

    private OnStateChangeListener onStateChangeListener;

    @IntDef({STATE_NET_PROGRESS, STATE_NET_ERROR, STATE_DATA_EMPTY, STATE_DATA_CONTENT, STATE_INVALID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentState {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_state_view);
        initBaseView();
    }

    private void initBaseView() {
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        progressView = inflater.inflate(R.layout.layout_net_loading, flContainer, false);
        netErrorView = inflater.inflate(R.layout.layout_net_error, flContainer, false);
        dataEmptyView = inflater.inflate(R.layout.layout_data_empty, flContainer, false);
        dataContentView = createContentView(inflater, flContainer);
        progressView.setVisibility(View.GONE);
        netErrorView.setVisibility(View.GONE);
        dataEmptyView.setVisibility(View.GONE);
        dataContentView.setVisibility(View.GONE);
        flContainer.addView(progressView);
        flContainer.addView(netErrorView);
        flContainer.addView(dataEmptyView);
        flContainer.addView(dataContentView);
    }

    public FrameLayout getFlContainer() {
        return flContainer;
    }

    public abstract View createContentView(LayoutInflater inflater, ViewGroup contentRoot);

    public void setOnStateChangeListener(@NonNull OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setContentState(@ContentState int contentState) {
        if (contentState == this.contentState) {
            return;
        }
        this.contentState = contentState;
        View currentContentView = null;
        switch (contentState) {
            case STATE_INVALID: {
                break;
            }
            case STATE_NET_PROGRESS: {
                currentContentView = progressView;
                break;
            }
            case STATE_NET_ERROR: {
                currentContentView = netErrorView;
                break;
            }
            case STATE_DATA_EMPTY: {
                currentContentView = dataEmptyView;
                break;
            }
            case STATE_DATA_CONTENT: {
                currentContentView = dataContentView;
                break;
            }
        }
        if (null != currentContentView) {
            if (lastContentView != null) {
                lastContentView.setVisibility(View.GONE);
            }
            lastContentView = currentContentView;
            currentContentView.setVisibility(View.VISIBLE);
            if (currentContentView != progressView) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(currentContentView, "alpha", 0, 1).setDuration(500);
                animator.start();
            }
        }
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChanged(contentState);
        }
    }

    /**
     * 设置各种状态view点击事件
     * 当前仅支持网络请求异常及空数据状态视图点击事件
     *
     * @see #netErrorView
     * @see #dataEmptyView
     */
    public void setOnStateViewClickListener(@ContentState int contentState, View.OnClickListener listener) {
        if (contentState == STATE_NET_ERROR) {
            netErrorView.setOnClickListener(listener);
        } else if (contentState == STATE_DATA_EMPTY) {
            dataEmptyView.setOnClickListener(listener);
        }
    }

    public interface OnStateChangeListener {
        void onStateChanged(@ContentState int contentState);
    }
}
