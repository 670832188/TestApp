package com.dev.kit.basemodule.turnpage1;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;

/**
 * @author cuiyan
 * Created on 2021/1/25.
 */
public class TurnPage3DRotation implements ITurnPage {
    private int orientation;
    private Paint paint;
    private Camera camera;
    private Matrix matrix;

    @Override
    public void onTurnPageDraw(Canvas canvas, Bitmap currentBitmap, Bitmap lastBitmap, int width, int height, float progress) {
        if (lastBitmap == null) {
            canvas.drawBitmap(currentBitmap, 0, 0, null);
            return;
        }
        if (progress == 0) {
            init();
        }
        canvas.drawColor(Color.LTGRAY);
        float degree = progress * 90;
        int VERTICAL = 1;
        if (orientation == VERTICAL) {
            float rotatePivotY = (90 - degree) / 90f * height;

            matrix.reset();
            camera.save();
            camera.rotateX(degree);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-width / 2f, -height);
            matrix.postTranslate(width / 2f, rotatePivotY);
            canvas.drawBitmap(lastBitmap, matrix, paint);

            matrix.reset();
            camera.save();
            camera.rotateX(degree - 90);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-width / 2f, 0);
            matrix.postTranslate(width / 2f, rotatePivotY);
            canvas.drawBitmap(currentBitmap, matrix, paint);
        } else {
            float rotatePivotX = (90 - degree) / 90 * width;

            matrix.reset();
            camera.save();
            camera.rotateY(-degree);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-width, -height / 2f);
            matrix.postTranslate(rotatePivotX, height / 2f);
            canvas.drawBitmap(lastBitmap, matrix, paint);

            matrix.reset();
            camera.save();
            camera.rotateY(90 - degree);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0, -height / 2f);
            matrix.postTranslate(rotatePivotX, height / 2f);
            canvas.drawBitmap(currentBitmap, matrix, paint);
        }
    }

    private void init() {
        Random random = new Random();
        orientation = random.nextInt(Integer.MAX_VALUE) % 2;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        camera = new Camera();
        matrix = new Matrix();
    }

    @Override
    public long getPeriod() {
        return 800;
    }
}
