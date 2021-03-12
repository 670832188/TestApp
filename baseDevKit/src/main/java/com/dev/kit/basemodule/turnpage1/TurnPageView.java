package com.dev.kit.basemodule.turnpage1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dev.kit.basemodule.util.ImageUtil;

import java.util.ArrayList;
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
    private int timeInterval = 6 * 1000;
    private List<ITurnPage> turnPageList;
    private Bitmap lastBitmap;

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
        turnPageList = new ArrayList<>();
        turnPageList.add(new TurnPageShutter());
        turnPageList.add(new TurnPage3DRotation());
        turnPageList.add(new TurnPageTranslate());
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
        lastBitmap = null;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int index = currentIndex.getAndIncrement();
                ImgData data = dataList.get(index % dataList.size());
                Log.e("mytag", "isCached: " + ImageUtil.isCache(getContext(), data.getUri()));
                ITurnPage turnPage = turnPageList.get(index % turnPageList.size());
                long turnPagePeriod = turnPage.getPeriod();
                try {
                    Bitmap bitmap = Glide.with(getContext()).asBitmap().load(data.getUri()).apply(new RequestOptions().centerCrop()).submit(width, height).get(timeInterval - turnPagePeriod, TimeUnit.MILLISECONDS);
                    long startTime = System.currentTimeMillis();
                    long diff = 0;

                    while (true) {
                        float progress = diff * 1f / turnPagePeriod;
                        if (progress > 1) {
                            progress = 1;
                        }
                        Canvas canvas = getHolder().lockCanvas();
                        try {
                            turnPage.onTurnPageDraw(canvas, bitmap, lastBitmap, width, height, progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            getHolder().unlockCanvasAndPost(canvas);
                        }
                        if (progress == 1) {
                            break;
                        }
                        diff = System.currentTimeMillis() - startTime;
                    }
                    lastBitmap = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (data.getHolderRes() != 0) {
                        Canvas canvas = getHolder().lockCanvas();
                        try {
                            Bitmap bitmap = Glide.with(getContext()).asBitmap().load(data.getHolderRes()).apply(new RequestOptions().centerCrop()).submit(width, height).get();
                            canvas.drawBitmap(bitmap, 0, 0, null);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        } finally {
                            getHolder().unlockCanvasAndPost(canvas);
                        }
                    }
                }
                if (index == Integer.MAX_VALUE) {
                    currentIndex.set(index % dataList.size());
                }
            }
        }, 0, timeInterval);
    }

}
