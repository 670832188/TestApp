package com.dev.kit.testapp.multiGroupHistogram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramChildData;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramGroupData;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramView;
import com.dev.kit.testapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cuiyan on 2018/5/7.
 */
public class MultiGroupHistogramActivity extends BaseStateViewActivity {
    private MultiGroupHistogramView multiGroupHistogramView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_multi_group_histogram, getFlContainer(), false);
    }

    private void init() {
        setOnClickListener(R.id.iv_left, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setText(R.id.tv_title, "分组直方图表");
        multiGroupHistogramView = findViewById(R.id.multiGroupHistogramView);
        initMultiGroupHistogramView();
        setContentState(STATE_DATA_CONTENT);
    }

    private void initMultiGroupHistogramView() {
        Random random = new Random();
        int groupSize = random.nextInt(5) + 10;
        List<MultiGroupHistogramGroupData> groupDataList = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            List<MultiGroupHistogramChildData> childDataList = new ArrayList<>();
            MultiGroupHistogramGroupData groupData = new MultiGroupHistogramGroupData();
            groupData.setGroupName("第" + (i + 1) + "组");
            MultiGroupHistogramChildData childData1 = new MultiGroupHistogramChildData();
            childData1.setSuffix("分");
            childData1.setValue(random.nextInt(50) + 51);
            childDataList.add(childData1);

            MultiGroupHistogramChildData childData2 = new MultiGroupHistogramChildData();
            childData2.setSuffix("%");
            childData2.setValue(random.nextInt(50) + 51);
            childDataList.add(childData2);
            groupData.setChildDataList(childDataList);
            groupDataList.add(groupData);
        }
        multiGroupHistogramView.setDataList(groupDataList);
        int[] color1 = new int[]{getResources().getColor(R.color.color_orange), getResources().getColor(R.color.colorPrimary)};

        int[] color2 = new int[]{getResources().getColor(R.color.color_supper_tip_normal), getResources().getColor(R.color.bg_supper_selected)};
        multiGroupHistogramView.setHistogramColor(color1, color2);
    }
}
