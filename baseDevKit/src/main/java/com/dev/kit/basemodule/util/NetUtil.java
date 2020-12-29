package com.dev.kit.basemodule.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;


/**
 * Author: cuiyan
 * Date:   16/7/16 16:47
 * Desc:
 */
public class NetUtil {
    public synchronized static boolean isNetError(Throwable e, Context context) {
        return e instanceof HttpException || e instanceof SocketTimeoutException || e instanceof ConnectException || !isNetAvailable(context);
    }

    public synchronized static boolean isNetAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable();
    }
}