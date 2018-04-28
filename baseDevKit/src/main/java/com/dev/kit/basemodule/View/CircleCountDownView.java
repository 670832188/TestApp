package com.dev.kit.basemodule.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.ImageUtil;

/**
 * 圆形倒计时控件，圆形及背景图片将绘制在控件中央
 * 默认背景图片绘制区域大小与圆形大小之间的比例为90%
 * Created by cuiyan on 2018/4/28.
 */
public class CircleCountDownView extends View {
    private OnCountDownFinishListener onCountDownFinishListener;

    // 中间图片大小与控件大小之间的比例
    private int width;
    private int height;
    private int borderWidth;
    // 相邻时间节点倒计时的执行进度(取值0到1)
    private float timeProgress;
    private int initialCountDownValue;
    private int currentCountDownValue;

    private Paint circleBgPaint;
    private Paint circleImgPaint;
    private Matrix circleImgMatrix;
    private Bitmap circleImgBitmap;
    private int circleImgRadius;
    private BitmapShader circleImgBitmapShader;
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
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        circleBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleBgPaint.setStyle(Paint.Style.FILL);
        circleImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleImgPaint.setStyle(Paint.Style.FILL);
        circleImgMatrix = new Matrix();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        countDownAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1000);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleCountDownView);
        int centerImgSrc = typedArray.getResourceId(R.styleable.CircleCountDownView_centerImgSrc, R.mipmap.ic_radar);
        circleImgBitmap = ImageUtil.cropSquareBitmap(BitmapFactory.decodeResource(getResources(), centerImgSrc));
        circleBgPaint.setColor(typedArray.getColor(R.styleable.CircleCountDownView_circleBgColor, Color.WHITE));
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleCountDownView_circleBorderWidth, 0);
        typedArray.recycle();
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
            resizeCircleImg();
        }
    }

    private void resizeCircleImg() {
        circleImgMatrix.reset();
        circleImgRadius = (Math.min(width, height) - borderWidth) / 2;
        int circleDesiredDrawingWH = Math.min(width, height) - 2 * borderWidth;
        int actualCircleImgBitmapWH = circleImgBitmap.getWidth();
        float circleDrawingScale = (float) (Math.min(width, height) - 2 * borderWidth) / actualCircleImgBitmapWH;
        Matrix matrix = new Matrix();
        matrix.setScale(circleDrawingScale, circleDrawingScale, actualCircleImgBitmapWH / 2, actualCircleImgBitmapWH / 2);
        circleImgBitmap = Bitmap.createBitmap(circleImgBitmap, 0, 0, (int) circleImgBitmap.getWidth(), (int) circleImgBitmap.getHeight(), matrix, true);
        circleImgBitmapShader = new BitmapShader(circleImgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        int translationX = (width - circleDesiredDrawingWH) / 2;
        int translationY = (height - circleDesiredDrawingWH) / 2;
        circleImgMatrix.setTranslate(translationX, translationY);
    }

    private float lastTimeProcess;

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            return;
        }
        int centerX = width / 2;
        int centerY = height / 2;
        circleImgMatrix.postRotate((timeProgress - lastTimeProcess) * 360, centerX, centerY);
        circleImgBitmapShader.setLocalMatrix(circleImgMatrix);
        circleImgPaint.setShader(circleImgBitmapShader);
        if (borderWidth > 0) {
            canvas.drawCircle(centerX, centerY, Math.min(width, height) / 2, circleBgPaint);
        }
        canvas.drawCircle(centerX, centerY, circleImgRadius, circleImgPaint);
        lastTimeProcess = timeProgress;


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
