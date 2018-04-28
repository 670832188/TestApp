package com.dev.kit.testapp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

public class CircleCountDownView extends View {
    private OnCountDownFinishListener onCountDownFinishListener;
    private int circleRadius;
    private int width;
    private int height;
    // 相邻时间节点倒计时的执行进度(取值0到1)
    private float timeProgress;
    private int initialCountDownValue;
    private int currentCountDownValue;
    private Paint paint;
    private Matrix matrix;
    private Bitmap circleBitmap;
    private Paint textPaint;
    private float textSize = DisplayUtil.dp2px(12);

    private ValueAnimator countDownAnimator;

    public CircleCountDownView(Context context) {
        this(context, null);
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        matrix = new Matrix();
        circleBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_praise);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        countDownAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1000);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
    }

    public void setStartCountValue(int initialCountDownValue) {
        this.initialCountDownValue = initialCountDownValue;
        this.currentCountDownValue = initialCountDownValue;
        invalidate();
    }

    public void reset() {
        countDownAnimator.cancel();
        currentCountDownValue = initialCountDownValue;
        invalidate();
    }

    public void pause() {
        countDownAnimator.pause();
    }

    public void setOnCountDownFinishListener(OnCountDownFinishListener onCountDownFinishListener) {
        this.onCountDownFinishListener = onCountDownFinishListener;
    }

    public void startCountDown() {
        if (countDownAnimator.isPaused()) {
            countDownAnimator.resume();
            return;
        }
        if (currentCountDownValue > 0) {
            countDownAnimator.setRepeatCount(currentCountDownValue - 1);
            countDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    timeProgress = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            countDownAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationRepeat(Animator animation) {
                    currentCountDownValue--;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (onCountDownFinishListener != null) {
                        onCountDownFinishListener.onCountDownFinish();
                    }
                }
            });
            countDownAnimator.start();
        } else if (onCountDownFinishListener != null) {
            onCountDownFinishListener.onCountDownFinish();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
//
        if (width > 0 && height > 0) {
            matrix.reset();
            int circleDesiredDrawingWH = Math.min(circleBitmap.getWidth(), circleBitmap.getHeight());
            if (circleDesiredDrawingWH > Math.min(width, height)) {
                float circleDrawingScale = (float) Math.min(width, height) / circleDesiredDrawingWH;
                matrix.setScale(circleDrawingScale, circleDrawingScale);
            } else {
                int translationX = (width - circleDesiredDrawingWH) / 2;
                int translationY = (height - circleDesiredDrawingWH) / 2;
                matrix.setTranslate(translationX, translationY);
            }
//            int circleDrawingWH = Math.min(Math.min(width, height), circleDesiredDrawingWH);
//            float circleDrawingScale = (float) Math.min(width, height) / circleDrawingWH;
//            int translationX = (width - circleDrawingWH) / 2;
//            int translationY = (height - circleDrawingWH) / 2;
//            matrix.reset();
//            matrix.setScale(circleDrawingScale, circleDrawingScale);
//            matrix.preTranslate(translationX > 0 ? translationX : 0, translationY > 0 ? translationY : 0);
        }
    }

    private float lastTimeProcess;

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            return;
        }
        int centerX = width / 2;
        int centerY = height / 2;
        canvas.drawCircle(centerX, centerY, Math.min(width, height) / 2, paint);
        matrix.postRotate((timeProgress - lastTimeProcess) * 360, centerX, centerY);
        lastTimeProcess = timeProgress;
        canvas.drawBitmap(circleBitmap, matrix, paint);

        // 绘制倒计时时间
        // current
        textPaint.setTextSize(textSize);
        String currentTimePoint = currentCountDownValue + "s";
        float textWidth = textPaint.measureText(currentTimePoint);
        float x = centerX - textWidth / 2;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float verticalBaseline = (height - fontMetrics.bottom - fontMetrics.top) / 2;
        float y = verticalBaseline - timeProgress * (centerY);
        textPaint.setAlpha((int) (255 - timeProgress * 255));
        canvas.drawText(currentTimePoint, x, y, textPaint);

        // next
        String nextTimePoint = (currentCountDownValue - 1) + "s";
        textWidth = textPaint.measureText(nextTimePoint);
        x = centerX - textWidth / 2;
        y = y + height / 2;
        textPaint.setAlpha((int) (timeProgress * 255));
        canvas.drawText(nextTimePoint, x, y, textPaint);
    }

    public interface OnCountDownFinishListener {
        void onCountDownFinish();
    }

}
