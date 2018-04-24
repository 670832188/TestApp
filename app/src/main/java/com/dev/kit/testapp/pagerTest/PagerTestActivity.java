package com.dev.kit.testapp.pagerTest;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.pageTransformer.HorizontalStackTransformerWithRotation;
import com.dev.kit.basemodule.surpport.CommonPagerAdapter;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

import java.util.Arrays;
import java.util.List;

public class PagerTestActivity extends BaseActivity {
    ViewPager vpTest;
    List<Integer> colorsRes = Arrays.asList(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent, R.color.color_red, R.color.bg_lunch_selected, R.color.bg_supper_selected);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_test);
        init();
    }

    private void init() {
        vpTest = findViewById(R.id.vp_test);
        vpTest.setOffscreenPageLimit(3);
        CommonPagerAdapter<Integer> adapter = new CommonPagerAdapter<Integer>(colorsRes) {
            @Override
            public void renderItemView(@NonNull View itemView, final int position) {
//                itemView.setBackgroundResource(colorsRes.get(position));
                ((TextView) itemView).setText("第" + (position + 1) + "页面");
                int color = getResources().getColor(getBindItemData(position));
                GradientDrawable drawable = (GradientDrawable) itemView.getBackground();
                drawable.setStroke(DisplayUtil.dp2px(5), getResources().getColor(R.color.color_light_grey));
                drawable.setColor(color);
            }

            @NonNull
            @Override
            public View getPageItemView(@NonNull ViewGroup container, int position) {
                View view = LayoutInflater.from(PagerTestActivity.this).inflate(R.layout.item_vp_test, container, false);
                view.setTag(String.valueOf(position));
                return view;
            }
        };
        adapter.setOnItemClickListener(new CommonPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showToast("当前点击第" + (position + 1) + "页");
            }
        });

        vpTest.setPageTransformer(false, new HorizontalStackTransformerWithRotation(vpTest));
        vpTest.setAdapter(adapter);
    }
}
