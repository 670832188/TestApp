package com.dev.kit.testapp.pagerTest;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.pageTransformer.HorizontalStackTransformerWithRotation;
import com.dev.kit.basemodule.pageTransformer.VerticalStackPageTransformerWithRotation;
import com.dev.kit.basemodule.surpport.CommonPagerAdapter;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PagerTestActivity extends BaseStateViewActivity {
    private List<Integer> colorsRes = Arrays.asList(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorAccent, R.color.color_red, R.color.bg_lunch_selected, R.color.bg_supper_selected);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_pager_test, contentRoot, false);
    }

    private void init() {
        setOnClickListener(R.id.iv_left, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setText(R.id.tv_title, "层叠ViewPager");
        ViewPager vpHorizontalStack = findViewById(R.id.vp_horizontal_stack);
        vpHorizontalStack.setOffscreenPageLimit(3);
        CommonPagerAdapter<Integer> horizontalStackAdapter = new CommonPagerAdapter<Integer>(colorsRes) {
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
                View view = LayoutInflater.from(PagerTestActivity.this).inflate(R.layout.item_vp_test, container, false);
                view.setTag(String.valueOf(position));
                return view;
            }
        };
        horizontalStackAdapter.setOnItemClickListener(new CommonPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showToast("当前点击第" + (position + 1) + "页");
            }
        });

        vpHorizontalStack.setPageTransformer(false, new HorizontalStackTransformerWithRotation(vpHorizontalStack));
        vpHorizontalStack.setAdapter(horizontalStackAdapter);

        ViewPager vpVerticalStack = findViewById(R.id.vp_vertical_stack);
        vpVerticalStack.setOffscreenPageLimit(3);
        CommonPagerAdapter<Integer> verticalStackAdapter = new CommonPagerAdapter<Integer>(deepCopyList(colorsRes)) {
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
                View view = LayoutInflater.from(PagerTestActivity.this).inflate(R.layout.item_vp_test, container, false);
                view.setTag(String.valueOf(position));
                return view;
            }
        };
        verticalStackAdapter.setOnItemClickListener(new CommonPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showToast("当前点击第" + (position + 1) + "页");
            }
        });

        vpVerticalStack.setPageTransformer(false, new VerticalStackPageTransformerWithRotation(vpVerticalStack));
        vpVerticalStack.setAdapter(verticalStackAdapter);
        setContentState(STATE_DATA_CONTENT);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> deepCopyList(List<T> src) {
        List<T> dest = new ArrayList<>();
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
        }
        return dest;
    }
}
