package com.dev.kit.basemodule.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dev.kit.basemodule.BuildConfig;

/**
 * image工具类
 * Created by cy on 2017/12/5.
 */

public class ImageUtil {
    public static synchronized void showImg(Context context, String imgUri, @DrawableRes int defaultSrcId, @DrawableRes int errorSrcId, ImageView target, float sizeMultiplier) {
        if (TextUtils.isEmpty(imgUri)) {
            return;
        }
        Glide.with(context)
                .load(imgUri)
                .apply(new RequestOptions()
                        .placeholder(defaultSrcId)
                        .error(errorSrcId))
                .thumbnail((sizeMultiplier > 0 && sizeMultiplier < 1) ? sizeMultiplier : 1.0f)
                .into(target);
    }

    public static synchronized void showImg(Context context, String imgUri, SimpleTarget<Drawable> target, @DrawableRes int defaultSrcId, @DrawableRes int errorSrcId, float sizeMultiplier) {
        if (TextUtils.isEmpty(imgUri)) {
            return;
        }
        Glide.with(context).load(imgUri)
                .apply(new RequestOptions()
                        .placeholder(defaultSrcId)
                        .error(errorSrcId))
                .thumbnail((sizeMultiplier > 0 && sizeMultiplier < 1) ? 1.0f : sizeMultiplier)
                .into(target);
    }

    public static synchronized void showImg(Context context, String imgUri, @DrawableRes int defaultSrcId, @DrawableRes int errorSrcId, SimpleTarget<Bitmap> target, float sizeMultiplier) {
        if (TextUtils.isEmpty(imgUri)) {
            return;
        }
        Glide.with(context).asBitmap().load(imgUri)
                .apply(new RequestOptions()
                        .placeholder(defaultSrcId)
                        .error(errorSrcId))
                .thumbnail((sizeMultiplier > 0 && sizeMultiplier < 1) ? 1.0f : sizeMultiplier)
                .into(target);
    }
}
