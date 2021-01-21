package com.dev.kit.basemodule.turnpage1;


import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

/**
 * @author cuiyan
 * Created on 2021/1/20.
 */
public class ImgData {
    private final String uri;
    private final int holderRes;

    public ImgData(String uri, @DrawableRes int holderRes) {
        this.uri = uri;
        this.holderRes = holderRes;
    }

    public String getUri() {
        return uri;
    }

    public int getHolderRes() {
        return holderRes;
    }
}
