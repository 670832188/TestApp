package com.dev.kit.basemodule.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.ImageUtil;

/**
 * 圆形倒计时控件，圆形及背景图片将绘制在控件中央
 * Created by cuiyan on 2018/4/28.
 */
public class CircleCountDownView extends View {
    private OnCountDownFinishListener onCountDownFinishListener;

    private int width;
    private int height;
    private int padding;
    private int borderWidth;
    // 相邻时间节点倒计时的执行进度(取值0到1)
    private float timeProgress;
    private float totalTimeProgress;
    private int initialCountDownValue;
    private int currentCountDownValue;

    private Paint circleBorderPaint;
    private Paint circleProcessPaint;
    private RectF circleProgressRectF;

    private Paint circleImgPaint;
    private Matrix circleImgMatrix;
    private Bitmap circleImgBitmap;
    private int circleImgRadius;
    private BitmapShader circleImgBitmapShader;
    private Paint valueTextPaint;

    private ValueAnimator countDownAnimator;

    public CircleCountDownView(Context context) {
        this(context, null);
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        circleBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleBorderPaint.setStyle(Paint.Style.STROKE);
        circleProcessPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleProcessPaint.setStyle(Paint.Style.STROKE);
        circleImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleImgPaint.setStyle(Paint.Style.FILL);
        circleImgMatrix = new Matrix();
        valueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        countDownAnimator = ValueAnimator.ofFloat(0, 1).setDuration(1000);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleCountDownView);

        padding = typedArray.getDimensionPixelSize(R.styleable.CircleCountDownView_padding, DisplayUtil.dp2px(5));
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleCountDownView_circleBorderWidth, 0);
        circleBorderPaint.setStrokeWidth(borderWidth);
        circleBorderPaint.setColor(typedArray.getColor(R.styleable.CircleCountDownView_circleBorderColor, Color.WHITE));
        circleProcessPaint.setStrokeWidth(borderWidth);

        int centerImgSrc = typedArray.getResourceId(R.styleable.CircleCountDownView_centerImgSrc, R.mipmap.ic_radar);
        circleImgBitmap = ImageUtil.cropSquareBitmap(BitmapFactory.decodeResource(getResources(), centerImgSrc));

        valueTextPaint.setColor(typedArray.getColor(R.styleable.CircleCountDownView_valueTextColor, Color.WHITE));
        valueTextPaint.setTextSize(typedArray.getDimensionPixelSize(R.styleable.CircleCountDownView_valueTextSize, DisplayUtil.dp2px(13)));

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

    float lastInput;

    public void startCountDown() {
        if (countDownAnimator.isPaused()) {
            countDownAnimator.resume();
            return;
        }
        if (currentCountDownValue > 0) {
            countDownAnimator.setInterpolator(new LinearInterpolator());
            countDownAnimator.setRepeatCount(currentCountDownValue - 1);
            countDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    totalTimeProgress = (initialCountDownValue - currentCountDownValue + (float) animation.getAnimatedValue()) / initialCountDownValue;
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
        if (width > 0 && height > 0) {
            doCalculate();
        }
    }

    private void doCalculate() {
        circleImgMatrix.reset();
        circleImgRadius = (Math.min(width, height) - 2 * borderWidth - 2 * padding) / 2;
        float actualCircleImgBitmapWH = circleImgBitmap.getWidth();
        float circleDrawingScale = circleImgRadius * 2 / actualCircleImgBitmapWH;
        Matrix matrix = new Matrix();
        matrix.setScale(circleDrawingScale, circleDrawingScale, actualCircleImgBitmapWH / 2, actualCircleImgBitmapWH / 2);
        circleImgBitmap = Bitmap.createBitmap(circleImgBitmap, 0, 0, (int) circleImgBitmap.getWidth(), (int) circleImgBitmap.getHeight(), matrix, true);
        circleImgBitmapShader = new BitmapShader(circleImgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float translationX = (width - circleImgRadius * 2) / 2;
        float translationY = (height - circleImgRadius * 2) / 2;
        circleImgMatrix.setTranslate(translationX, translationY);

        if (borderWidth > 0) {
            // 外层进度条宽度（注意：需要减掉画笔宽度）
            float circleProgressWH = Math.min(width, height) - borderWidth - 2 * padding;
            float left = (width > height ? (width - height) / 2 : 0) + borderWidth / 2 + padding;
            float top = (height > width ? (height - width) / 2 : 0) + borderWidth / 2 + padding;
            float right = left + circleProgressWH;
            float bottom = top + circleProgressWH;
            circleProgressRectF = new RectF(left, top, right, bottom);
            circleProcessPaint.setShader(new LinearGradient(left, top, left + circleImgRadius * 2, top + circleImgRadius * 2, Color.BLUE, Color.GREEN, Shader.TileMode.MIRROR));
            circleProcessPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));//设置发光
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
        circleImgMatrix.postRotate((timeProgress - lastTimeProcess) * 360, centerX, centerY);
        circleImgBitmapShader.setLocalMatrix(circleImgMatrix);
        circleImgPaint.setShader(circleImgBitmapShader);
        if (borderWidth > 0) {
            canvas.drawCircle(centerX, centerY, Math.min(width, height) / 2 - borderWidth / 2 - padding, circleBorderPaint);
            canvas.drawArc(circleProgressRectF, 0, 360 * totalTimeProgress, true, circleProcessPaint);

        }
        canvas.drawCircle(centerX, centerY, circleImgRadius, circleImgPaint);
        lastTimeProcess = timeProgress;


        // 绘制倒计时时间
        // current
        String currentTimePoint = currentCountDownValue + "s";
        float textWidth = valueTextPaint.measureText(currentTimePoint);
        float x = centerX - textWidth / 2;
        Paint.FontMetrics fontMetrics = valueTextPaint.getFontMetrics();
        float verticalBaseline = (height - fontMetrics.bottom - fontMetrics.top) / 2;
        float y = verticalBaseline - timeProgress * (centerY);
        valueTextPaint.setAlpha((int) (255 - timeProgress * 255));
        canvas.drawText(currentTimePoint, x, y, valueTextPaint);

        // next
        String nextTimePoint = (currentCountDownValue - 1) + "s";
        textWidth = valueTextPaint.measureText(nextTimePoint);
        x = centerX - textWidth / 2;
        y = y + height / 2;
        valueTextPaint.setAlpha((int) (timeProgress * 255));
        canvas.drawText(nextTimePoint, x, y, valueTextPaint);
    }

    public interface OnCountDownFinishListener {
        void onCountDownFinish();
    }

}
