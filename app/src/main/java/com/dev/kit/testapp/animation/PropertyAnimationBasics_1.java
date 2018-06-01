package com.dev.kit.testapp.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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
    private ObjectAnimator exampleAnimator;

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
        setText(R.id.tv_title, "属性动画基础（一）");
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
        tvAnimatedValue.measure(-1, -1);
        float translationX = DisplayUtil.getScreenWidth() - tvAnimatedValue.getMeasuredWidth();
        exampleAnimator = ObjectAnimator.ofFloat(tvAnimatedValue, "translationX", 0, translationX, 0).setDuration(1000);
        // 测试重复次数与重复模式对插值器的影响
//        exampleAnimator.setRepeatCount(1);
//        exampleAnimator.setRepeatMode(ValueAnimator.RESTART);
//        exampleAnimator.setRepeatMode(ValueAnimator.REVERSE);
        exampleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                LogUtil.e("animatedFraction " + animation.getAnimatedFraction() + " --- animatedValue: " + animatedValue);
            }
        });
        exampleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvAnimatedValue.setText("动画结束");
            }

            @Override
            public void onAnimationStart(Animator animation) {
                tvAnimatedValue.setText("动画开始");
            }
        });
//        exampleAnimator.setEvaluator(new TypeEvaluator<Float>() {
//            @Override
//            public Float evaluate(float fraction, Float startValue, Float endValue) {
//                float evaluatedValue = startValue + (endValue - startValue) * fraction;
//                LogUtil.e("evaluatedFraction: " + fraction + " --- evaluatedValue: " + evaluatedValue);
//                return evaluatedValue;
//            }
//        });
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
                            LogUtil.e("accelerateRealFraction: " + input + " --- interpolatedFraction: " + interpolation);
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
                            LogUtil.e("linearRealFraction: " + input);
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
