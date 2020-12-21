package com.dev.kit.testapp.activity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2020/5/20.
 */
public class BitmapTestActivity extends BaseActivity {
    private ImageView ivTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap_test);
        ivTest = findViewById(R.id.iv_test);
        ivTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private int count;
    private void test() {
        Drawable[] drawables;
        if (count % 3 == 0) {
            drawables  = new Drawable[]{getDrawable(R.mipmap.iv_ts1), getDrawable(R.mipmap.iv_ts2)};
        } else if (count % 3 == 1){
            drawables  = new Drawable[]{getDrawable(R.mipmap.iv_ts2), getDrawable(R.mipmap.iv_ts3)};
        } else {
            drawables  = new Drawable[]{getDrawable(R.mipmap.iv_ts3), getDrawable(R.mipmap.iv_ts1)};
        }
        TransitionDrawable drawable = new TransitionDrawable(drawables);
        ivTest.setImageDrawable(drawable);
        drawable.setCrossFadeEnabled(true);
        drawable.startTransition(600);
        count++;
    }
}
