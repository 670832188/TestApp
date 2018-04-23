package com.dev.kit.basemodule.pageTransformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

public class VerticalStackPageTransformerWithRotation implements ViewPager.PageTransformer {

    private static final float CENTER_PAGE_SCALE = 0.8f;
    private int offscreenPageLimit;
    private ViewPager boundViewPager;

    public VerticalStackPageTransformerWithRotation(ViewPager boundViewPager) {
        this.boundViewPager = boundViewPager;
        offscreenPageLimit = boundViewPager.getOffscreenPageLimit();
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pagerHeight = boundViewPager.getHeight();
        float verticalOffsetBase = (pagerHeight - pagerHeight * CENTER_PAGE_SCALE) / 2 / offscreenPageLimit + DisplayUtil.dp2px(15);
        if (position >= offscreenPageLimit || position <= -1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        float translationX = -view.getWidth() * position;
        float translationY = -verticalOffsetBase * position;
        if (position >= 0) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
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
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5);
//        ((CardView) view).setCardElevation((offscreenPageLimit - position) * 3);
    }
}
