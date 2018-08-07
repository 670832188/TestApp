package com.dev.kit.basemodule.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.shaohui.advancedluban.Luban;

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

    public static synchronized Bitmap cropSquareBitmap(Bitmap bitmap) {//从中间截取一个正方形
        return cropSquareBitmap(bitmap, Integer.MAX_VALUE);
    }

    public static synchronized Bitmap cropSquareBitmap(Bitmap bitmap, int maxWH) {//从中间截取一个正方形
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = Math.min(maxWH, Math.min(w, h));
        return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2, (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

    public static synchronized void compressImg(Context context, File imgFile, int compressMode, @NonNull final CompressImgListener listener) {
        Luban.compress(context, imgFile)
                .putGear(compressMode)
                .asObservable()
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        List<File> fileList = new ArrayList<>();
                        fileList.add(file);
                        listener.onSuccess(fileList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.onFailed();
                    }
                });
    }

    public static synchronized void compressImgByFiles(Context context, List<File> imgFileList, int compressMode, @NonNull final CompressImgListener listener) {
        Luban.compress(context, imgFileList)           // 加载多张图片
                .putGear(compressMode)
                .asListObservable()
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        listener.onSuccess(files);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onFailed();
                    }
                });
    }

    public static synchronized void compressImgByPaths(Context context, List<String> imgPathList, @NonNull final CompressImgListener listener) {
        List<File> imgFileList = new ArrayList<>();
        for (String path : imgPathList) {
            imgFileList.add(new File(path));
        }
        Luban luban;
        File cacheFile = getCacheDir();
        if (cacheFile != null) {
            luban = Luban.compress(imgFileList, cacheFile);
        } else {
            luban = Luban.compress(context, imgFileList);
        }

        luban.putGear(Luban.THIRD_GEAR)
                .asListObservable()
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        listener.onSuccess(files);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.onFailed();
                    }
                });
    }

    private static File getCacheDir() {
        String cacheDirName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "testCache";
        File cacheDirFile = new File(cacheDirName);
        if (cacheDirFile.exists()) {
            return cacheDirFile;
        } else {
            if (cacheDirFile.mkdir()) {
                return cacheDirFile;
            }
        }
        return null;
    }

    public interface CompressImgListener {
        void onSuccess(List<File> compressedImgFileList);

        void onFailed();
    }
}
