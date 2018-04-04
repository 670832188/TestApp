package com.dev.kit.basemodule.netRequest.util;

import android.support.annotation.NonNull;

import com.dev.kit.basemodule.util.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 网络请求拦截器
 * Created by cuiyan on 16/6/8 12:07.
 */
public class CommonInterceptor implements Interceptor {

    private static Map<String, String> commonParams;

    public synchronized static void setCommonParam(Map<String, String> commonParams) {
        if (commonParams != null) {
            if (CommonInterceptor.commonParams != null) {
                CommonInterceptor.commonParams.clear();
            } else {
                CommonInterceptor.commonParams = new HashMap<>();
            }
            for (String paramKey : commonParams.keySet()) {
                CommonInterceptor.commonParams.put(paramKey, commonParams.get(paramKey));
            }
        }
    }

    public synchronized static void updateOrInsertCommonParam(@NonNull String paramKey, @NonNull String paramValue) {
        if (commonParams == null) {
            commonParams = new HashMap<>();
        }
        commonParams.put(paramKey, paramValue);
    }

    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        Request request = rebuildRequest(chain.request());
        Response response = chain.proceed(request);
        // 输出返回结果
        try {
            Charset charset;
            charset = Charset.forName("UTF-8");
            ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
            Reader jsonReader = new InputStreamReader(responseBody.byteStream(), charset);
            BufferedReader reader = new BufferedReader(jsonReader);
            StringBuilder sbJson = new StringBuilder();
            String line = reader.readLine();
            do {
                sbJson.append(line);
                line = reader.readLine();
            } while (line != null);
            LogUtil.e("response: " + sbJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage(), e);
        }
//        saveCookies(response, request.url().toString());
        return response;
    }


    public static byte[] toByteArray(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        InputStream inputStream = buffer.inputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] bufferWrite = new byte[4096];
        int n = 0;
        while (-1 != (n = inputStream.read(bufferWrite))) {
            output.write(bufferWrite, 0, n);
        }
        return output.toByteArray();
    }

    private Request rebuildRequest(Request request) throws IOException {
        Request.Builder requestBuilder;
        if ("POST".equals(request.method())) {
            RequestBody requestBody = rebuildBody(request);
            requestBuilder = request.newBuilder().method(request.method(), requestBody);
        } else {
            requestBuilder = request.newBuilder().method(request.method(), request.body());
        }
        return requestBuilder.build();
    }


    /**
     * 获取请求参数
     */
    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }


    /**
     * 请求统一添加小米人app的参数
     */
    private RequestBody rebuildBody(Request request) {
        RequestBody originalRequestBody = request.body();
        if (originalRequestBody instanceof FormBody) {
            FormBody.Builder builder = new FormBody.Builder();
            FormBody requestBody = (FormBody) request.body();
            int fieldSize = requestBody == null ? 0 : requestBody.size();
            for (int i = 0; i < fieldSize; i++) {
                builder.add(requestBody.name(i), requestBody.value(i));
            }
            if (commonParams != null && commonParams.size() > 0) {
                for (String paramKey : commonParams.keySet()) {
                    builder.add(paramKey, commonParams.get(paramKey));
                }
            }
            return builder.build();
        } else if (originalRequestBody instanceof MultipartBody) {
            MultipartBody requestBody = (MultipartBody) request.body();
            if (requestBody == null) {
                return null;
            }
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder();
            for (int i = 0; i < requestBody.size(); i++) {
                multipartBodybuilder.addPart(requestBody.part(i));
            }
            return multipartBodybuilder.build();
        } else {
            return originalRequestBody;
        }
    }

}
