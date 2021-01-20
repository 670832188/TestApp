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
public class ShutterRight2Left implements ITurnPage {

    private int duration = 350;//动画持续时间
    private final int leafNum = 5;
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

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

        long start = System.currentTimeMillis();
        long runMills = 0;

        Rect src = new Rect();
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

                canvas.save();
                canvas.translate(dx, dy);
                for (int j = 0; j < leafNum; j++) {
                    src.set((int) ((j + 1) * perWidth - ((float) runMills / (float) duration) * perWidth), 0, (j + 1) * perWidth, maxHeight);
                    canvas.drawBitmap(bitmap[0], src, src, null);
                }
                canvas.restore();

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
