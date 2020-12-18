package com.dev.kit.testapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.surpport.BaseRecyclerAdapter;
import com.dev.kit.basemodule.surpport.RecyclerDividerDecoration;
import com.dev.kit.basemodule.surpport.RecyclerViewHolder;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author cuiyan
 * Created on 2020/11/3.
 */
public class DecorationTestActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rvTest;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerDividerDecoration decoration;
    private List<String> testData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoration_test);
        init();
    }

    private void init() {
        rvTest = findViewById(R.id.rv_test);
        setOnClickListener(R.id.btn_1, this);
        setOnClickListener(R.id.btn_2, this);
        setOnClickListener(R.id.btn_3, this);
        for (int i = 0; i < 60; i++) {
            testData.add(String.valueOf(i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1: {
                layoutManager = new LinearLayoutManager(this);
                decoration = new RecyclerDividerDecoration(RecyclerDividerDecoration.LAYOUT_TYPE_VERTICAL, Color.GREEN, DisplayUtil.dp2px(10));
                break;
            }
            case R.id.btn_2: {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                decoration = new RecyclerDividerDecoration(RecyclerDividerDecoration.LAYOUT_TYPE_HORIZONTAL, Color.GREEN, DisplayUtil.dp2px(10));
                break;
            }
            case R.id.btn_3: {
                layoutManager = new GridLayoutManager(this, 5);
                decoration = new RecyclerDividerDecoration(RecyclerDividerDecoration.LAYOUT_TYPE_GRID, Color.GREEN, DisplayUtil.dp2px(10));
                break;
            }
        }
        reset();
    }

    private void reset() {
        rvTest.setLayoutManager(layoutManager);
        try {
            rvTest.removeItemDecorationAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rvTest.addItemDecoration(decoration);
        rvTest.setAdapter(new TestAdapter(this, testData));
    }

    private class TestAdapter extends BaseRecyclerAdapter<String> {

        private TestAdapter(Context context, List<String> dataList) {
            super(context, dataList, R.layout.item_test);
        }

        @Override
        public void fillData(RecyclerViewHolder holder, int position) {
            holder.setText(R.id.tv_test, getItem(position));
        }
    }
}
