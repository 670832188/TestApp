package com.dev.kit.testapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.testapp.R;

/**
 * 过渡渐变ImageView
 * Created by cy on 2017/11/8.
 */
public class GradualImageView extends View {

    private Paint startPaint;
    private Paint endPaint;
    private Bitmap startBitmap;
    private Bitmap endBitmap;

    private int viewWidth;
    private int viewHeight;

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    public GradualImageView(Context context) {
        this(context, null);
    }

    public GradualImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradualImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GradualImageView);
        startBitmap = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.GradualImageView_startImgScr, R.mipmap.ic_back_black));
        endBitmap = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.GradualImageView_endImgSrc, R.mipmap.ic_back_white));
        typedArray.recycle();
        startPaint = new Paint();
        endPaint = new Paint();
        startPaint.setAntiAlias(true);
        startPaint.setFilterBitmap(true);
        endPaint.setAntiAlias(true);
        endPaint.setFilterBitmap(true);
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;
        int desiredWidth = getPaddingLeft() + getPaddingRight();
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = desiredWidth;
                break;
        }
        int desiredHeight = getPaddingTop() + getPaddingBottom();
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = desiredHeight;
                break;
        }
        setMeasuredDimension(width, height);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        RectF drawableRect;
        drawableRect = getDrawableRect(startBitmap);
        canvas.drawBitmap(startBitmap, null, drawableRect, startPaint);
        drawableRect = getDrawableRect(endBitmap);
        canvas.drawBitmap(endBitmap, null, drawableRect, endPaint);
    }

    /**
     * Drawable转化为Bitmap
     */
//    private Bitmap drawableToBitmap(Drawable drawable) {
//        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : viewWidth;
//        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : viewHeight;
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, width, height);
//        drawable.draw(canvas);
//        return bitmap;
//
//    }
    private RectF getDrawableRect(Bitmap bitmap) {
        int height = Math.min(bitmap.getHeight(), viewHeight - paddingTop - paddingBottom);
        int width = (int) (bitmap.getWidth() * ((float) height / bitmap.getHeight()));
        float left = paddingLeft + (viewWidth - paddingLeft - paddingRight - width) / 2;
        float top = (viewHeight - paddingTop - paddingBottom - height) / 2.0F + paddingTop;
        return new RectF(left, top, left + width, top + height);
    }

    public void setBitmap(@DrawableRes int startResId, @DrawableRes int endResId) {
        startBitmap = BitmapFactory.decodeResource(getResources(), startResId);
        endBitmap = BitmapFactory.decodeResource(getResources(), endResId);
        postInvalidate();
    }
    public void setStartBitmap(@DrawableRes int startResId) {
        startBitmap = BitmapFactory.decodeResource(getResources(), startResId);
        postInvalidate();
    }
    public void setEndBitmap(@DrawableRes int endResId) {
        endBitmap = BitmapFactory.decodeResource(getResources(), endResId);
        postInvalidate();
    }

    /**
     * 设置两种画笔所占比重
     *
     * @param ratio endPaint比重
     */
    public void changeRatio(float ratio) {
        startPaint.setAlpha((int) ((1f - ratio) * 255));
        endPaint.setAlpha((int) (ratio * 255));
        invalidate();
    }
}
