package com.dev.kit.basemodule.turnpage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Matrix:
 * 1.0   0.0   0.0      scaleX   skewX    transX
 * 0.0   1.0   0.0      skewY    scaleY   transY
 * 0.0   0.0   1.0      persp0   persp1   persp2
 *
 * @author cuiyan
 * Created on 2021/1/21.
 */
public interface ITurnPage {
    /**
     * @param progress [0,1]
     */
    void onTurnPageDraw(Canvas canvas, Bitmap currentBitmap, Bitmap lastBitmap, int width, int height, float progress);

    long getPeriod();
}
