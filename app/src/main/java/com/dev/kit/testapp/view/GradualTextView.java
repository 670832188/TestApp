package com.dev.kit.testapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.AttributeSet;

import com.dev.kit.testapp.R;

/**
 * 文本颜色过渡渐变TextView
 * 临时创建，比较粗糙，只支持文字居中
 * Created by cy on 2017/11/8.
 */

public class GradualTextView extends AppCompatTextView {
    private Paint startTextPaint;
    private Paint endTextPaint;
    private Rect textBound = new Rect();

    public GradualTextView(Context context) {
        this(context, null);
    }

    public GradualTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradualTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int startTextColor = Color.parseColor("#FFFFFF");
        int endTextColor = Color.parseColor("#666666");
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GradualTextView, defStyleAttr, 0);
        if (typedArray != null) {
            int count = typedArray.getIndexCount();
            for (int i = 0; i < count; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.GradualTextView_startTextColor: {
                        startTextColor = typedArray.getColor(attr, startTextColor);
                        break;
                    }
                    case R.styleable.GradualTextView_endTextColor: {
                        endTextColor = typedArray.getColor(attr, endTextColor);
                        break;
                    }
                }
            }
            typedArray.recycle();
            startTextPaint = new Paint(getPaint());
            startTextPaint.setColor(startTextColor);
            endTextPaint = new Paint(getPaint());
            endTextPaint.setColor(endTextColor);
            endTextPaint.setAlpha(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String mTipText = getText().toString();
        startTextPaint.getTextBounds(mTipText, 0, mTipText.length(), textBound);
        endTextPaint.getTextBounds(mTipText, 0, mTipText.length(), textBound);
        canvas.drawText(mTipText, getMeasuredWidth() / 2 - textBound.width() / 2, getMeasuredHeight() / 2 + textBound.height() / 2, startTextPaint);
        canvas.drawText(mTipText, getMeasuredWidth() / 2 - textBound.width() / 2, getMeasuredHeight() / 2 + textBound.height() / 2, endTextPaint);
    }

    public void setTextColor(@ColorInt int startColor, @ColorInt int endTextColor) {
        startTextPaint.setColor(startColor);
        endTextPaint.setColor(endTextColor);
        postInvalidate();
    }

    public void setStartTextColor(@ColorInt int startColor) {
        startTextPaint.setColor(startColor);
        postInvalidate();
    }

    public void setEndTextColor(@ColorInt int endTextColor) {
        endTextPaint.setColor(endTextColor);
        postInvalidate();
    }

    /**
     * 设置两种画笔所占比重
     *
     * @param ratio endPaint比重
     */
    public void changeRatio(float ratio) {
        startTextPaint.setAlpha((int) ((1f - ratio) * 255));
        endTextPaint.setAlpha((int) (ratio * 255));
        invalidate();
    }
}
