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
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
                    if (cornerRadius > 0) { // Glide4.+版本，圆角与CenterCrop冲突，通过MultiTransformation解决
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
        Glide.with(context)
                .load(url)
                .apply(options).into(target);
    }

    public static void loadImage(Context context, String url, Target target) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }
        RequestBuilder builder = null;
        Type type = target.getClass().getGenericSuperclass();
        System.out.println(type);
        ParameterizedType p = (ParameterizedType) type;
        if (p != null) {
            for (Type t : p.getActualTypeArguments()) {
                Class typeClass = (Class) t;
                if (typeClass == Bitmap.class) {
                    builder = Glide.with(context)
                            .asBitmap()
                            .load(url);
                    break;
                } else if (typeClass == GifDrawable.class) {
                    builder = Glide.with(context)
                            .asGif()
                            .load(url);
                    break;
                } else if (typeClass == File.class) {
                    builder = Glide.with(context)
                            .asFile()
                            .load(url);
                    break;
                }
            }
        }
        if (builder == null) {
            builder = Glide.with(context).load(url);
        }
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

    public static File getCache(Context context, String url) {
        File cacheFile = null;
        RequestFutureTarget<File> futureTarget = (RequestFutureTarget<File>) Glide.with(context).downloadOnly().load(url).apply(new RequestOptions().onlyRetrieveFromCache(true)).submit();
        Class<?> class1 = futureTarget.getClass();
        Field field;
        try {
            synchronized (futureTarget) {
                futureTarget.wait();
            }
            field = class1.getDeclaredField("resource");
            field.setAccessible(true);//开放权限
            cacheFile = (File) field.get(futureTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cacheFile;
    }

    public static boolean isCache(Context context, String url) {
        return getCache(context, url) != null;
    }
}
