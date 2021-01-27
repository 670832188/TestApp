package com.dev.kit.basemodule.turnpage1;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.dev.kit.basemodule.util.DisplayUtil;

import java.util.Random;

/**
 * @author cuiyan
 * Created on 2021/1/21.
 */
public class TurnPageShutter implements ITurnPage {
    private int thickness;
    private int shutterNum;
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
            init(width, height);
        }
        canvas.drawColor(Color.LTGRAY);
        float degree = progress * 180;
        int VERTICAL = 1;
        if (orientation == VERTICAL) {
            Bitmap bitmap;
            if (degree <= 90) {
                bitmap = lastBitmap;
            } else {
                degree = degree - 180;
                bitmap = currentBitmap;
            }
            for (int i = 0; i < shutterNum; i++) {
                matrix.reset();
                camera.save();
                camera.rotateX(degree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(-width / 2f, -thickness / 2f);
                matrix.postTranslate(width / 2f, thickness / 2f);
                matrix.postTranslate(0, thickness * i);
                canvas.drawBitmap(clipBitmap(bitmap, 0, i * thickness, width, thickness), matrix, paint);
            }
        } else {
            Bitmap bitmap;
            if (degree <= 90) {
                bitmap = lastBitmap;
            } else {
                degree = degree - 180;
                bitmap = currentBitmap;
            }
            for (int i = 0; i < shutterNum; i++) {
                matrix.reset();
                camera.save();
                camera.rotateY(degree);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(-thickness / 2f, -width / 2f);
                matrix.postTranslate(thickness / 2f, width / 2f);
                matrix.postTranslate(thickness * i, 0);
                canvas.drawBitmap(clipBitmap(bitmap, i * thickness, 0, thickness, height), matrix, paint);
            }
        }
    }

    private void init(int width, int height) {
        Random random = new Random();
        orientation = random.nextInt(Integer.MAX_VALUE) % 2;
        int maxThickness = DisplayUtil.dp2px(30);
        int minThickness = DisplayUtil.dp2px(20);
        int totalThickness;
        int HORIZONTAL = 0;
        if (orientation == HORIZONTAL) {
            totalThickness = width;
        } else {
            totalThickness = height;
        }
        int maxCount = totalThickness / minThickness;
        int minCount = totalThickness / maxThickness;
        shutterNum = minCount + random.nextInt(maxCount - minCount);
        thickness = (int) (totalThickness * 1.f / shutterNum);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        camera = new Camera();
        matrix = new Matrix();
    }

    private Bitmap clipBitmap(Bitmap source, int left, int top, int width, int height) {
        return Bitmap.createBitmap(source, left, top, width, height);
    }

    @Override
    public long getPeriod() {
        return 1200;
    }
}
