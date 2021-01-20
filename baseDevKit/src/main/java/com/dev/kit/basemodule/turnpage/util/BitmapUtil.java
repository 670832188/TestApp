package com.dev.kit.basemodule.turnpage.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author yanglonghui
 */
public class BitmapUtil {

    public static Bitmap getFitBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        Bitmap bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
        float rW = (float) reqWidth / (float) bitmap.getWidth();
        float rH = (float) reqHeight / (float) bitmap.getHeight();
        float r = Math.min(rW, rH);
        int retWidth = (int) (bitmap.getWidth() * r);
        int retHeight = (int) (bitmap.getHeight() * r);
        Bitmap ret = Bitmap.createScaledBitmap(bitmap, retWidth, retHeight, false);
        bitmap.recycle();
        bitmap = null;
        return ret;
    }

    public static Bitmap getFitBitmapFromFile(String pathName, int resId, int reqWidth, int reqHeight) {
        Bitmap bitmap = decodeSampledBitmapFromFile(pathName, resId, reqWidth, reqHeight);
        float rW = (float) reqWidth / (float) bitmap.getWidth();
        float rH = (float) reqHeight / (float) bitmap.getHeight();
        float r = Math.min(rW, rH);
        int retWidth = (int) (bitmap.getWidth() * r);
        int retHeight = (int) (bitmap.getHeight() * r);
        Bitmap ret = Bitmap.createScaledBitmap(bitmap, retWidth, retHeight, false);
        bitmap.recycle();
        bitmap = null;
        return ret;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String pathName, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            if (inSampleSize == 1) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}
