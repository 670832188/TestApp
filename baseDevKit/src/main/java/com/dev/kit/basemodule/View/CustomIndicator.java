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
    private static final int INVALID_INDEX = -1;
    private int heightMeasureSpec;
    Paint paint;
    private float normalPointRadius;
    private float selectedPointRadius;
    private int pointInterval;
    private int normalPointColor;
    private int selectedPointColor;
    private int indicatorType;
    private int pointCount;
    private List<PointF> relativeControlPoints;
    private int selectedPointIndex;
    private int nextPointIndex = INVALID_INDEX;
    private int width;
    private int height;
    private Path arcPath;
    private ViewPager boundPager;
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
        normalPointRadius = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_normalPointRadius, 8);
        selectedPointRadius = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_selectedPointRadius, indicatorType == INDICATOR_TYPE_GRADUAL ? 5 : 12);
        pointInterval = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_pointInterval, 20);
        normalPointColor = typedArray.getColor(R.styleable.CustomIndicator_normalPointColor, Color.parseColor("#FFFFFF"));
        selectedPointColor = typedArray.getColor(R.styleable.CustomIndicator_selectedPointColor, Color.parseColor("#FF4081"));
        typedArray.recycle();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(normalPointColor);
        arcPath = new Path();
        relativeControlPoints = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            float x;
            float y;
            switch (i) {
                case 0: {
                    x = normalPointRadius;
                    y = (float) (normalPointRadius * factor);
                    break;
                }
                case 1: {
                    x = (float) (normalPointRadius * factor);
                    y = normalPointRadius;
                    break;
                }
                case 2: {
                    x = -(float) (normalPointRadius * factor);
                    y = normalPointRadius;
                    break;
                }
                case 3: {
                    x = -normalPointRadius;
                    y = (float) (normalPointRadius * factor);
                    break;
                }
                case 4: {
                    x = -normalPointRadius;
                    y = -(float) (normalPointRadius * factor);
                    break;
                }
                case 5: {
                    x = -(float) (normalPointRadius * factor);
                    y = -normalPointRadius;
                    break;
                }
                case 6: {
                    x = (float) (normalPointRadius * factor);
                    y = -normalPointRadius;
                    break;
                }
                default: {
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
        boundPager = viewPager;
        if (boundPager != null) {
            boundPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    LogUtil.e("posInfo: " + selectedPointIndex + " " + position + " " + positionOffset + " " + positionOffsetPixels);
                    if (selectedPointIndex == position) {
                        if ((selectedPointIndex == 0 || selectedPointIndex == adapter.getRealCount()) && positionOffsetPixels <= 0) {
                            nextPointIndex = INVALID_INDEX;
                        } else {
                            nextPointIndex = position + 1;
                        }
                        translationFactor = positionOffset;
                    } else {
                        nextPointIndex = position;
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
            adapter = (CommonPagerAdapter) boundPager.getAdapter();
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
        if (height > 0) {
            float centerX;
            float centerY = height / 2;
            float endX;
            float endY;
            for (int i = 0; i < pointCount; i++) {
                centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval + selectedPointRadius - normalPointRadius;
                if (i == selectedPointIndex) {
                    pointRadius = normalPointRadius + (1 - translationFactor) * (selectedPointRadius - normalPointRadius);
                } else if (i == nextPointIndex) {
                    pointRadius = normalPointRadius + (translationFactor) * (selectedPointRadius - normalPointRadius);
                } else {
                    pointRadius = normalPointRadius;
                }
                arcPath.reset();
                arcPath.moveTo(centerX + pointRadius, centerY);
                for (int k = 0; k < relativeControlPoints.size() / 2; k++) {
                    switch (k) {
                        case 0: {
                            endX = centerX;
                            endY = centerY + pointRadius;
                            break;
                        }
                        case 1: {
                            endX = centerX - pointRadius;
                            endY = centerY;
                            break;
                        }
                        case 2: {
                            endX = centerX;
                            endY = centerY - pointRadius;
                            break;
                        }
                        default: {
                            endX = centerX + pointRadius;
                            endY = centerY;
                            break;
                        }
                    }
                    float controlPointX1;
                    float controlPointY1;
                    float controlPointX2;
                    float controlPointY2;
                    if (i == selectedPointIndex || i == nextPointIndex) {
//                        switch (indicatorType) {
//                            case INDICATOR_TYPE_SCALE:{
//                                break;
//                            }case INDICATOR_TYPE_GRADUAL:{
//                                break;
//                            }case INDICATOR_TYPE_SPLIT:{
//                                break;
//                            }
//                        }
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
                    canvas.drawPath(arcPath, paint);
                }
            }
        }
    }

    private void drawScaleTypeIndicator(Canvas canvas, float endX, float endY) {

    }
    @Override
    protected void onDetachedFromWindow() {
        if (adapter != null && dataSetObserver != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        super.onDetachedFromWindow();
    }
}
