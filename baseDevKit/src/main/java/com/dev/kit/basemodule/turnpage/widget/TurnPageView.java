package com.dev.kit.basemodule.turnpage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.dev.kit.basemodule.turnpage.ITurnPage;

/**
 * @author yanglonghui
 */
public class TurnPageView extends SurfaceView {

    private GestureDetector mGestureDetector; // 手势
    private IFillingEvent mFillingListener;
    private ITurnPage mTrunPageAnimation;
    private boolean isPause = true;
    private DrawThread drawThread;
    private SurfaceHolder holder;
    private Object mObject = new Object();
    private Bitmap[] mBitmaps;

    public TurnPageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TurnPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TurnPageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        holder = this.getHolder();
        setZOrderOnTop(true);
        holder.addCallback(callBack);
        holder.setFormat(PixelFormat.TRANSPARENT);
        setFocusableInTouchMode(true);
        mGestureDetector = new GestureDetector(mSimpleOnGestureListener);
    }

    public void setOnFillingListener(IFillingEvent mFillingListener) {
        this.mFillingListener = mFillingListener;
    }


    public void setTurnPageStyle(ITurnPage mTrunPageAnimation) {
        this.mTrunPageAnimation = mTrunPageAnimation;
    }

    public void setBitmaps(Bitmap[] mBitmaps) {
        this.mBitmaps = mBitmaps;
    }

    private Callback callBack = new Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
//			Log.v("surfaceDestroyed ", "------------");
            drawThread.isRunning = false;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//			Log.v("surfaceCreated ", "------------");
            if (null != drawThread) {
                drawThread.isRunning = false;
            }
            drawThread = new DrawThread();
            drawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
//			Log.v("surfaceChanged: format:"+format, "width："+width+"height: "+height);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event); //通知手势识别方法
        return true;
    }


    private SimpleOnGestureListener mSimpleOnGestureListener = new SimpleOnGestureListener() {
        final int MIN_DISTANCE = 100;
        final int MIN_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean isHor = (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()));
            if (isHor) {
                if (e1.getX() - e2.getX() > MIN_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY) {
                    Log.v("SimpleOnGestureListener", "Fling left");

                    if (null != mFillingListener) {
                        mFillingListener.onFlingLeft();
                    }
                    notifyTurnPage();
                } else if (e2.getX() - e1.getX() > MIN_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY) {
                    Log.v("SimpleOnGestureListener", "Fling right");

                    if (null != mFillingListener) {
                        mFillingListener.onFlingRight();
                    }
                    notifyTurnPage();
                }
            } else {
                if (e1.getY() - e2.getY() > MIN_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY) {
                    Log.v("SimpleOnGestureListener", "Fling up");
                    if (null != mFillingListener) {
                        mFillingListener.onFlingUp();
                    }
                    notifyTurnPage();
                } else if (e2.getY() - e1.getY() > MIN_DISTANCE && Math.abs(velocityX) > MIN_VELOCITY) {
                    Log.v("SimpleOnGestureListener", "Fling down");
                    if (null != mFillingListener) {
                        mFillingListener.onFlingDown();
                    }
                    notifyTurnPage();
                }
            }
            return true;
        }
    };

    // let's be nice with the cpu
    @Override
    public void setVisibility(int visibility) {
        if (getVisibility() != visibility) {
            super.setVisibility(visibility);
            isPause = (visibility == GONE || visibility == INVISIBLE);
        }
    }

    // let's be nice with the cpu
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        isPause = (visibility == GONE || visibility == INVISIBLE);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isPause = (visibility == GONE || visibility == INVISIBLE);
    }

    private void notifyTurnPage() {
        synchronized (mObject) {
            mObject.notifyAll();
        }
    }

    private class DrawThread extends Thread {
        private boolean isRunning = true;

        @Override
        public void run() {
            try {
                while (isRunning) {
                    if (!isPause) {
                        if (null != mTrunPageAnimation && null != mBitmaps) {
                            clearDraw();

                            mTrunPageAnimation.onCreate();
                            mTrunPageAnimation.onTurnPageDraw(holder, mBitmaps, getWidth(), getHeight());
                            mTrunPageAnimation.onDestory();
                            mTrunPageAnimation = null;

                            synchronized (mObject) {
                                mObject.wait();
                            }
                        } else {
                            Thread.sleep(50);
                            break;
                        }
                    } else {
                        Thread.sleep(500);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearDraw() {
        Canvas canvas = holder.lockCanvas(null);
        canvas.drawColor(Color.BLACK);// 清除画布
        holder.unlockCanvasAndPost(canvas);
    }
}
