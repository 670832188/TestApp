package com.dev.kit.basemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

/**
 * Created by cuiyan on 2018/7/13.
 */
public class WaveView extends View {
    private int width;
    private int height;
    private Paint wavePaint1;
    private Paint wavePaint2;
    private Paint wavePaint3;
    private Path wavePath1;
    private Path wavePath2;
    private Path wavePath3;
    private float offsetAngle1 = -(float) Math.PI / 4;
    private float offsetAngle2 = (float) Math.PI / 2;
    private float offsetAngle3 = (float) Math.PI;
    private float amplitude;
    private boolean isRunning;
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                postInvalidate();
            }
        }
    };

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint1.setStyle(Paint.Style.STROKE);
        wavePaint1.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePath1 = new Path();

        wavePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint2.setStyle(Paint.Style.STROKE);
        wavePaint2.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePath2 = new Path();

        wavePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint3.setStyle(Paint.Style.STROKE);
        wavePaint3.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePath3 = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (width > 0 && height > 0) {
            initPaintShader();
        }
    }

    private void initPaintShader() {
        amplitude = height / 2 - wavePaint1.getStrokeWidth();
        int[] colors = {Color.parseColor("#f5e8dc"), Color.parseColor("#f5dec6"), Color.parseColor("#f5e8dc")};
        float[] positions = {0.1f, 0.3f, 0.95f};
        LinearGradient gradient = new LinearGradient(0, 0, width, height, colors, positions, Shader.TileMode.CLAMP);
        wavePaint1.setShader(gradient);
        int[] colors1 = {Color.parseColor("#80ffa15f"), Color.parseColor("#ff8400"), Color.parseColor("#80ffa15f")};
        gradient = new LinearGradient(0, 0, width, height, colors1, positions, Shader.TileMode.CLAMP);
        wavePaint2.setShader(gradient);
        wavePaint3.setShader(gradient);

//        float blurMaskRadius = DisplayUtil.dp2px(20);
//        wavePaint1.setMaskFilter(new BlurMaskFilter(blurMaskRadius, BlurMaskFilter.Blur.SOLID));
//        wavePaint2.setMaskFilter(new BlurMaskFilter(blurMaskRadius, BlurMaskFilter.Blur.SOLID));
//        wavePaint3.setMaskFilter(new BlurMaskFilter(blurMaskRadius, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width <= 0 || height <= 0) {
            return;
        }
        wavePath1.reset();
        wavePath2.reset();
        wavePath3.reset();
        float tempAmplitude = amplitude;
        wavePath1.moveTo(0, (float) (Math.sin(offsetAngle1) * tempAmplitude + height / 2));
        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 4) + offsetAngle1;
            float y = (float) (Math.sin(angle) * tempAmplitude + height / 2);
            wavePath1.lineTo(i, y);
        }
        tempAmplitude = amplitude - Math.min(DisplayUtil.dp2px(5), height / 5);
        wavePath2.moveTo(0, (float) (Math.sin(offsetAngle2) * tempAmplitude + height / 2));
        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 4) + offsetAngle2;
            float y = (float) (Math.sin(angle) * tempAmplitude + height / 2);
            wavePath2.lineTo(i, y);
        }
        tempAmplitude = amplitude - 2 * Math.min(DisplayUtil.dp2px(5), height / 5);
        wavePath3.moveTo(0, (float) (Math.sin(offsetAngle3) * tempAmplitude + height / 2));
        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 4) + offsetAngle3;
            float y = (float) (Math.sin(angle) * tempAmplitude + height / 2);
            wavePath3.lineTo(i, y);
        }
        if (isRunning) {
            offsetAngle1 -= Math.PI * 0.05;
            offsetAngle2 -= Math.PI * 0.04;
            offsetAngle3 -= Math.PI * 0.03;
//            postDelayed(refreshRunnable, 10);
            invalidate();
        }

        canvas.drawPath(wavePath1, wavePaint1);
        canvas.drawPath(wavePath2, wavePaint2);
        canvas.drawPath(wavePath3, wavePaint3);
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            postInvalidate();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
        }
    }
}
