package com.dev.kit.testapp.bezierCurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestView extends View {
    Paint paint;

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
        path.moveTo(100, 100);
        path.quadTo(200, 200, 300, 100);
        canvas.drawPath(path, paint);
        canvas.drawCircle(200, 200, 5, paint);

    }
}
