package com.dev.kit.basemodule.turnpage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

/**
 * @author cuiyan
 * Created on 2021/1/20.
 */
public class TurnPageView extends SurfaceView implements SurfaceHolder.Callback {
    private int width;
    private int height;
    private Timer timer;
    private final AtomicInteger currentIndex = new AtomicInteger();
    private volatile boolean isSurfaceAvailable;
    private List<ImgData> dataList;
    private int timeInterval = 5 * 1000;

    public TurnPageView(Context context) {
        this(context, null);
    }

    public TurnPageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurnPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        isSurfaceAvailable = true;
        startLoop();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cancelLoop();
    }

    public void cancelLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isSurfaceAvailable = false;
    }

    public void setDataList(@NonNull List<ImgData> dataList) {
        if (dataList.isEmpty()) {
            return;
        }
        this.dataList = dataList;
        currentIndex.set(0);
        if (!isSurfaceAvailable) {
            return;
        }
        startLoop();
    }

    private void startLoop() {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int index = currentIndex.getAndIncrement() % dataList.size();
                ImgData data = dataList.get(index);
                Canvas canvas = getHolder().lockCanvas();
                try {
                    Bitmap bitmap = Glide.with(getContext()).asBitmap().load(data.getUri()).apply(new RequestOptions().centerCrop()).submit(width, height).get(timeInterval - 1000, TimeUnit.MILLISECONDS);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                } catch (Exception e) {
                    if (data.getHolderRes() != 0) {
                        try {
                            Bitmap bitmap = Glide.with(getContext()).asBitmap().load(data.getHolderRes()).apply(new RequestOptions().centerCrop()).submit(width, height).get();
                            canvas.drawBitmap(bitmap, 0, 0, null);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                } finally {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }, 0, timeInterval);
    }
}
