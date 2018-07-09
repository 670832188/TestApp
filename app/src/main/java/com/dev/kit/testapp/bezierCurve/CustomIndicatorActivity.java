package com.dev.kit.testapp.bezierCurve;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.kit.basemodule.view.CustomIndicator;
import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.surpport.CommonPagerAdapter;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class CustomIndicatorActivity extends BaseStateViewActivity implements View.OnClickListener {

    private CustomIndicator customIndicator;
    private List<Integer> colorsRes = Arrays.asList(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.color_breakfast_tip_normal, R.color.bg_lunch_selected, R.color.color_light_red);

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_custom_indicator, contentRoot, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentState(STATE_DATA_CONTENT);
        setOnClickListener(R.id.iv_left, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setText(R.id.tv_title, "自定义Indicator");
        customIndicator = findViewById(R.id.CustomIndicator);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        CommonPagerAdapter<Integer> adapter = new CommonPagerAdapter<Integer>(colorsRes) {
            @Override
            public void renderItemView(@NonNull View itemView, final int position) {
                ((TextView) itemView).setText("第" + (position + 1) + "页面");
                int color = getResources().getColor(getBindItemData(position));
                GradientDrawable drawable = (GradientDrawable) itemView.getBackground();
                if (drawable == null) {
                    drawable = new GradientDrawable();
                    itemView.setBackground(drawable);
                }
//                drawable.setCornerRadius(DisplayUtil.dp2px(5));
                drawable.setStroke(DisplayUtil.dp2px(1), getResources().getColor(R.color.color_light_grey));
                drawable.setColor(color);
            }

            @NonNull
            @Override
            public View getPageItemView(@NonNull ViewGroup container, int position) {
                View view = LayoutInflater.from(CustomIndicatorActivity.this).inflate(R.layout.item_vp_test, container, false);
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
        viewPager.setAdapter(adapter);
        customIndicator.bindViewPager(viewPager);
        setOnClickListener(R.id.tv_type1, this);
        setOnClickListener(R.id.tv_type2, this);
        setOnClickListener(R.id.tv_type3, this);
        setOnClickListener(R.id.tv_type4, this);
    }

    @Override
    public void onClick(View v) {
        int type = -1;
        switch (v.getId()) {
            case R.id.tv_type1: {
                type = CustomIndicator.INDICATOR_TYPE_SCALE;
                break;
            }
            case R.id.tv_type2: {
                type = CustomIndicator.INDICATOR_TYPE_GRADUAL;
                break;
            }
            case R.id.tv_type3: {
                type = CustomIndicator.INDICATOR_TYPE_SPLIT;
                break;
            }
            case R.id.tv_type4: {
                type = CustomIndicator.INDICATOR_TYPE_SCALE_AND_GRADUAL;
                break;
            }
        }
        if (type != -1) {
            customIndicator.setIndicatorType(type);
        }
    }
}
