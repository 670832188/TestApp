package com.dev.kit.testapp.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2020/5/20.
 */
public class BitmapTestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_test);
        bitMapTest();
    }

    private void bitMapTest() {
        Resources resources = getResources();
        Bitmap bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.iv_ts);
        Bitmap bitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.iv_ts1);
        Bitmap bitmap3 = BitmapFactory.decodeResource(resources, R.mipmap.iv_ts2);
        Bitmap bitmap4 = BitmapFactory.decodeResource(resources, R.mipmap.iv_ts3);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = 320;
        options.inScreenDensity = 320;
        Bitmap bitmap5 = BitmapFactory.decodeResource(resources, R.mipmap.iv_ts, options);

        LogUtil.e("mytag", "bitmapInfo:\n"
                + "drawable: " + bitmap1.getWidth() + " " + bitmap1.getHeight() + " " + bitmap1.getByteCount() + "\n"
                + "mipmap-xhdpi: " + bitmap2.getWidth() + " " + bitmap2.getHeight() + " " + bitmap2.getByteCount() + "\n"
                + "mipmap-xxhdpi: " + bitmap3.getWidth() + " " + bitmap3.getHeight() + " " + bitmap3.getByteCount() + "\n"
                + "mipmap-xxxhdpi: " + bitmap4.getWidth() + " " + bitmap4.getHeight() + " " + bitmap4.getByteCount());
    }
}
