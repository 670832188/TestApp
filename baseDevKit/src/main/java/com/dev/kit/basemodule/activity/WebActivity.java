package com.dev.kit.basemodule.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.StringUtil;
import com.dev.kit.basemodule.view.ProgressWebView;

import java.util.Stack;


/**
 * web Activity
 * Created by cuiyan on 2019/5/5.
 */

public class WebActivity extends BaseActivity {
    public static final String LOAD_URL = "loadUrl";
    public static final String TITLE = "title";
    private ProgressWebView webView;
    private Stack<String> urlStack = new Stack<>();
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initData();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        setOnClickListener(R.id.iv_left, v -> finish());
        webView = findViewById(R.id.web_view);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                webView.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (StringUtil.isEmpty(WebActivity.this.title)) {
                    setText(R.id.tv_title, title);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) return false;
                if (!url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {//网页加载结束的时候
                super.onPageFinished(view, url);
                String originalUrl;
                if (!StringUtil.isEmpty(originalUrl = view.getOriginalUrl())) {
                    if (urlStack.empty() || !urlStack.peek().equals(originalUrl)) {
                        urlStack.add(originalUrl);
                    }
                }
            }

            @TargetApi(23)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        WebSettings settings = webView.getSettings();
//        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true); //设置可以访问文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(false);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        String loadUrl = intent.getStringExtra(LOAD_URL);
        title = intent.getStringExtra(TITLE);
        if (!StringUtil.isEmpty(title)) {
            setText(R.id.tv_title, title);
        }
        if (!StringUtil.isEmpty(loadUrl)) {
            webView.loadUrl(loadUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (!urlStack.empty()) {
            urlStack.pop();
        }
        if (urlStack.empty()) {
            super.onBackPressed();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
