package com.dev.kit.basemodule.turnpage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * @author yanglonghui
 */
public class BlackSquareFadeAway implements ITurnPage {

    private int duration = 850;//动画持续时间
    private final int leafNum = 5;
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public BlackSquareFadeAway() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTurnPageDraw(SurfaceHolder holder, Bitmap[] bitmap,
                               int maxWidth, int maxHeight) {

        int dx = (maxWidth - bitmap[0].getWidth()) / 2;
        int dy = (maxHeight - bitmap[0].getHeight()) / 2;
        int perWidth = maxWidth / leafNum;
        int row = maxHeight / perWidth;
        int perHeight = maxHeight / row;

        Rect[][] array = new Rect[row][leafNum];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = new Rect();
                array[i][j].set(j * perWidth, i * perHeight, (j + 1) * perWidth, (i + 1) * perHeight);

                if (j == array[i].length - 1) {
                    array[i][j].set(maxWidth - perWidth, array[i][j].top, maxWidth, array[i][j].bottom);
                }

                if (i == array.length - 1) {
                    array[i][j].set(array[i][j].left, maxHeight - perHeight, array[i][j].right, maxHeight);
                }
            }
        }

        long start = System.currentTimeMillis();
        long runMills = 0;


        Paint mRectPaint = new Paint();
        mRectPaint.setColor(Color.BLACK);
        Rect drawRect = new Rect();

        Canvas canvas = null;
        boolean isRunning = true;
        while (isRunning) {
            isRunning = ((runMills = (System.currentTimeMillis() - start)) < duration);
            if (!isRunning) {
                runMills = duration;
            }

            try {
                canvas = holder.lockCanvas(null);

                canvas.setDrawFilter(pdf);
                canvas.drawColor(Color.BLACK);// 清除画布

                if (!isRunning) {
                    canvas.drawBitmap(bitmap[0], dx, dy, null);
                } else {
                    canvas.save();
                    canvas.translate(dx, dy);
                    for (int i = 0; i < array.length; i++) {
                        for (int j = 0; j < array[i].length; j++) {
                            drawRect.set(array[i][j]);
                            int _dx = drawRect.width() / 2 - (int) (((float) runMills / (float) duration) * drawRect.width() / 2);
                            int _dy = drawRect.height() / 2 - (int) (((float) runMills / (float) duration) * drawRect.height() / 2);

                            drawRect.inset(_dx, _dy);
                            canvas.drawBitmap(bitmap[0], drawRect, drawRect, null);
                        }
                    }
                    canvas.restore();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != canvas) {
                    holder.unlockCanvasAndPost(canvas);
                } else {
                    break;
                }

                if (!isRunning) {
                    break;
                }
            }
        }
    }

    @Override
    public void onDestory() {
        // TODO Auto-generated method stub

    }

}
