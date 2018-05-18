package com.dev.kit.testapp.RxjavaAndRetrofitTest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.netRequest.Configs.ApiConstants;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.util.EasyBlur;
import com.dev.kit.basemodule.util.ImageUtil;
import com.dev.kit.basemodule.util.MIUIHelper;
import com.dev.kit.testapp.R;
import com.dev.kit.testapp.view.GradualTitleView;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * 网络请求组件使用示例
 * Created by cy on 2018/4/3.
 */

public class NetRequestDemoActivity extends BaseStateViewActivity {
    private NewsAdapter newsAdapter;
    private boolean isStatusBarLightMode;
    private float appBarLayoutOffsetRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MIUIHelper.setStatusBarLightMode(this, false);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_net_request, contentRoot, false);
    }

    private void init() {
        setVisibility(R.id.title_view, View.GONE);
        final GradualTitleView titleView = findViewById(R.id.gradual_title_view);
        titleView.setText("NBA资讯");
        final ImageView ivBanner = findViewById(R.id.iv_banner);
        String bannerUrl = "http://image.baidu.com/search/down?tn=download&ipn=dwnl&word=download&ie=utf8&fr=result&url=http%3A%2F%2Fp4.gexing.com%2FG1%2FM00%2FFF%2F0C%2FrBACE1YlwsbCH1EMAAJphzN4Pyw642_600x.jpg&thumburl=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D3335352464%2C37077284%26fm%3D27%26gp%3D0.jpg";
//        ImageUtil.showImg(this, bannerUrl, R.mipmap.ic_default_banner, R.mipmap.ic_default_banner, ivBanner, 1.f);
        ImageUtil.showImg(this, bannerUrl, R.mipmap.ic_default_banner, R.mipmap.ic_default_banner, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ivBanner.setImageBitmap(EasyBlur.getInstance()
                        .bitmap(resource)
                        .radius(1)
                        .scale(3)
                        .blur());
            }
        }, 1.f);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                appBarLayoutOffsetRatio = Math.abs((float) verticalOffset / (ivBanner.getHeight() - titleView.getHeight()));
                setStatusBarLightMode();
                titleView.changeRatio((float) Math.pow(appBarLayoutOffsetRatio, 3));
            }
        });
        newsAdapter = new NewsAdapter(this, new ArrayList<NewsItemInfo>());
        RecyclerView rvNews = findViewById(R.id.rv_news);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        rvNews.setAdapter(newsAdapter);
        getNews();
    }

    private void setStatusBarLightMode() {
        if (appBarLayoutOffsetRatio > 0.8 && !isStatusBarLightMode) {
            isStatusBarLightMode = MIUIHelper.setStatusBarLightMode(this, true);
        } else if (appBarLayoutOffsetRatio < 0.8 && isStatusBarLightMode) {
            if (MIUIHelper.setStatusBarLightMode(this, false)) {
                isStatusBarLightMode = false;
            }
        }
    }

    private void getNews() {
        NetRequestSubscriber<NewsResult> subscriber = new NetRequestSubscriber<>(new NetRequestCallback<NewsResult>() {
            @Override
            public void onStart() {
                setContentState(STATE_NET_PROGRESS);
            }

            @Override
            public void onSuccess(@NonNull NewsResult newsResult) {
                if (newsResult.getData() != null && newsResult.getData().size() > 0) {
                    setContentState(STATE_DATA_CONTENT);
                    newsAdapter.updateDataList(newsResult.getData());
                } else {
                    setContentState(STATE_DATA_EMPTY);
                }
            }

            @Override
            public void onResultNull() {
                setContentState(STATE_NET_ERROR);
            }

            @Override
            public void onError(Throwable throwable) {
                setContentState(STATE_NET_ERROR);
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        }, this);

        Observable<NewsResult> observable = BaseServiceUtil.createService(ApiService.class, ApiConstants.JUHE_BASE_URL).getQQSportNews("69", Constant.JUHE_API_KEY);
        BaseController.sendRequest(this, subscriber, observable);
    }

}
