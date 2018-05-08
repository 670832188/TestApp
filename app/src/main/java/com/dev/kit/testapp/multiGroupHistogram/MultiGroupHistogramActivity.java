package com.dev.kit.testapp.multiGroupHistogram;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseActivity;
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
public class MultiGroupHistogramActivity extends BaseActivity {
    private MultiGroupHistogramView multiGroupHistogramView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_group_histogram);
        init();
    }

    private void init() {
        setOnClickListener(R.id.iv_left, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setText(R.id.tv_title, "小组测试排行榜");
        multiGroupHistogramView = findViewById(R.id.multiGroupHistogramView);
        initMultiGroupHistogramView();
    }

    private void initMultiGroupHistogramView() {
        Random random = new Random();
        int groupSize = random.nextInt(10) + 10;
        List<MultiGroupHistogramGroupData> groupDataList = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            List<MultiGroupHistogramChildData> childDataList = new ArrayList<>();
            MultiGroupHistogramGroupData groupData = new MultiGroupHistogramGroupData();
            groupData.setGroupName("第" + (i + 1) + "组");
            MultiGroupHistogramChildData childData1 = new MultiGroupHistogramChildData();
            childData1.setSuffix("%");
            childData1.setValue(random.nextInt(50) + 51);
            childDataList.add(childData1);

            MultiGroupHistogramChildData childData2 = new MultiGroupHistogramChildData();
            childData2.setSuffix("分");
            childData2.setValue(random.nextInt(50) + 51);
            childDataList.add(childData2);
            groupData.setChildDataList(childDataList);
            groupDataList.add(groupData);
        }
        multiGroupHistogramView.setDataList(groupDataList);
        int[] color1 = new int[]{Color.parseColor("#FFD100"), Color.parseColor("#FF3300")};

        int[] color2 = new int[]{Color.parseColor("#1DB890"), Color.parseColor("#4576F9")};
        multiGroupHistogramView.setHistogramColor(color1, color2);
    }
}
