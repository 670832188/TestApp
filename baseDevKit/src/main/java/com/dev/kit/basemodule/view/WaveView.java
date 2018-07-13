package com.dev.kit.basemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

import java.util.Random;

/**
 * Created by cuiyan on 2018/7/13.
 */
public class WaveView extends View {
    private int width = DisplayUtil.getScreenWidth() * 2;
    private int height = DisplayUtil.dp2px(60);
    private Paint wavePaint;
    private Path wavePath;
    //    private Path wavePath;
    private boolean isStart;

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
        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setStrokeWidth(DisplayUtil.dp2px(2));
        wavePath = new Path();
        resetPath();
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        width = w;
//        height = h;
//        if (width > 0 && height > 0) {
//            resetPath();
//        }
//    }


    private void resetPath() {
        wavePath.reset();
        wavePath.moveTo(0, height / 2);
        wavePath.cubicTo(width / 8, height / 4, width / 8 * 3, height / 4 * 3, width / 2, height / 2);
//        wavePath.moveTo(width / 2, height / 2);
        wavePath.cubicTo(width / 8 + width / 2, height / 4, width / 8 * 3 + width / 2, height / 4 * 3, width, height / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width <= 0 || height <= 0) {
            return;
        }
        canvas.drawPath(wavePath, wavePaint);
        postDelayed(refreshRunnable, 200);
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            scrollBy(10, 0);
        }
    };

    public void start() {
        if (!isStart) {
            isStart = true;
            postInvalidate();
        }
    }

    public void stop() {
        if (isStart) {
            isStart = false;
        }
    }
}
