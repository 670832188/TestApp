package com.dev.kit.testapp.animation;

import android.os.Bundle;
import android.view.View;

import com.dev.kit.basemodule.View.CircleCountDownView;
import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class CountDownActivity extends BaseActivity implements View.OnClickListener {
    private CircleCountDownView countDownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);
        init();
    }

    private void init() {
        setOnClickListener(R.id.tv_restart, this);
        countDownView = findViewById(R.id.view_count);
        countDownView.setStartCountValue(10);
        countDownView.setCountDownListener(new CircleCountDownView.CountDownListener() {
            @Override
            public void onCountDownFinish() {

            }

            @Override
            public void restTime(long restTime) {
                LogUtil.e("restTime: " + restTime);
            }
        });
        countDownView.setAnimationInterpolator(new CircleCountDownView.AnimationInterpolator() {
            @Override
            public float getInterpolation(float inputFraction) {
                return inputFraction * inputFraction;
            }
        });
        countDownView.startCountDown();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_restart: {
                countDownView.restart();
                break;
            }
        }
    }
}
