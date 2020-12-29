package com.dev.kit.basemodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dev.kit.basemodule.R;


/**
 * 持有进度条的webView
 * Created by cy on 2017/11/28.
 */

public class ProgressWebView extends WebView {

    private ProgressBar progressBar;
    private boolean enableProgress;

    public ProgressWebView(Context context) {
        super(context);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressWebView);
        int progressBarHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressWebView_progressBarHeight, 8);
        enableProgress = typedArray.getBoolean(R.styleable.ProgressWebView_enableProgressBar, false);
        Drawable progressDrawable = typedArray.getDrawable(R.styleable.ProgressWebView_progressDrawable);
        typedArray.recycle();
        if (enableProgress) {
            progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, progressBarHeight);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setProgressDrawable(progressDrawable);
            addView(progressBar);
        }
    }

    public void setProgress(int progress) {
        if (enableProgress) {
            progressBar.setProgress(progress);
            if (progress == 100) {
                progressBar.setVisibility(GONE);
            } else if (progressBar.getVisibility() != VISIBLE) {
                progressBar.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (progressBar != null) {
            LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
            lp.x = l;
            lp.y = t;
            progressBar.setLayoutParams(lp);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
