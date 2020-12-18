package com.dev.kit.basemodule.multiChildHistogram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.StringUtil;

import java.util.List;

/**
 * 多组分直方图表
 * 每组可包含多个子数据
 * Created by cuiyan on 2018/5/3.
 */
public class MultiGroupHistogramView extends View {
    private int width;
    private int height;
    // 坐标轴线宽度
    private int coordinateAxisWidth;

    // 组名称字体大小
    private int groupNameTextSize;
    // 小组之间间距
    private int groupInterval;
    // 组内子直方图间距
    private int histogramInterval;
    private int histogramValueTextSize;
    private int histogramValueDecimalCount;
    private int histogramHistogramWidth;
    private int chartPaddingTop;
    private int histogramPaddingStart;
    private int histogramPaddingEnd;
    // 各组名称到X轴的距离
    private int distanceFormGroupNameToAxis;
    // 直方图上方数值到直方图的距离
    private int distanceFromValueToHistogram;

    // 直方图最大高度
    private int maxHistogramHeight;
    // 轴线画笔
    private Paint coordinateAxisPaint;
    // 组名画笔
    private Paint groupNamePaint;
    private Paint.FontMetrics groupNameFontMetrics;
    private Paint.FontMetrics histogramValueFontMetrics;
    // 直方图数值画笔
    private Paint histogramValuePaint;
    // 直方图画笔
    private Paint histogramPaint;
    // 直方图绘制区域
    private Rect histogramPaintRect;
    // 直方图表视图总宽度
    private int histogramContentWidth;
    // 存储组内直方图shader color，例如，每组有3个直方图，该SparseArray就存储3个相对应的shader color
    private SparseArray<int[]> histogramShaderColorArray;

    private List<MultiGroupHistogramGroupData> dataList;
    private SparseArray<Float> childMaxValueArray;

