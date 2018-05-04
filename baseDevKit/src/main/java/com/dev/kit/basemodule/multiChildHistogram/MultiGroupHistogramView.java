package com.dev.kit.basemodule.multiChildHistogram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.LogUtil;

import java.util.List;

/**
 * 多组分柱状图表
 * 每组可包含多个子数据
 * Created by cuiyan on 2018/5/3.
 */
public class MultiGroupHistogramView extends View {
    private int width;
    private int height;
    // 坐标轴线宽度
    private int coordinateAxisWidth;
    // 坐标轴线颜色
    private int coordinateAxisColor;
    // 底部小组名称字体颜色
    private int groupNameTextColor;
    // 底部小组名称字体大小
    private int groupNameTextSize;
    // 小组之间间距
    private int groupInterval;
    // 组内子柱状图间距
    private int childInterval;
    //
    private int childValueTextColor;
    private int childValueTextSize;
    private int childHistogramWidth;
    private int histogramPaddingStart;
    private int histogramPaddingEnd;
    // 各组名称到X轴的距离
    private int distanceFormGroupNameToAxis;
    // 柱状图上方数值到柱状图的距离
    private int distanceFromValueToHistogram;

    // 柱状图最大高度
    private int maxHistogramHeight;
    private Paint coordinateAxisPaint;
    private Paint childPaint;

    private List<MultiGroupHistogramGroupData> dataList;
    private SparseArray<Float> childMaxValueArray;

    public MultiGroupHistogramView(Context context) {
        this(context, null);
    }

    public MultiGroupHistogramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiGroupHistogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MultiGroupHistogramView);
        coordinateAxisWidth = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_coordinateAxisWidth, DisplayUtil.dp2px(2));
        coordinateAxisColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_coordinateAxisColor, Color.parseColor("#434343"));
        groupNameTextColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_groupNameTextColor, Color.parseColor("#CC202332"));
        groupNameTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_groupNameTextSize, DisplayUtil.dp2px(15));
        groupInterval = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_groupInterval, DisplayUtil.dp2px(30));
        childInterval = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_childInterval, DisplayUtil.dp2px(10));
        childValueTextColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_childValueTextColor, Color.parseColor("#CC202332"));
        childValueTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_childValueTextSize, DisplayUtil.dp2px(15));
        childHistogramWidth = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_childHistogramWidth, DisplayUtil.dp2px(30));
        histogramPaddingStart = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramPaddingStart, DisplayUtil.dp2px(15));
        histogramPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramPaddingEnd, DisplayUtil.dp2px(15));
        distanceFormGroupNameToAxis = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_distanceFormGroupNameToAxis, DisplayUtil.dp2px(15));
        distanceFromValueToHistogram = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_distanceFromValueToHistogram, DisplayUtil.dp2px(10));
        typedArray.recycle();
        coordinateAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coordinateAxisPaint.setStyle(Paint.Style.FILL);
        coordinateAxisPaint.setStrokeWidth(coordinateAxisWidth);
        childPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        maxHistogramHeight = height - groupNameTextSize - coordinateAxisWidth - distanceFormGroupNameToAxis - distanceFromValueToHistogram - childValueTextSize;
    }

    public void setDataList(@NonNull List<MultiGroupHistogramGroupData> dataList) {
        this.dataList = dataList;
        if (childMaxValueArray == null) {
            childMaxValueArray = new SparseArray<>();
        } else {
            childMaxValueArray.clear();
        }
        for (MultiGroupHistogramGroupData groupData : dataList) {
            List<MultiGroupHistogramGroupData.ChildData> childDataList = groupData.getChildDataList();
            if (childDataList != null && childDataList.size() > 0) {
                for (int i = 0; i < childDataList.size(); i++) {
                    MultiGroupHistogramGroupData.ChildData childData = childDataList.get(i);
                    Float childMaxValue = childMaxValueArray.get(i);
                    if (childMaxValue == null || childMaxValue < childData.getValue()) {
                        childMaxValueArray.put(i, childData.getValue());
                    }
                }
            }
        }
        for (int i = 0; i < childMaxValueArray.size(); i++) {
            LogUtil.e("childMaxValue: " + childMaxValueArray.get(i));
        }
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
        if (dataList != null && dataList.size() > 0) {
            int xAxisOffset = histogramPaddingStart;
            for (MultiGroupHistogramGroupData groupData : dataList) {
                List<MultiGroupHistogramGroupData.ChildData> childDataList = groupData.getChildDataList();
                if (childDataList != null && childDataList.size() > 0) {
                    for (MultiGroupHistogramGroupData.ChildData childData : childDataList) {
//                        canvas.drawRect();
                        xAxisOffset += childInterval;
                    }
                    xAxisOffset += groupInterval;
                }
            }
        }
    }
}
