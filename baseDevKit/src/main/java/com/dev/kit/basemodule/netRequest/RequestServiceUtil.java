package com.dev.kit.basemodule.netRequest;

import android.text.TextUtils;

import com.dev.kit.basemodule.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cuiyan on 16/6/3 11:07
 */
public class RequestServiceUtil {
    private static final int DEFAULT_TIMEOUT = 10;
//    private static final CommonInterceptor interceptor = new CommonInterceptor();
//    private static final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
//            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
//    // Retrofit要求baseUrl以 '/' 为结尾
//    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());


    private static final Map<String, Retrofit> baseUrl2Retrofit = new HashMap<>();

    public static synchronized <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        Retrofit retrofit;
        synchronized (baseUrl2Retrofit) {
            retrofit = baseUrl2Retrofit.get(baseUrl);
            if (retrofit == null) {
                CommonInterceptor interceptor = new CommonInterceptor();
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                // Retrofit要求baseUrl以 '/' 为结尾
                Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create());
                if (!TextUtils.isEmpty(baseUrl)) {
                    retrofitBuilder.baseUrl(baseUrl);
                } else {
                    retrofitBuilder.baseUrl(BuildConfig.BASE_URL);
                }
                clientBuilder.interceptors().clear();
                clientBuilder.interceptors().add(interceptor);

                // 设置证书
//                try {
//                    clientBuilder.sslSocketFactory(RqbTrustManager.getInstance().getSSLSocketFactory("BKS", R.raw.rqb_ssl));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                OkHttpClient client = clientBuilder.build();
                retrofit = retrofitBuilder.client(client).build();
                baseUrl2Retrofit.put(baseUrl, retrofit);
            }
        }
        return retrofit.create(serviceClass);
    }

    public static class RetrofitBuilder {
        private String baseUrl;
        private long timeout;
        private Interceptor interceptor;
        private Converter.Factory converterFactory;

        public RetrofitBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RetrofitBuilder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public RetrofitBuilder interceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        private RetrofitBuilder converterFactory(Converter.Factory converterFactory) {
            this.converterFactory = converterFactory;
            return this;
        }

        public Retrofit create() {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS);
            if (!(interceptor instanceof CommonInterceptor)) {
                clientBuilder.addInterceptor(interceptor);
            } else {
                clientBuilder.addInterceptor(new CommonInterceptor());
            }
            // Retrofit要求baseUrl以 '/' 为结尾
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create());
            if (!(converterFactory instanceof GsonConverterFactory)) {

            }
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
            return retrofitBuilder.client(clientBuilder.build()).build();
        }
    }
}
