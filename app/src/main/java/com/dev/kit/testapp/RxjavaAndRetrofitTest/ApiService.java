package com.dev.kit.testapp.RxjavaAndRetrofitTest;


import com.dev.kit.basemodule.netRequest.Configs.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by cuiyan on 16-10-18.
 */
public interface ApiService {

    @GET(ApiConstants.QQ_SPORT_API)
    Observable<NewsResult> getQQSportNews(@Query("baid") String baid, @Query("apikey") String apiKey);
}
