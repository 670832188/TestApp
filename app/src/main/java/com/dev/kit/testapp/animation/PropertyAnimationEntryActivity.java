package com.dev.kit.testapp.animation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class PropertyAnimationEntryActivity extends BaseStateViewActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_property_activity_entry, contentRoot, false);
    }

    private void init() {
        setText(R.id.tv_title, "属性动画");
        setOnClickListener(R.id.iv_left, this);
        setOnClickListener(R.id.tv_count_down, this);
        setOnClickListener(R.id.tv_balls_fall_down, this);
        setOnClickListener(R.id.tv_property_animator_basics_1, this);
        setContentState(STATE_DATA_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left: {
                finish();
                break;
            }
            case R.id.tv_count_down: {
                startActivity(new Intent(PropertyAnimationEntryActivity.this, CountDownActivity.class));
                break;
            }
            case R.id.tv_balls_fall_down: {
                startActivity(new Intent(PropertyAnimationEntryActivity.this, BallsFallDownSimultaneously.class));
                break;
            }
            case R.id.tv_property_animator_basics_1: {
                startActivity(new Intent(this, PropertyAnimationBasics_1.class));
                break;
            }
        }
    }
}
