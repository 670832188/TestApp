package com.dev.kit.basemodule.surpport;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * @author cuiyan
 * Created on 2021/3/12.
 */
public class CommonViewPager extends ViewPager {
    private CommonPagerAdapter commonAdapter;

    public CommonViewPager(@NonNull Context context) {
        super(context);
    }

    public CommonViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof CommonPagerAdapter) {
            this.commonAdapter = (CommonPagerAdapter) adapter;
            setCurrentItem(500 * commonAdapter.getRealCount());
        }
    }

    public int getRealCurrentItem() {
        if (commonAdapter != null) {
            return commonAdapter.getRealPosition(super.getCurrentItem());
        } else {
            return super.getCurrentItem();
        }
    }
}
