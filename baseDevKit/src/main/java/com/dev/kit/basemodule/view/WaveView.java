package com.dev.kit.basemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    private float offsetAngle3 = (float) Math.PI / 4 *3;
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
        wavePaint1.setColor(Color.YELLOW);
        wavePath1 = new Path();

        wavePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint2.setStyle(Paint.Style.STROKE);
        wavePaint2.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePaint2.setColor(Color.BLUE);
        wavePath2 = new Path();

        wavePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint3.setStyle(Paint.Style.STROKE);
        wavePaint3.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePaint3.setColor(Color.RED);
        wavePath3 = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
//        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width <= 0 || height <= 0) {
            return;
        }
        wavePath1.reset();
        wavePath2.reset();
        wavePath3.reset();
        float amplitude = (float) height / 2 - 10;
        wavePath1.moveTo(0, (float) (Math.sin(offsetAngle1) * amplitude + height / 2));

        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 3) + offsetAngle1;
            float y = (float) (Math.sin(angle) * amplitude + height / 2);
            wavePath1.lineTo(i, y);
        }
        wavePath2.moveTo(0, (float) (Math.sin(offsetAngle2) * amplitude + height / 2));
        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 3) + offsetAngle2;
            float y = (float) (Math.sin(angle) * amplitude + height / 2);
            wavePath2.lineTo(i, y);
        }
        wavePath3.moveTo(0, (float) (Math.sin(offsetAngle3) * amplitude + height / 2));
        for (float i = 0; i < width; i++) {
            float angle = (float) (i / width * Math.PI * 3) + offsetAngle3;
            float y = (float) (Math.sin(angle) * amplitude + height / 2);
            wavePath3.lineTo(i, y);
        }
        if (isRunning) {
            offsetAngle1 += Math.PI * 0.01;
            offsetAngle2 -= Math.PI * 0.01;
            offsetAngle3 += Math.PI * 0.015;
            postDelayed(refreshRunnable, 10);
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