    private Scroller scroller;
    private int minimumVelocity;
    private int maximumVelocity;
    private VelocityTracker velocityTracker;

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
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MultiGroupHistogramView);
        coordinateAxisWidth = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_coordinateAxisWidth, DisplayUtil.dp2px(2));
        // 坐标轴线颜色
        int coordinateAxisColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_coordinateAxisColor, Color.parseColor("#434343"));
        // 底部小组名称字体颜色
        int groupNameTextColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_groupNameTextColor, Color.parseColor("#CC202332"));
        groupNameTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_groupNameTextSize, DisplayUtil.dp2px(15));
        groupInterval = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_groupInterval, DisplayUtil.dp2px(30));
        histogramInterval = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramInterval, DisplayUtil.dp2px(10));
        // 直方图数值文本颜色
        int histogramValueTextColor = typedArray.getColor(R.styleable.MultiGroupHistogramView_histogramValueTextColor, Color.parseColor("#CC202332"));
        histogramValueTextSize = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramValueTextSize, DisplayUtil.dp2px(12));
        histogramValueDecimalCount = typedArray.getInt(R.styleable.MultiGroupHistogramView_histogramValueDecimalCount, 0);
        histogramHistogramWidth = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramHistogramWidth, DisplayUtil.dp2px(20));
        chartPaddingTop = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_chartPaddingTop, DisplayUtil.dp2px(10));
        histogramPaddingStart = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramPaddingStart, DisplayUtil.dp2px(15));
        histogramPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_histogramPaddingEnd, DisplayUtil.dp2px(15));
        distanceFormGroupNameToAxis = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_distanceFormGroupNameToAxis, DisplayUtil.dp2px(15));
        distanceFromValueToHistogram = typedArray.getDimensionPixelSize(R.styleable.MultiGroupHistogramView_distanceFromValueToHistogram, DisplayUtil.dp2px(10));
        typedArray.recycle();

        coordinateAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coordinateAxisPaint.setStyle(Paint.Style.FILL);
        coordinateAxisPaint.setStrokeWidth(coordinateAxisWidth);
        coordinateAxisPaint.setColor(coordinateAxisColor);

        groupNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        groupNamePaint.setTextSize(groupNameTextSize);
        groupNamePaint.setColor(groupNameTextColor);
        groupNameFontMetrics = groupNamePaint.getFontMetrics();

        histogramValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        histogramValuePaint.setTextSize(histogramValueTextSize);
        histogramValuePaint.setColor(histogramValueTextColor);
        histogramValueFontMetrics = histogramValuePaint.getFontMetrics();

        histogramPaintRect = new Rect();
        histogramPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scroller = new Scroller(getContext(), new LinearInterpolator());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        maxHistogramHeight = height - groupNameTextSize - coordinateAxisWidth - distanceFormGroupNameToAxis - distanceFromValueToHistogram - histogramValueTextSize - chartPaddingTop;
    }
    /**
     * 判断是否可以水平滑动
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (direction > 0) {
            return histogramContentWidth - getScrollX() - width + histogramPaddingStart + histogramPaddingEnd > 0;
        } else {
            return getScrollX() > 0;
        }
    }

    /**
     * 根据滑动方向获取最大可滑动距离
     * @param direction 标识滑动方向  正数：右滑(手指从右至左移动)；负数：左滑(手指由左向右移动)
     */
    private int getMaxCanScrollX(int direction) {
        if (direction > 0) {
            return histogramContentWidth - getScrollX() - width + histogramPaddingStart + histogramPaddingEnd > 0 ?
                    histogramContentWidth - getScrollX() - width + histogramPaddingStart + histogramPaddingEnd : 0;
        } else if (direction < 0) {
            return getScrollX();
        }
        return 0;
    }

    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                lastX = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = (int) (event.getX() - lastX);
                lastX = event.getX();
                if (deltaX > 0 && canScrollHorizontally(-1)) {
                    scrollBy(-Math.min(getMaxCanScrollX(-1), deltaX), 0);
                } else if (deltaX < 0 && canScrollHorizontally(1)) {
                    scrollBy(Math.min(getMaxCanScrollX(1), -deltaX), 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                fling(velocityX);
                recycleVelocityTracker();
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                recycleVelocityTracker();
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void initVelocityTrackerIfNotExists() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private void fling(int velocityX) {
        if (Math.abs(velocityX) > minimumVelocity) {
            if (Math.abs(velocityX) > maximumVelocity) {
                velocityX = maximumVelocity * velocityX / Math.abs(velocityX);
            }
            scroller.fling(getScrollX(), getScrollY(), -velocityX, 0, 0, histogramContentWidth + histogramPaddingStart - width, 0, 0);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), 0);
        }
    }

    public void setDataList(@NonNull List<MultiGroupHistogramGroupData> dataList) {
        this.dataList = dataList;
        if (childMaxValueArray == null) {
            childMaxValueArray = new SparseArray<>();
        } else {
            childMaxValueArray.clear();
        }
        histogramContentWidth = 0;
        for (MultiGroupHistogramGroupData groupData : dataList) {
            List<MultiGroupHistogramChildData> childDataList = groupData.getChildDataList();
            if (childDataList != null && childDataList.size() > 0) {
                for (int i = 0; i < childDataList.size(); i++) {
                    histogramContentWidth += histogramHistogramWidth + histogramInterval;
                    MultiGroupHistogramChildData childData = childDataList.get(i);
                    Float childMaxValue = childMaxValueArray.get(i);
                    if (childMaxValue == null || childMaxValue < childData.getValue()) {
                        childMaxValueArray.put(i, childData.getValue());
                    }
                }
                histogramContentWidth += groupInterval - histogramInterval;
            }
        }
        histogramContentWidth += -groupInterval;
        postInvalidate();
    }

    /**
     * 设置组内直方图颜色
     */
    public void setHistogramColor(int[]... colors) {
        if (colors != null && colors.length > 0) {
            if (histogramShaderColorArray == null) {
                histogramShaderColorArray = new SparseArray<>();
            } else {
                histogramShaderColorArray.clear();
            }
            for (int i = 0; i < colors.length; i++) {
                histogramShaderColorArray.put(i, colors[i]);
            }
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
                List<MultiGroupHistogramChildData> childDataList = groupData.getChildDataList();
                if (childDataList != null && childDataList.size() > 0) {
                    int groupWidth = 0;
                    for (int i = 0; i < childDataList.size(); i++) {
                        MultiGroupHistogramChildData childData = childDataList.get(i);
                        histogramPaintRect.left = xAxisOffset;
                        histogramPaintRect.right = histogramPaintRect.left + histogramHistogramWidth;
                        int childHistogramHeight;
                        if (childData.getValue() <= 0 || childMaxValueArray.get(i) <= 0) {
                            childHistogramHeight = 0;
                        } else {
                            childHistogramHeight = (int) (childData.getValue() / childMaxValueArray.get(i) * maxHistogramHeight);
                        }
                        histogramPaintRect.top = height - childHistogramHeight - coordinateAxisWidth - distanceFormGroupNameToAxis - groupNameTextSize;
                        histogramPaintRect.bottom = histogramPaintRect.top + childHistogramHeight;
                        int[] histogramShaderColor = histogramShaderColorArray.get(i);
                        LinearGradient shader = null;
                        if (histogramShaderColor != null && histogramShaderColor.length > 0) {
                            shader = getHistogramShader(histogramPaintRect.left, chartPaddingTop + distanceFromValueToHistogram + histogramValueTextSize,
                                    histogramPaintRect.right, histogramPaintRect.bottom, histogramShaderColor);
                        }
                        histogramPaint.setShader(shader);
                        canvas.drawRect(histogramPaintRect, histogramPaint);
                        String childHistogramHeightValue = StringUtil.NumericScaleByFloor(String.valueOf(childData.getValue()), histogramValueDecimalCount) + childData.getSuffix();

                        float valueTextX = xAxisOffset + (histogramHistogramWidth - histogramValuePaint.measureText(childHistogramHeightValue)) / 2;
                        float valueTextY = histogramPaintRect.top - distanceFormGroupNameToAxis + (histogramValueFontMetrics.bottom) / 2;
                        canvas.drawText(childHistogramHeightValue, valueTextX, valueTextY, histogramValuePaint);
                        int deltaX = i < childDataList.size() - 1 ? histogramHistogramWidth + histogramInterval : histogramHistogramWidth;
                        groupWidth += deltaX;
                        xAxisOffset += i == childDataList.size() - 1 ? deltaX + groupInterval : deltaX;
                    }
                    String groupName = groupData.getGroupName();
                    float groupNameTextWidth = groupNamePaint.measureText(groupName);
                    float groupNameTextX = xAxisOffset - groupWidth - groupInterval + (groupWidth - groupNameTextWidth) / 2;
                    float groupNameTextY = (height - groupNameFontMetrics.bottom / 2);
                    canvas.drawText(groupName, groupNameTextX, groupNameTextY, groupNamePaint);
                }
            }
        }
    }

    private LinearGradient getHistogramShader(float x0, float y0, float x1, float y1, int[] colors) {
        return new LinearGradient(x0, y0, x1, y1, colors, null, Shader.TileMode.CLAMP);
    }


