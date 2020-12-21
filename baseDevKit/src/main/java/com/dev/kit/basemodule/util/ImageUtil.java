package com.dev.kit.basemodule.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * image工具类
 * Created by cy on 2017/12/5.
 */

public class ImageUtil {
    public enum SCALE_TYPE {
        CENTER_INSIDE,
        CENTER_CROP,
        FIT_CENTER,
        CIRCLE_CROP,
        NONE
    }

    public static void loadImage(Context context, String url, ImageView target) {
        loadImage(context, url, SCALE_TYPE.NONE, 0, target, 0);
    }

    public static void loadImage(Context context, String url, SCALE_TYPE scaleType, int defaultRes, ImageView target) {
        loadImage(context, url, scaleType, defaultRes, defaultRes, target, 0);
    }

    public static void loadImage(Context context, String url, SCALE_TYPE scaleType, int defaultRes, ImageView target, int cornerRadius) {
        loadImage(context, url, scaleType, defaultRes, defaultRes, target, cornerRadius);
    }

    public static void loadImage(Context context, String url, SCALE_TYPE scaleType, int defaultRes, int errorRes, ImageView target, int cornerRadius) {
        loadImage(context, url, scaleType, defaultRes, errorRes, target, cornerRadius, 0, 0);
    }

    public static void loadImage(Context context, String url, SCALE_TYPE scaleType, int defaultRes, int errorRes, ImageView target, int cornerRadius, int width, int height) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }
        RequestOptions options = new RequestOptions();
        if (scaleType != null) {
            switch (scaleType) {
                case CENTER_INSIDE: {
                    options = options.centerInside();
                    break;
                }
                case CENTER_CROP: {
                    options = options.centerCrop();
                    if (cornerRadius > 0) { // Glide4.+至4.9版本，圆角与CenterCrop冲突，通过MultiTransformation解决
                        RoundedCorners roundedCorners = new RoundedCorners(DisplayUtil.dp2px(cornerRadius));
                        MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(new CenterCrop(), roundedCorners);
                        options = options.transform(multiTransformation);
                    } else {
                        options = options.centerCrop();
                    }
                    break;
                }
                case FIT_CENTER: {
                    options = options.fitCenter();
                    break;
                }
                case CIRCLE_CROP: {
                    options = options.circleCrop();
                    break;
                }
                default: {
                    options = options.downsample(DownsampleStrategy.NONE);
                    break;
                }
            }
        }
        if (defaultRes != 0) {
            options = options.placeholder(defaultRes);
            if (errorRes <= 0) {
                options = options.error(defaultRes);
            }
        }
        if (errorRes != 0) {
            options = options.error(errorRes);
        }
        if (cornerRadius > 0 && scaleType != SCALE_TYPE.CENTER_CROP) {
            options = options.transform(new RoundedCorners(DisplayUtil.dp2px(cornerRadius)));
        }
        if (width > 0 && height > 0) {
            options = options.override(width, height);
        }
        RequestBuilder builder = Glide.with(context)
                .load(url)
                .apply(options);
        builder.into(target);
    }

    public static <T> void loadImage(Context context, String url, SCALE_TYPE scaleType, int defaultRes, int errorRes, Target<T> target, int cornerRadius, int width, int height) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }
        RequestOptions options = new RequestOptions();
        if (scaleType != null) {
            switch (scaleType) {
                case CENTER_INSIDE: {
                    options = options.centerInside();
                    break;
                }
                case CENTER_CROP: {
                    options = options.centerCrop();
                    if (cornerRadius > 0) { // Glide4.+至4.9版本，圆角与CenterCrop冲突，通过MultiTransformation解决
                        RoundedCorners roundedCorners = new RoundedCorners(DisplayUtil.dp2px(cornerRadius));
                        MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(new CenterCrop(), roundedCorners);
                        options = options.transform(multiTransformation);
                    } else {
                        options = options.centerCrop();
                    }
                    break;
                }
                case FIT_CENTER: {
                    options = options.fitCenter();
                    break;
                }
                case CIRCLE_CROP: {
                    options = options.circleCrop();
                    break;
                }
                default: {
                    options = options.downsample(DownsampleStrategy.NONE);
                    break;
                }
            }
        }
        if (defaultRes != 0) {
            options = options.placeholder(defaultRes);
            if (errorRes <= 0) {
                options = options.error(defaultRes);
            }
        }
        if (errorRes != 0) {
            options = options.error(errorRes);
        }
        if (cornerRadius > 0 && scaleType != SCALE_TYPE.CENTER_CROP) {
            options = options.transform(new RoundedCorners(DisplayUtil.dp2px(cornerRadius)));
        }
        if (width > 0 && height > 0) {
            options = options.override(width, height);
        }
        RequestBuilder builder = Glide.with(context)
                .load(url)
                .apply(options);
        builder.into(target);
    }

    public static Bitmap cropSquareBitmap(Bitmap bitmap) {
        return cropSquareBitmap(bitmap, Math.min(bitmap.getWidth(), bitmap.getHeight()));
    }

    public static Bitmap cropSquareBitmap(Bitmap bitmap, int maxWH) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int newWH = Math.min(maxWH, Math.min(w, h));
        return Bitmap.createBitmap(bitmap, (w - newWH) / 2, (h - newWH) / 2, newWH, newWH);
    }
}
