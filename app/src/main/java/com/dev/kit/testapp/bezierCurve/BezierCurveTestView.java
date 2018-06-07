package com.dev.kit.testapp.bezierCurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestView extends View {
    Paint paint;
    private float radius;
    private int pointInterval;
    private double factor = 0.55191502449;
    private List<PointF> relativeControlPoints;
    private int pointCount = 5;
    private int paddingLeft;
    private int paddingRight;
    private int width;
    private int height;
    private Path arcPath;

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
        radius = DisplayUtil.dp2px(15);
        pointInterval = DisplayUtil.dp2px(15);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        arcPath = new Path();
        relativeControlPoints = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            float x;
            float y;
            switch (i) {
                case 0: {
                    x = radius;
                    y = (float) (radius * factor);
                    break;
                }
                case 1: {
                    x = (float) (radius * factor);
                    y = radius;
                    break;
                }
                case 2: {
                    x = -(float) (radius * factor);
                    y = radius;
                    break;
                }
                case 3: {
                    x = -radius;
                    y = (float) (radius * factor);
                    break;
                }
                case 4: {
                    x = -radius;
                    y = -(float) (radius * factor);
                    break;
                }
                case 5: {
                    x = -(float) (radius * factor);
                    y = -radius;
                    break;
                }
                case 6: {
                    x = (float) (radius * factor);
                    y = -radius;
                    break;
                }
                default: {
                    x = radius;
                    y = -(float) (radius * factor);
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
    protected void onDraw(Canvas canvas) {
//        Path path = new Path();
//        float centerX = 400;
//        float centerY = 400;
//        path.moveTo(400, centerY + radius);
//        float relativeX1 = (float) (factor * radius);
//        float relativeY1 = radius;
//        float relativeX2 = radius;
//        float relativeY2 = (float) (factor * radius);
//        path.cubicTo(centerX + relativeX1, centerY + relativeY1, centerX + relativeX2, centerY + relativeY2, centerX + radius, centerY);
////        path.cubicTo(400,200, 200, 300, 300, 400);
////        path.quadTo(radius + centerX, radius + centerY, radius + centerX, centerY);
////        path.quadTo(centerX + radius, centerY - radius, centerX, centerY - radius);
//        canvas.drawPath(path, paint);
//        canvas.drawCircle(centerX + relativeX1, centerY + relativeY1, 5, paint);
//        canvas.drawCircle(centerX + relativeX2, centerY + relativeY2, 5, paint);
//        canvas.drawCircle(centerX, centerY, radius, paint);

        if (height > 0) {
            float centerX;
            float centerY = height / 2;
            float endX;
            float endY;
            for (int i = 0; i < pointCount; i++) {
                centerX = (i * 2 + 1) * radius + i * pointInterval;
                float controlPointX1;
                float controlPointY1;
                float controlPointX2;
                float controlPointY2;
                arcPath.moveTo(centerX + radius, centerY);
                for (int k = 0; k < relativeControlPoints.size() / 2; k++) {
                    switch (k) {
                        case 0: {
                            endX = centerX;
                            endY = centerY + radius;
                            break;
                        }
                        case 1: {
                            endX = centerX - radius;
                            endY = centerY;
                            break;
                        }
                        case 2: {
                            endX = centerX;
                            endY = centerY - radius;
                            break;
                        }
                        default: {
                            endX = centerX + radius;
                            endY = centerY;
                            break;
                        }
                    }
                    controlPointX1 = centerX + relativeControlPoints.get(k * 2).x;
                    controlPointY1 = centerY + relativeControlPoints.get(k * 2).y;
                    controlPointX2 = centerX + relativeControlPoints.get(k * 2 + 1).x;
                    controlPointY2 = centerY + relativeControlPoints.get(k * 2 + 1).y;
                    arcPath.cubicTo(controlPointX1, controlPointY1, controlPointX2, controlPointY2, endX, endY);
                    canvas.drawPath(arcPath, paint);
                }
            }
        }

    }
}
