package com.dev.kit.testapp.rxJavaAndRetrofitTest;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by cuiyan on 16-10-18.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST
    Observable<NewsResult> getNews(@Url String url, @Field("key") String apiKey);

}
