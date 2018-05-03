package com.dev.kit.testapp.animation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class PropertyAnimationEntryActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_activity_entry);
        init();
    }

    private void init() {
        setOnClickListener(R.id.tv_count_down, this);
        setOnClickListener(R.id.tv_balls_fall_down, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_count_down: {
                startActivity(new Intent(PropertyAnimationEntryActivity.this, CountDownActivity.class));
                break;
            }
            case R.id.tv_balls_fall_down: {
                startActivity(new Intent(PropertyAnimationEntryActivity.this, BallsFallDownSimultaneously.class));
                break;
            }
        }
    }
}
