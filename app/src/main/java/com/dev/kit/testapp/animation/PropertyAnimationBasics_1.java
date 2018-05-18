package com.dev.kit.testapp.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/18.
 */
public class PropertyAnimationBasics_1 extends BaseStateViewActivity implements View.OnClickListener {
    private TextView tvAnimatedValue;
    private ValueAnimator exampleAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_animation_basics_1, contentRoot, false);
    }

    private void init() {
        setText(R.id.tv_title, "属性动画之ValueAnimator基础一");
        setOnClickListener(R.id.iv_left, this);
        setOnClickListener(R.id.bt_interpolator0, this);
        setOnClickListener(R.id.bt_interpolator1, this);
        setOnClickListener(R.id.bt_interpolator2, this);
        setOnClickListener(R.id.bt_interpolator3, this);

        tvAnimatedValue = findViewById(R.id.tv_animated_value);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(DisplayUtil.getScaleFactor() * 45);
        drawable.setColor(getResources().getColor(R.color.bg_supper_selected));
        tvAnimatedValue.setBackground(drawable);
        exampleAnimator = ValueAnimator.ofInt(0, 50).setDuration(500);
        exampleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtil.e("animatedFraction " + String.valueOf(animation.getAnimatedFraction()));
                tvAnimatedValue.setText(String.valueOf((int) animation.getAnimatedValue()));
            }
        });
        exampleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvAnimatedValue.setText("等待动画执行");
            }
        });
        exampleAnimator.setEvaluator(new TypeEvaluator<Integer>() {
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                LogUtil.e("evaluatedFraction: " + fraction);
                return (int) (startValue + (endValue - startValue) * fraction);
            }
        });
        setContentState(STATE_DATA_CONTENT);
    }

    private synchronized void startAnimator(int interpolatorFlag) {
        if (exampleAnimator.isRunning()) {
            showToast("动画正在执行，请稍后");
        } else {
            TimeInterpolator interpolator = null;
            switch (interpolatorFlag) {
                case 0: {
                    interpolator = new LinearInterpolator();
                    break;
                }
                case 1: {
                    interpolator = new AccelerateInterpolator();
                    break;
                }
                case 2: {
                    // 等同于默认加速插值器，即case1
                    interpolator = new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            float interpolation = input * input;
                            LogUtil.e("realFraction: " + input + " --- interpolatedFraction: " + interpolation);
                            return interpolation;
                        }
                    };
                    break;
                }
                case 3: {
                    // 等同于线性插值器
                    interpolator = new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return input;
                        }
                    };
                    break;
                }
            }
            exampleAnimator.setInterpolator(interpolator);
            exampleAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left: {
                finish();
                break;
            }
            case R.id.bt_interpolator0: {
                startAnimator(0);
                break;
            }
            case R.id.bt_interpolator1: {
                startAnimator(1);
                break;
            }
            case R.id.bt_interpolator2: {
                startAnimator(2);
                break;
            }
            case R.id.bt_interpolator3: {
                startAnimator(3);
                break;
            }
        }
    }
}
