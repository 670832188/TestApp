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
        if (position >= 0) {
            float translationX = -view.getWidth() * position;
            float translationY = -verticalOffsetBase * position;
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
        if (position > -1 && position < 0) {
            float rotation = position * 30;
            view.setRotation(rotation);
            view.setAlpha((position * position * position + 1));
        } else if (position > offscreenPageLimit - 1) {
            view.setAlpha((float) (1 - position + Math.floor(position)));
        } else {
            view.setRotation(0);
            view.setAlpha(1);
        }
        if (position == 0) {
            view.setScaleX(CENTER_PAGE_SCALE);
            view.setScaleY(CENTER_PAGE_SCALE);
        } else {
            float scaleFactor = Math.min(CENTER_PAGE_SCALE - position * 0.1f, CENTER_PAGE_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
        ViewCompat.setElevation(view, (offscreenPageLimit - position) * 5);
    }
}
