package com.dev.kit.testapp.RxjavaAndRetrofitTest;


import java.util.List;

/**
 * Created by cuiyan on 16-10-18.
 */
public class NewsResult {
    private String retcode;
    private List<NewsItemInfo> data;

    public String getRetCode() {
        return retcode;
    }

    public List<NewsItemInfo> getData() {
        return data;
    }
}
