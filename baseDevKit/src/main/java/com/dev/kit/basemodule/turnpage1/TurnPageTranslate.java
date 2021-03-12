package com.dev.kit.basemodule.turnpage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * @author cuiyan
 * Created on 2021/1/27.
 */
public class TurnPageTranslate implements ITurnPage {
    private Paint paint;
    private Matrix matrix;

    @Override
    public void onTurnPageDraw(Canvas canvas, Bitmap currentBitmap, Bitmap lastBitmap, int width, int height, float progress) {
        if (lastBitmap == null) {
            canvas.drawBitmap(currentBitmap, 0, 0, null);
            return;
        }
        progress = progress * progress;
        if (progress == 0) {
            init();
        }
        canvas.drawColor(Color.LTGRAY);
        matrix.setTranslate(-width * progress, 0);
        canvas.drawBitmap(lastBitmap, matrix, paint);

        matrix.setTranslate(width - width * progress, 0);
        canvas.drawBitmap(currentBitmap, matrix, paint);
    }

    @Override
    public long getPeriod() {
        return 800;
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        matrix = new Matrix();
    }
}
