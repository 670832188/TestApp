package com.dev.kit.basemodule.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.kit.basemodule.R;


public class ExpandTextView extends LinearLayout {
    public static final int DEFAULT_MAX_LINES = 3;
    private TextView tvContentText;
    private TextView tvTrigger;
    private String foldStateTriggerText;
    private String unfoldStateTriggerText;
    private int triggerTextColor;
    private int defaultShowLines;

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandTextView, 0, 0);
        foldStateTriggerText = typedArray.getString(R.styleable.ExpandTextView_foldStateTriggerText);
        unfoldStateTriggerText = typedArray.getString(R.styleable.ExpandTextView_unfoldStateTriggerText);
        triggerTextColor = typedArray.getColor(R.styleable.ExpandTextView_triggerTextColor, Color.parseColor("#4D202332"));
        defaultShowLines = typedArray.getInt(R.styleable.ExpandTextView_defaultShownLines, DEFAULT_MAX_LINES);
        if (TextUtils.isEmpty(foldStateTriggerText)) {
            foldStateTriggerText = getResources().getString(R.string.action_unfold);
        }
        if (TextUtils.isEmpty(unfoldStateTriggerText)) {
            unfoldStateTriggerText = getResources().getString(R.string.action_fold);
        }
        typedArray.recycle();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_foldable_tv_trigger, this);
        tvContentText = findViewById(R.id.tv_content);

        tvTrigger = findViewById(R.id.tv_trigger);
        tvTrigger.setTextColor(triggerTextColor);
        tvTrigger.setOnClickListener(v -> {
            String textStr = tvTrigger.getText().toString().trim();
            if (foldStateTriggerText.equals(textStr)) {
                tvContentText.setMaxLines(Integer.MAX_VALUE);
                tvTrigger.setText(unfoldStateTriggerText);
            } else {
                tvContentText.setMaxLines(defaultShowLines);
                tvTrigger.setText(foldStateTriggerText);
            }
        });
    }

    public void setText(String contentText) {
        tvContentText.setText(contentText);

        tvContentText.post(() -> {
            int linCount = tvContentText.getLineCount();
            if (linCount > defaultShowLines) {
                tvContentText.setMaxLines(defaultShowLines);
                tvTrigger.setVisibility(View.VISIBLE);
                tvTrigger.setText(foldStateTriggerText);
            } else {
                tvTrigger.setVisibility(View.GONE);
            }
        });
    }

    public void setTextSize(int size) {
        tvContentText.setTextSize(size);
    }

    public float getTextSize() {
        return tvContentText.getTextSize();
    }
}
