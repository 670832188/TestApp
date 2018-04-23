package com.dev.kit.basemodule.pageTransformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 层叠式ViewPager PageTransformer
 * Created by cy on 2018/4/11.
 */

public class HorizontalStackTransformerWithRotation implements ViewPager.PageTransformer {
    private static final float CENTER_PAGE_SCALE = 0.8f;
    private int offscreenPageLimit;

    public HorizontalStackTransformerWithRotation(int offscreenPageLimit) {
        this.offscreenPageLimit = offscreenPageLimit;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position >= offscreenPageLimit || position <= -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        float translationX = (80 - view.getWidth()) * position;
        if (position >= 0) {
            view.setTranslationX(translationX);
        }
        if (position > -1 && position < 0) {
            float rotation = position * 30;
            view.setRotation(rotation);
            view.setAlpha((position * position * position + 1));
        } else {
            view.setRotation(0);
            view.setAlpha(1);
        }
        if (position == 0) {
            view.setScaleX(CENTER_PAGE_SCALE);
            view.setScaleY(CENTER_PAGE_SCALE);
        } else {
            view.setScaleX(CENTER_PAGE_SCALE - position * 0.1f);
            view.setScaleY(CENTER_PAGE_SCALE - position * 0.1f);
        }
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 3);
//        ((CardView) view).setCardElevation((offscreenPageLimit - position) * 3);
    }
}
