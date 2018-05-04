package com.dev.kit.basemodule.multiChildHistogram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.LogUtil;

import java.util.List;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class MultiChildHistogramView extends View {
    private int width;
    private int height;
    private int coordinateAxisWidth;
    private int coordinateAxisColor;
    private int groupNameTextColor;
    private int groupNameTextSize;
    private int groupInterval;
    private int childInterval;
    private int childValueTextColor;
    private int childValueTextSize;
    private int childHistogramWidth;
    private int histogramPaddingStart;
    private int histogramPaddingEnd;
    private int distanceFormGroupNameToAxis;
    private int distanceFromValueToHistogram;

    private int maxHistogramHeight;
    private Paint coordinateAxisPaint;

    private List<MultiChildHistogramGroupData> dataList;

    public MultiChildHistogramView(Context context) {
        this(context, null);
    }

    public MultiChildHistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiChildHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MultiChildHistogramView);
        coordinateAxisWidth = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_coordinateAxisWidth, DisplayUtil.dp2px(2));
        coordinateAxisColor = typedArray.getColor(R.styleable.MultiChildHistogramView_coordinateAxisColor, Color.parseColor("#434343"));
        groupNameTextColor = typedArray.getColor(R.styleable.MultiChildHistogramView_groupNameTextColor, Color.parseColor("#CC202332"));
        groupNameTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_groupNameTextSize, DisplayUtil.dp2px(15));
        groupInterval = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_groupInterval, DisplayUtil.dp2px(30));
        childInterval = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_childInterval, DisplayUtil.dp2px(10));
        childValueTextColor = typedArray.getColor(R.styleable.MultiChildHistogramView_childValueTextColor, Color.parseColor("#CC202332"));
        childValueTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_childValueTextSize, DisplayUtil.dp2px(15));
        childHistogramWidth = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_childHistogramWidth, DisplayUtil.dp2px(30));
        histogramPaddingStart = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_histogramPaddingStart, DisplayUtil.dp2px(15));
        histogramPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_histogramPaddingEnd, DisplayUtil.dp2px(15));
        distanceFormGroupNameToAxis = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_distanceFormGroupNameToAxis, DisplayUtil.dp2px(15));
        distanceFromValueToHistogram = typedArray.getDimensionPixelSize(R.styleable.MultiChildHistogramView_distanceFromValueToHistogram, DisplayUtil.dp2px(10));
        typedArray.recycle();
        coordinateAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coordinateAxisPaint.setStyle(Paint.Style.FILL);
        coordinateAxisPaint.setStrokeWidth(coordinateAxisWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        maxHistogramHeight = height - groupNameTextSize - coordinateAxisWidth - distanceFormGroupNameToAxis - distanceFromValueToHistogram - childValueTextSize;
    }

    public void setDataList(@NonNull List<MultiChildHistogramGroupData> dataList) {
        this.dataList = dataList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            return;
        }
        int scrollX = getScrollX();
        int axisBottom = height - groupNameTextSize - distanceFormGroupNameToAxis - coordinateAxisWidth / 2;
        canvas.drawLine(coordinateAxisWidth / 2 + scrollX, 0, coordinateAxisWidth / 2 + scrollX, axisBottom, coordinateAxisPaint);
        canvas.drawLine(scrollX, axisBottom, width + scrollX, axisBottom, coordinateAxisPaint);

    }
}
