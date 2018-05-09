package com.dev.kit.testapp.multiGroupHistogram;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramChildData;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramGroupData;
import com.dev.kit.basemodule.multiChildHistogram.MultiGroupHistogramView;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by cuiyan on 2018/5/7.
 */
public class MultiGroupHistogramActivity extends BaseActivity {
    private OrientationEventListener orientationEventListener;
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
        initOrientationListener();
    }

    private void initOrientationListener() {
        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int screenOrientation = getResources().getConfiguration().orientation;
                if (orientation > 315 || orientation < 45 && orientation > 0) {
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else if (orientation > 45 && orientation < 135) {
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                } else if (orientation > 135 && orientation < 225) {
//                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
//                        LogUtil.e("kkkkkkkk: " + orientation);
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                    }
                } else if (orientation > 225 && orientation < 315) {
                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orientationEventListener != null) {
            orientationEventListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustUI(newConfig.orientation);
    }

    private void adjustUI(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                findViewById(R.id.ll_portrait_screen).setVisibility(View.GONE);
                findViewById(R.id.ll_landscape_screen).setVisibility(View.VISIBLE);
                multiGroupHistogramView.getLayoutParams().height = DisplayUtil.dp2px(210);
                break;
            }
            case Configuration.ORIENTATION_PORTRAIT: {
                findViewById(R.id.ll_portrait_screen).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_landscape_screen).setVisibility(View.GONE);
                multiGroupHistogramView.getLayoutParams().height = DisplayUtil.dp2px(280);
                break;
            }
        }
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
