package com.dev.kit.basemodule.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by cy on 2018/3/13.
 */

public class ColorFilterImageView extends AppCompatImageView {
    private int touchFilterColor = Color.LTGRAY;
    private GestureDetector gestureDetector;

    public ColorFilterImageView(Context context) {
        this(context, null);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchFilterColor(int touchFilterColor) {
        this.touchFilterColor = touchFilterColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        gestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                setColorFilter(touchFilterColor, PorterDuff.Mode.MULTIPLY);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                setColorFilter(Color.TRANSPARENT);
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

}
