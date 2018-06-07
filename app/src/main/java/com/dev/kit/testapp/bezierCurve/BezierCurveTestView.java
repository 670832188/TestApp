package com.dev.kit.testapp.bezierCurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestView extends View {
    private static final double factor = 0.55191502449;
    Paint paint;
    private float normalPointRadius;
    private int pointInterval;
    private int pointColor;
    private float selectedPointRadius;
    private int selectedPointIndex = 1;
    private List<PointF> relativeControlPoints;
    private int pointCount = 5;
    private int paddingLeft;
    private int paddingRight;
    private int width;
    private int height;
    private Path arcPath;

    private float moveX;

    public BezierCurveTestView(Context context) {
        this(context, null);
    }

    public BezierCurveTestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurveTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        normalPointRadius = DisplayUtil.dp2px(10);
        selectedPointRadius = DisplayUtil.dp2px(15);
        pointInterval = DisplayUtil.dp2px(15);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#ff8400"));
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                moveX = downX - event.getX();
                if (moveX >= 0 && moveX <= 100) {
                    invalidate();
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float pointRadius;
        if (height > 0) {
            float centerX;
            float centerY = height / 2;
            float endX;
            float endY;
            for (int i = 0; i < pointCount; i++) {
                if (i != selectedPointIndex) {
                    pointRadius = normalPointRadius;
                } else {
                    pointRadius = selectedPointRadius;
                }
                if (i < selectedPointIndex) {
                    centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval;
                } else if (selectedPointIndex == i) {
                    centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval + selectedPointRadius - normalPointRadius;
                } else {
                    centerX = (i * 2 + 1) * normalPointRadius + i * pointInterval + (selectedPointRadius - normalPointRadius) * 2;
                }
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
                    if (i == selectedPointIndex) {
                        controlPointX1 = centerX + relativeControlPoints.get(k * 2).x * selectedPointRadius / normalPointRadius;
                        controlPointY1 = centerY + relativeControlPoints.get(k * 2).y * selectedPointRadius / normalPointRadius;
                        controlPointX2 = centerX + relativeControlPoints.get(k * 2 + 1).x * selectedPointRadius / normalPointRadius;
                        controlPointY2 = centerY + relativeControlPoints.get(k * 2 + 1).y * selectedPointRadius / normalPointRadius;
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
}
