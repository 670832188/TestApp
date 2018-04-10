package com.dev.kit.basemodule.netRequest.util;

import android.support.annotation.NonNull;

import com.dev.kit.basemodule.util.LogUtil;

import org.json.JSONObject;

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
        int n;
        while (-1 != (n = inputStream.read(bufferWrite))) {
            output.write(bufferWrite, 0, n);
        }
        return output.toByteArray();
    }

    private Request rebuildRequest(Request request) throws IOException {
        Request newRequest;
        if ("POST".equals(request.method())) {
            newRequest = rebuildPostRequest(request);
        } else if ("GET".equals(request.method())) {
            newRequest = rebuildGetRequest(request);
        } else {
            newRequest = request;
        }
        LogUtil.e("requestUrl: " + newRequest.url().toString());
        return newRequest;
    }

    /**
     * 对post请求添加统一参数
     */
    private Request rebuildPostRequest(Request request) {
        if (commonParams == null || commonParams.size() == 0) {
            return request;
        }
        RequestBody originalRequestBody = request.body();
        assert originalRequestBody != null;
        RequestBody newRequestBody;
        if (originalRequestBody instanceof FormBody) { // 传统表单
            FormBody.Builder builder = new FormBody.Builder();
            FormBody requestBody = (FormBody) request.body();
            int fieldSize = requestBody == null ? 0 : requestBody.size();
            for (int i = 0; i < fieldSize; i++) {
                builder.add(requestBody.name(i), requestBody.value(i));
            }
            for (String paramKey : commonParams.keySet()) {
                builder.add(paramKey, commonParams.get(paramKey));
            }
            newRequestBody = builder.build();
        } else if (originalRequestBody instanceof MultipartBody) { // 文件
            MultipartBody requestBody = (MultipartBody) request.body();
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder();
            if (requestBody != null) {
                for (int i = 0; i < requestBody.size(); i++) {
                    multipartBodybuilder.addPart(requestBody.part(i));
                }
            }
            for (String paramKey : commonParams.keySet()) {
                multipartBodybuilder.addFormDataPart(paramKey, commonParams.get(paramKey));
            }
            newRequestBody = multipartBodybuilder.build();
        } else {
            try {
                JSONObject jsonObject;
                if (originalRequestBody.contentLength() == 0) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = new JSONObject(getParamContent(originalRequestBody));
                }
                for (String commonParamKey : commonParams.keySet()) {
                    jsonObject.put(commonParamKey, commonParams.get(commonParamKey));
                }
                newRequestBody = RequestBody.create(originalRequestBody.contentType(), jsonObject.toString());
                LogUtil.e(getParamContent(newRequestBody));

            } catch (Exception e) {
                newRequestBody = originalRequestBody;
                e.printStackTrace();
            }
        }
        return request.newBuilder().method(request.method(), newRequestBody).build();
    }

    /**
     * 获取常规post请求参数
     */
    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

    /**
     * 对get请求做统一参数处理
     */
    private Request rebuildGetRequest(Request request) {
        if (commonParams == null || commonParams.size() == 0) {
            return request;
        }
        String url = request.url().toString();
        int separatorIndex = url.lastIndexOf("?");
        StringBuilder sb = new StringBuilder(url);
        if (separatorIndex == -1) {
            sb.append("?");
        }
        for (String commonParamKey : commonParams.keySet()) {
            sb.append("&").append(commonParamKey).append("=").append(commonParams.get(commonParamKey));
        }
        Request.Builder requestBuilder = request.newBuilder();
        return requestBuilder.url(sb.toString()).build();
    }
}