//    private static final float INFLEXION = 0.35f;
//    private static float mFlingFriction = ViewConfiguration.getScrollFriction();
//    private static float mPhysicalCoeff = SensorManager.GRAVITY_EARTH * 39.37f * DisplayUtil.getScaleFactor() * 160 * 0.84f;
//    ;
//    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
//
//    private double getSplineDeceleration(int velocity) {
//        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
//    }
//
//    private double getSplineDecelerationByDistance(double distance) {
//        final double decelMinusOne = DECELERATION_RATE - 1.0;
//        return decelMinusOne * (Math.log(distance / (mFlingFriction * mPhysicalCoeff))) / DECELERATION_RATE;
//    }
//
//    //通过初始速度获取最终滑动距离
//    private double getSplineFlingDistance(int velocity) {
//        final double l = getSplineDeceleration(velocity);
//        final double decelMinusOne = DECELERATION_RATE - 1.0;
//        return mFlingFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelMinusOne * l);
//    }
//
//    //通过需要滑动的距离获取初始速度
//    private int getVelocityByDistance(double distance) {
//        final double l = getSplineDecelerationByDistance(distance);
//        int velocity = (int) (Math.exp(l) * mFlingFriction * mPhysicalCoeff / INFLEXION);
//        return Math.abs(velocity);
//    }
//
//    //获取滑动的时间
//    private int getSplineFlingDuration(int velocity) {
//        final double l = getSplineDeceleration(velocity);
//        final double decelMinusOne = DECELERATION_RATE - 1.0;
//        return (int) (1000.0 * Math.exp(l / decelMinusOne));
//    }
}
