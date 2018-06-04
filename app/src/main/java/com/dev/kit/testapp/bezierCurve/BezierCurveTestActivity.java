package com.dev.kit.testapp.bezierCurve;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/6/4.
 */
public class BezierCurveTestActivity extends BaseStateViewActivity {

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
    }
}
