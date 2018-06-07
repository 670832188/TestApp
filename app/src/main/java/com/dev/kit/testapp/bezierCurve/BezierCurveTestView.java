package com.dev.kit.testapp.bezierCurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final double factor = 0.55191502449;
    Paint paint;
    private float pointRadius;
    private int pointInterval;
    private int pointColor;
    private float selectedPointRadius;
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
        pointRadius = DisplayUtil.dp2px(15);
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
                    x = pointRadius;
                    y = (float) (pointRadius * factor);
                    break;
                }
                case 1: {
                    x = (float) (pointRadius * factor);
                    y = pointRadius;
                    break;
                }
                case 2: {
                    x = -(float) (pointRadius * factor);
                    y = pointRadius;
                    break;
                }
                case 3: {
                    x = -pointRadius;
                    y = (float) (pointRadius * factor);
                    break;
                }
                case 4: {
                    x = -pointRadius;
                    y = -(float) (pointRadius * factor);
                    break;
                }
                case 5: {
                    x = -(float) (pointRadius * factor);
                    y = -pointRadius;
                    break;
                }
                case 6: {
                    x = (float) (pointRadius * factor);
                    y = -pointRadius;
                    break;
                }
                default: {
                    x = pointRadius;
                    y = -(float) (pointRadius * factor);
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
//        path.moveTo(400, centerY + pointRadius);
//        float relativeX1 = (float) (factor * pointRadius);
//        float relativeY1 = pointRadius;
//        float relativeX2 = pointRadius;
//        float relativeY2 = (float) (factor * pointRadius);
//        path.cubicTo(centerX + relativeX1, centerY + relativeY1, centerX + relativeX2, centerY + relativeY2, centerX + pointRadius, centerY);
////        path.cubicTo(400,200, 200, 300, 300, 400);
////        path.quadTo(pointRadius + centerX, pointRadius + centerY, pointRadius + centerX, centerY);
////        path.quadTo(centerX + pointRadius, centerY - pointRadius, centerX, centerY - pointRadius);
//        canvas.drawPath(path, paint);
//        canvas.drawCircle(centerX + relativeX1, centerY + relativeY1, 5, paint);
//        canvas.drawCircle(centerX + relativeX2, centerY + relativeY2, 5, paint);
//        canvas.drawCircle(centerX, centerY, pointRadius, paint);

        if (height > 0) {
            float centerX;
            float centerY = height / 2;
            float endX;
            float endY;
            for (int i = 0; i < pointCount; i++) {
                centerX = (i * 2 + 1) * pointRadius + i * pointInterval;
                float controlPointX1;
                float controlPointY1;
                float controlPointX2;
                float controlPointY2;
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
