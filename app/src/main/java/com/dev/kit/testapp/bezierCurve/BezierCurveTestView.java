package com.dev.kit.testapp.bezierCurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.util.LogUtil;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestView extends View {
    Paint paint;
    private float radius = 200;
    private double factor = 0.55191502449;

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
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        float centerX = 400;
        float centerY = 400;
        path.moveTo(400, centerY + radius);
        float relativeX1 = (float) (factor * radius);
        float relativeY1 =  radius;
        float relativeX2 = radius;
        float relativeY2 = (float) (factor * radius);
        path.cubicTo(centerX + relativeX1, centerY + relativeY1, centerX + relativeX2, centerY + relativeY2, centerX + radius, centerY);
//        path.cubicTo(400,200, 200, 300, 300, 400);
//        path.quadTo(radius + centerX, radius + centerY, radius + centerX, centerY);
//        path.quadTo(centerX + radius, centerY - radius, centerX, centerY - radius);
        canvas.drawPath(path, paint);
        canvas.drawCircle(centerX + relativeX1, centerY + relativeY1, 5, paint);
        canvas.drawCircle(centerX + relativeX2, centerY + relativeY2, 5, paint);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
}
