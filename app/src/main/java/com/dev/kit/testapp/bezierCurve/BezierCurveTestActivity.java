package com.dev.kit.testapp.bezierCurve;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.kit.basemodule.View.CustomIndicator;
import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.surpport.CommonPagerAdapter;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestActivity extends BaseStateViewActivity {

    private CustomIndicator customIndicator;
    private List<Integer> colorsRes = Arrays.asList(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.color_breakfast_tip_normal,  R.color.bg_lunch_selected, R.color.color_light_red);

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_bezier_curve_test, contentRoot, false);
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
        setText(R.id.tv_title, "贝塞尔曲线");
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
                drawable.setCornerRadius(DisplayUtil.dp2px(5));
                drawable.setStroke(DisplayUtil.dp2px(5), getResources().getColor(R.color.color_light_grey));
                drawable.setColor(color);
            }

            @NonNull
            @Override
            public View getPageItemView(@NonNull ViewGroup container, int position) {
                View view = LayoutInflater.from(BezierCurveTestActivity.this).inflate(R.layout.item_vp_test, container, false);
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
    }
}
