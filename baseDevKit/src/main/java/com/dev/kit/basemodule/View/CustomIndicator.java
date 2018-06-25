package com.dev.kit.basemodule.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.surpport.CommonPagerAdapter;
import com.dev.kit.basemodule.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class CustomIndicator extends View {
    private static final double factor = 0.55191502449;
    private static final int INDICATOR_TYPE_SCALE = 0;
    private static final int INDICATOR_TYPE_GRADUAL = 1;
    private static final int INDICATOR_TYPE_SPLIT = 2;
    private static final int INDICATOR_TYPE_SCALE_AND_GRADUAL = 3;
    private static final int DEFAULT_NORMAL_POINT_RADIUS = 8;
    private static final int DEFAULT_SELECTED_POINT_RADIUS = 12;

    private static final int INVALID_INDEX = -1;
    private int heightMeasureSpec;
    private Paint normalPointPaint;
    private Paint selectedPointPaint;
    private Paint targetPointPaint;
    private float normalPointRadius;
    private float selectedPointRadius;
    private int pointInterval;
    //    private int normalPointColor;
//    private int selectedPointColor;
    private int indicatorType;
    private int pointCount;
    private List<PointF> relativeControlPoints;
    private int selectedPointIndex;
    private int targetPointIndex = INVALID_INDEX;
    private int width;
    private int height;
    private Path arcPath;
    private Path splitArcPath;
    private float translationFactor;
    private CommonPagerAdapter adapter;
    private DataSetObserver dataSetObserver;

    public CustomIndicator(Context context) {
        this(context, null);
    }

    public CustomIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomIndicator);
        indicatorType = typedArray.getInt(R.styleable.CustomIndicator_indicatorType, 0);
        normalPointRadius = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_normalPointRadius, DEFAULT_NORMAL_POINT_RADIUS);
        selectedPointRadius = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_selectedPointRadius, indicatorType == INDICATOR_TYPE_GRADUAL ? DEFAULT_NORMAL_POINT_RADIUS : DEFAULT_SELECTED_POINT_RADIUS);
        pointInterval = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_pointInterval, 20);
        int normalPointColor = typedArray.getColor(R.styleable.CustomIndicator_normalPointColor, Color.parseColor("#FFFFFF"));
        int selectedPointColor = typedArray.getColor(R.styleable.CustomIndicator_selectedPointColor, Color.parseColor("#11EEEE"));
        typedArray.recycle();

        normalPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        normalPointPaint.setStyle(Paint.Style.FILL);
        normalPointPaint.setColor(normalPointColor);
        if (indicatorType == INDICATOR_TYPE_GRADUAL || indicatorType == INDICATOR_TYPE_SCALE_AND_GRADUAL) {
            selectedPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            selectedPointPaint.setStyle(Paint.Style.FILL);
            selectedPointPaint.setColor(selectedPointColor);
            targetPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            targetPointPaint.setStyle(Paint.Style.FILL);
            targetPointPaint.setColor(selectedPointColor);
        } else if (indicatorType == INDICATOR_TYPE_SPLIT) {
            if (selectedPointRadius < normalPointColor * 1.4) {
                selectedPointRadius = (int) (normalPointRadius * 1.4);
            }
            if (pointInterval < 4 * normalPointRadius) {
                pointInterval = (int) (4 * normalPointRadius);
            }
        }
        arcPath = new Path();
        splitArcPath = new Path();
        relativeControlPoints = new ArrayList<>();
        // 初始化绘制 1/4 圆弧的三阶贝塞尔曲线控制点相对坐标(相对圆心)
        for (int i = 0; i < 8; i++) {
            float x;
            float y;
            switch (i) {
                case 0: { // 右下P0
                    x = normalPointRadius;
                    y = (float) (normalPointRadius * factor);
                    break;
                }
                case 1: { // 右下P1
                    x = (float) (normalPointRadius * factor);
                    y = normalPointRadius;
                    break;
                }
                case 2: { // 左下P2
                    x = -(float) (normalPointRadius * factor);
                    y = normalPointRadius;
                    break;
                }
                case 3: { // 左下P3
                    x = -normalPointRadius;
                    y = (float) (normalPointRadius * factor);
                    break;
                }
                case 4: { // 左上P4
                    x = -normalPointRadius;
                    y = -(float) (normalPointRadius * factor);
                    break;
                }
                case 5: { // 左上P5
                    x = -(float) (normalPointRadius * factor);
                    y = -normalPointRadius;
                    break;
                }
                case 6: { // 右上P6
                    x = (float) (normalPointRadius * factor);
                    y = -normalPointRadius;
                    break;
                }
                default: { // 右上P7
                    x = normalPointRadius;
                    y = -(float) (normalPointRadius * factor);
                    break;
                }
            }
            PointF pointF = new PointF(x, y);
            relativeControlPoints.add(pointF);
        }

    }

    public void bindViewPager(ViewPager viewPager) {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    LogUtil.w("posInfo: " + selectedPointIndex + " " + position + " " + positionOffset + " " + positionOffsetPixels);
                    if (selectedPointIndex == position) {
                        if ((selectedPointIndex == 0 || selectedPointIndex == adapter.getRealCount()) && positionOffsetPixels <= 0) {
                            targetPointIndex = INVALID_INDEX;
                        } else {
                            targetPointIndex = position + 1;
                        }
                        translationFactor = positionOffset;
                    } else {
                        targetPointIndex = position;
                        translationFactor = 1 - positionOffset;
                    }
                    postInvalidate();
                }

                @Override
                public void onPageSelected(int position) {
                    selectedPointIndex = position;
                    postInvalidate();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            adapter = (CommonPagerAdapter) viewPager.getAdapter();
            if (adapter != null) {
                pointCount = adapter.getRealCount();
                measure(0, heightMeasureSpec);
                dataSetObserver = new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        pointCount = adapter.getRealCount();
                        postInvalidate();
                    }
                };
                adapter.registerDataSetObserver(dataSetObserver);
            } else {
                throw new RuntimeException("please set adapter before bind this viewPager");
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.heightMeasureSpec = heightMeasureSpec;
        if (pointCount > 0) {
            width = (int) (pointCount * normalPointRadius * 2 + pointCount * pointInterval) + 2;
        } else {
            width = 0;
        }

        if (heightMeasureSpec == MeasureSpec.EXACTLY) {
            height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        } else {
            height = (int) (selectedPointRadius * 2) + 2;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pointCount <= 0) {
            LogUtil.e("adapter size is 0");
            return;
        }

        float pointRadius;
        if (height > 0 && width > 0) {
            if (indicatorType == INDICATOR_TYPE_SPLIT) {
                drawSplitTypeIndicator(canvas);
                return;
            }
            float centerX;
            float centerY = height / 2;
            float endX;
            float endY;
            for (int i = 0; i < pointCount; i++) {
                float centerXOffset = selectedPointRadius - normalPointRadius;
                centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval + centerXOffset;
                // 根据ViewPager滑动动态调整当前选中点和目标点半径
                if (i == selectedPointIndex) {
                    pointRadius = normalPointRadius + (1 - translationFactor) * (selectedPointRadius - normalPointRadius);
                } else if (i == targetPointIndex) {
                    pointRadius = normalPointRadius + (translationFactor) * (selectedPointRadius - normalPointRadius);
                } else {
                    pointRadius = normalPointRadius;
                }
                arcPath.reset();
                arcPath.moveTo(centerX + pointRadius, centerY);
                for (int k = 0; k < relativeControlPoints.size() / 2; k++) {
                    switch (k) {
                        case 0: { // 相对于圆心，第一象限
                            endX = centerX;
                            endY = centerY + pointRadius;
                            break;
                        }
                        case 1: { // 相对于圆心，第二象限
                            endX = centerX - pointRadius;
                            endY = centerY;
                            break;
                        }
                        case 2: { // 相对于圆心，第三象限
                            endX = centerX;
                            endY = centerY - pointRadius;
                            break;
                        }
                        default: { // 相对于圆心，第四象限
                            endX = centerX + pointRadius;
                            endY = centerY;
                            break;
                        }
                    }
                    float controlPointX1;
                    float controlPointY1;
                    float controlPointX2;
                    float controlPointY2;
                    if (i == selectedPointIndex || i == targetPointIndex) {
                        // 控制点坐标根据ViewPager滑动做相应缩放
                        float stretchFactor = pointRadius / normalPointRadius;
                        controlPointX1 = centerX + relativeControlPoints.get(k * 2).x * stretchFactor;
                        controlPointY1 = centerY + relativeControlPoints.get(k * 2).y * stretchFactor;
                        controlPointX2 = centerX + relativeControlPoints.get(k * 2 + 1).x * stretchFactor;
                        controlPointY2 = centerY + relativeControlPoints.get(k * 2 + 1).y * stretchFactor;
                    } else {
                        controlPointX1 = centerX + relativeControlPoints.get(k * 2).x;
                        controlPointY1 = centerY + relativeControlPoints.get(k * 2).y;
                        controlPointX2 = centerX + relativeControlPoints.get(k * 2 + 1).x;
                        controlPointY2 = centerY + relativeControlPoints.get(k * 2 + 1).y;
                    }
                    arcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, endX, endY);
//                    canvas.drawPath(arcPath, normalPointPaint);
                    if (indicatorType == INDICATOR_TYPE_GRADUAL || indicatorType == INDICATOR_TYPE_SCALE_AND_GRADUAL) {
                        int alpha = (int) (translationFactor * 255);
                        if (i == selectedPointIndex) {
                            selectedPointPaint.setAlpha(255 - alpha);
                            normalPointPaint.setAlpha(alpha);
                            canvas.drawPath(arcPath, normalPointPaint);
                            canvas.drawPath(arcPath, selectedPointPaint);
                        } else if (i == targetPointIndex) {
                            targetPointPaint.setAlpha(alpha);
                            normalPointPaint.setAlpha(255 - alpha);
                            canvas.drawPath(arcPath, normalPointPaint);
                            canvas.drawPath(arcPath, targetPointPaint);
                        } else {
                            normalPointPaint.setAlpha(255);
                            canvas.drawPath(arcPath, normalPointPaint);
                        }
                    } else if (indicatorType == INDICATOR_TYPE_SCALE) {
                        canvas.drawPath(arcPath, normalPointPaint);
                    }
                }
            }
        }
    }

    private void drawSplitTypeIndicator(Canvas canvas) {
        float centerX;
        float centerY = height / 2;
        float endX;
        float endY;
        for (int i = 0; i < pointCount; i++) {
            float centerXOffset = selectedPointRadius - normalPointRadius;
            centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval + centerXOffset;
            arcPath.reset();
            splitArcPath.reset();
            arcPath.moveTo(centerX + normalPointRadius, centerY);
            for (int k = 0; k < relativeControlPoints.size() / 2; k++) {
                switch (k) {
                    case 0: {
                        endX = centerX;
                        endY = centerY + normalPointRadius;
                        break;
                    }
                    case 1: {
                        endX = centerX - normalPointRadius;
                        endY = centerY;
                        break;
                    }
                    case 2: {
                        endX = centerX;
                        endY = centerY - normalPointRadius;
                        break;
                    }
                    default: {
                        endX = centerX + normalPointRadius;
                        endY = centerY;
                        break;
                    }
                }
                float controlPointX1 = centerX + relativeControlPoints.get(k * 2).x;
                float controlPointY1 = centerY + relativeControlPoints.get(k * 2).y;
                float controlPointX2 = centerX + relativeControlPoints.get(k * 2 + 1).x;
                float controlPointY2 = centerY + relativeControlPoints.get(k * 2 + 1).y;
                arcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, endX, endY);
                canvas.drawPath(arcPath, normalPointPaint);

//                if (i == selectedPointIndex) {
//                    float stretchFactor = localSelectedPointRadius / normalPointRadius;
//                    float localSelectedControlPointX1 = localSelectedCenterX + relativeControlPoints.get(k * 2).x * stretchFactor;
//                    float localSelectedControlPointY1 = localSelectedCenterY + relativeControlPoints.get(k * 2).y * stretchFactor;
//                    float localSelectedControlPointX2 = localSelectedCenterX + relativeControlPoints.get(k * 2 + 1).x * stretchFactor;
//                    float localSelectedControlPointY2 = localSelectedCenterY + relativeControlPoints.get(k * 2 + 1).y * stretchFactor;
//                    splitArcPath.moveTo(localSelectedCenterX + localSelectedPointRadius, localSelectedCenterY);
//                    splitArcPath.cubicTo(localSelectedControlPointX1, localSelectedControlPointY1, localSelectedControlPointX2, localSelectedControlPointY2, localSelectedEndX, localSelectedEndY);
//                    canvas.drawPath(splitArcPath, normalPointPaint);
//                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (adapter != null && dataSetObserver != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        super.onDetachedFromWindow();
    }
}
