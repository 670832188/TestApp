package com.dev.kit.basemodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.DisplayUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cuiyan on 2018/6/29.
 */
public class AudioSignalView extends View {

    private int maxStaticLineHeight;
    private int maxDynamicLineHeight;
    private int minStaticLineHeight;
    private int groupCount;
    private int groupInterval;
    private int lineInterval;
    private int lineWidth;
    private int width;
    private int height;
    private static final int lineCountInGroup = 5;
    private List<LineCoordinates> staticCoordinates;
    private Paint linePaint;
    private Rect lineRect;
    private boolean isAudioSignalStarted;
    private Runnable signalRunnable;
    private SignalHandler signalHandler;

    public AudioSignalView(Context context) {
        this(context, null);
    }

    public AudioSignalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioSignalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AudioSignalView);
        maxDynamicLineHeight = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_maxDynamicLineHeight, DisplayUtil.dp2px(20));
        maxStaticLineHeight = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_maxStaticLineHeight, DisplayUtil.dp2px(14));
        if (maxStaticLineHeight > maxDynamicLineHeight * 0.8) {
            maxStaticLineHeight = (int) (maxDynamicLineHeight * 0.8);
        }
        if (maxStaticLineHeight < maxDynamicLineHeight * 0.6) {
            maxStaticLineHeight = (int) (maxDynamicLineHeight * 0.6);
        }
        minStaticLineHeight = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_minStaticLineHeight, DisplayUtil.dp2px(5));
        if (minStaticLineHeight > maxStaticLineHeight) {
            minStaticLineHeight = maxStaticLineHeight;
        }
        groupCount = typedArray.getInt(R.styleable.AudioSignalView_asv_groupCount, 3);
        if (groupCount < 3) {
            groupCount = 3;
        }
        groupInterval = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_groupInterval, DisplayUtil.dp2px(8));
        lineInterval = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_lineInterval, DisplayUtil.dp2px(3));
        int lineColor = typedArray.getColor(R.styleable.AudioSignalView_asv_lineColor, Color.parseColor("#4fc6be"));
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.AudioSignalView_asv_lineWidth, DisplayUtil.dp2px(2));
        typedArray.recycle();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        lineRect = new Rect();
        initData();
    }

    private void initData() {
        width = groupCount * lineCountInGroup * lineWidth + (groupCount * lineCountInGroup - 1) * lineInterval + (groupCount - 1) * (groupInterval - lineInterval);
        height = maxDynamicLineHeight;
        staticCoordinates = new ArrayList<>();
        int mean = (lineCountInGroup / 2);
        int remainder;
        int lineDif = (maxStaticLineHeight - minStaticLineHeight) / mean;
        int lineHeight;
        int lineLeft = 0;
        int lineTop;
        int lineRight;
        int lineBottom;
        int lineCount = groupCount * lineCountInGroup;
        for (int i = 0; i < lineCount; i++) {
            lineRight = lineLeft + lineWidth;
            remainder = i % lineCountInGroup;
            lineHeight = maxStaticLineHeight - lineDif * (Math.abs(mean - remainder));
            lineTop = (height - lineHeight) / 2;
            lineBottom = lineTop + lineHeight;
            LineCoordinates coordinates = new LineCoordinates(lineLeft, lineTop, lineRight, lineBottom);
            staticCoordinates.add(coordinates);
            if (i % (lineCountInGroup) == lineCountInGroup - 1) {
                lineLeft += lineWidth + groupInterval;
            } else {
                lineLeft += lineWidth + lineInterval;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    private Random random = new Random();

    @Override
    protected void onDraw(Canvas canvas) {
        for (LineCoordinates coordinates : staticCoordinates) {
            int offsetY;
            if (isAudioSignalStarted) {
                offsetY = random.nextInt() % (maxDynamicLineHeight - maxStaticLineHeight);
                if (offsetY < 0 && Math.abs(offsetY) > minStaticLineHeight) {
                    offsetY = -(int) (minStaticLineHeight * 0.9);
                }
            } else {
                offsetY = 0;
            }
            lineRect.left = coordinates.left;
            lineRect.top = coordinates.top - offsetY / 2;
            lineRect.right = coordinates.right;
            lineRect.bottom = coordinates.bottom + offsetY / 2;
            canvas.drawRect(lineRect, linePaint);
        }
    }

    public void startAudioSignal() {
        if (signalHandler == null) {
            signalHandler = new SignalHandler(this);
        }
        if (signalRunnable == null) {
            signalRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isAudioSignalStarted) {
                        postInvalidate();
                        if (signalHandler.getWeakReference() != null && signalHandler.getWeakReference().get() != null) {
                            signalHandler.postDelayed(signalRunnable, 150);
                        }
                    }
                }
            };
        }
        if (!isAudioSignalStarted) {
            isAudioSignalStarted = true;
            postInvalidate();
            signalHandler.postDelayed(signalRunnable, 150);
        }
    }

    public void stopAudioSignal() {
        if (isAudioSignalStarted) {
            isAudioSignalStarted = false;
            postInvalidate();
        }
    }

    public boolean isAudioSignalStarted() {
        return isAudioSignalStarted;
    }

    private class LineCoordinates {
        private int left;
        private int top;
        private int right;
        private int bottom;

        private LineCoordinates(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    private static class SignalHandler extends Handler {
        WeakReference<AudioSignalView> reference;

        private SignalHandler(AudioSignalView audioSignalView) {
            reference = new WeakReference<>(audioSignalView);
        }

        public WeakReference<AudioSignalView> getWeakReference() {
            return reference;
        }
    }
}
