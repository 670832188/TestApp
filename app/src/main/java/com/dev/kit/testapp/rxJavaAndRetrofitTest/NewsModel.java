package com.dev.kit.testapp.rxJavaAndRetrofitTest;

import com.dev.kit.basemodule.netRequest.ApiConstants;
import com.dev.kit.basemodule.netRequest.CommonRequestModel;
import com.dev.kit.basemodule.netRequest.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.RequestServiceUtil;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

/**
 * @author cuiyan
 * Created on 2020/12/25.
 */
public class NewsModel extends CommonRequestModel<NewsResult> {

    public NewsModel(@NonNull NetRequestCallback<NewsResult> callback) {
        super(callback);
    }

    @Override
    public Observable<NewsResult> getObservable() {
        return RequestServiceUtil.createService(ApiService.class).getNews(ApiConstants.JUHE_NEWS_URL, Constant.JUHE_NEWS_API_KEY);
    }

}
