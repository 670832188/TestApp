package com.dev.kit.testapp.animation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.view.CircleCountDownView;
import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class CountDownActivity extends BaseStateViewActivity implements View.OnClickListener {
    private CircleCountDownView countDownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_count_down, contentRoot, false);
    }

    private void init() {
        setContentState(STATE_DATA_CONTENT);
        setText(R.id.tv_title, "属性动画");
        setOnClickListener(R.id.iv_left, this);
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
            case R.id.iv_left: {
                finish();
                break;
            }
            case R.id.tv_restart: {
                countDownView.restart();
                break;
            }
        }
    }
}
