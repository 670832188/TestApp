package com.dev.kit.testapp.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.testapp.MainApplication;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/5/16.
 */
public class SettingActivity extends BaseStateViewActivity implements View.OnClickListener {

    private float fontScale;
    private TextView tvTitle;
    private TextView tvDecreaseFontSize;
    private TextView tvIncreaseFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_setting, contentRoot, false);
    }

    private void init() {
        fontScale = MainApplication.getFontScale();
        tvTitle = findViewById(R.id.tv_title);
        tvDecreaseFontSize = findViewById(R.id.tv_decrease_font_size);
        tvIncreaseFontSize = findViewById(R.id.tv_increase_font_size);
        tvDecreaseFontSize.setOnClickListener(this);
        tvIncreaseFontSize.setOnClickListener(this);
        setText(R.id.tv_title, "设置应用字体大小");
        setOnClickListener(R.id.iv_left, this);
        setContentState(STATE_DATA_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_decrease_font_size: {
                fontScale -= 0.05;
                setFontScale();
                break;
            }
            case R.id.tv_increase_font_size: {
                fontScale += 0.05;
                setFontScale();
                break;
            }
            case R.id.iv_left: {
                finish();
                break;
            }
        }
    }

    private synchronized void setFontScale() {
        MainApplication.setAppFontSize(fontScale);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.getScaleFactor() * 18 * fontScale);
        tvDecreaseFontSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.getScaleFactor() * 14 * fontScale);
        tvIncreaseFontSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.getScaleFactor() * 14 * fontScale);
    }
}
