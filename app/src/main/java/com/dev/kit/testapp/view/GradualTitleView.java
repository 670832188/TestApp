package com.dev.kit.testapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dev.kit.testapp.R;


/**
 * 透明度渐变titleView
 * Created by cy on 2017/11/9.
 */

public class GradualTitleView extends RelativeLayout {
    private GradualImageView ivBack;
    private GradualImageView ivRight;
    private GradualTextView tvTitle;
    private Paint bgPaint;
    private static final int INVALID_TEXT_COLOR = -100;
    private static final int INVALID_RES_ID = -1;
    private int startTextColor = INVALID_RES_ID;
    private int endTextColor = INVALID_RES_ID;
    private
    @DrawableRes
    int startLeftBitmap = INVALID_TEXT_COLOR;
    private
    @DrawableRes
    int endLeftBitmap = INVALID_TEXT_COLOR;
    @DrawableRes
    int startRightBitmap = INVALID_TEXT_COLOR;
    private
    @DrawableRes
    int endRightBitmap = INVALID_TEXT_COLOR;
    private boolean isRightImgShown;

    public GradualTitleView(Context context) {
        this(context, null);
    }

    public GradualTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradualTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GradualTitleView);
        int startBgAlpha = 255;
        int bgColor = context.getResources().getColor(R.color.color_white);
        int attrsCount = typedArray.getIndexCount();
        for (int index = 0; index < attrsCount; index++) {
            int attr = typedArray.getIndex(index);
            switch (attr) {
                case R.styleable.GradualTitleView_gt_targetBgColor:
                    bgColor = typedArray.getColor(attr, bgColor);
                    break;
                case R.styleable.GradualTitleView_gt_startAlpha: {
                    startBgAlpha = typedArray.getInt(attr, startBgAlpha);
                    break;
                }
                case R.styleable.GradualTitleView_gt_startTextColor: {
                    startTextColor = typedArray.getColor(attr, Color.parseColor("#FFFFFF"));
                    break;
                }
                case R.styleable.GradualTitleView_gt_endTextColor: {
                    endTextColor = typedArray.getColor(attr, Color.parseColor("#666666"));
                    break;
                }
                case R.styleable.GradualTitleView_gt_startLeftImgScr: {
                    startLeftBitmap = typedArray.getResourceId(attr, R.mipmap.ic_back_black);
                    break;
                }
                case R.styleable.GradualTitleView_gt_endLeftImgSrc: {
                    endLeftBitmap = typedArray.getResourceId(attr, R.mipmap.ic_back_white);
                    break;
                }
                case R.styleable.GradualTitleView_gt_startRightImgScr: {
                    startRightBitmap = typedArray.getResourceId(attr, R.mipmap.ic_back_black);
                    break;
                }
                case R.styleable.GradualTitleView_gt_endRightImgSrc: {
                    endRightBitmap = typedArray.getResourceId(attr, R.mipmap.ic_back_white);
                    break;
                }
            }
        }
        typedArray.recycle();
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor);
        bgPaint.setAlpha(startBgAlpha);
        initChildView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRightImgVisibility(int visibility) {
        ivRight.setVisibility(visibility);
        isRightImgShown = visibility == View.VISIBLE;
    }

    private void initChildView() {
        inflate(getContext(), R.layout.layout_gradual_title, this);
        ivBack = (GradualImageView) findViewById(R.id.iv_back);
        ivRight = (GradualImageView) findViewById(R.id.iv_right);
        tvTitle = (GradualTextView) findViewById(R.id.tv_title);
        if (startTextColor != INVALID_TEXT_COLOR) {
            tvTitle.setStartTextColor(startTextColor);
        }
        if (endTextColor != INVALID_TEXT_COLOR) {
            tvTitle.setEndTextColor(endTextColor);
        }
        if (startLeftBitmap != INVALID_RES_ID) {
            ivBack.setStartBitmap(startLeftBitmap);
        }
        if (endLeftBitmap != INVALID_RES_ID) {
            ivBack.setEndBitmap(endLeftBitmap);
        }
        if (startRightBitmap != INVALID_RES_ID) {
            ivRight.setStartBitmap(startRightBitmap);
        }
        if (endRightBitmap != INVALID_RES_ID) {
            ivRight.setEndBitmap(endRightBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
    }

    private void drawRect(Canvas canvas) {
        Rect rect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawRect(rect, bgPaint);
    }

    public void changeRatio(float ratio) {
        bgPaint.setAlpha((int) (ratio * 255));
        ivBack.changeRatio(ratio);
        if (isRightImgShown) {
            ivRight.changeRatio(ratio);
        }
        tvTitle.changeRatio(ratio);
        invalidate();
    }

    public void setText(String text) {
        tvTitle.setText(text);
    }
}
