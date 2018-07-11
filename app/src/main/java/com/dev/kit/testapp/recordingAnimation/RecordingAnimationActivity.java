package com.dev.kit.testapp.recordingAnimation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.view.AudioSignalView;
import com.dev.kit.testapp.R;

/**
 * Created by cuiyan on 2018/7/11.
 */
public class RecordingAnimationActivity extends BaseStateViewActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_audio_signal, contentRoot, false);
    }

    private void initView() {
        setText(R.id.tv_title, "音频动画");
        setOnClickListener(R.id.iv_left, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final AudioSignalView audioSignalView1 = findViewById(R.id.audio_signal_view_1);
        final AudioSignalView audioSignalView2 = findViewById(R.id.audio_signal_view_2);
        final AudioSignalView audioSignalView3 = findViewById(R.id.audio_signal_view_3);
        findViewById(R.id.tv_trigger).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.performClick();
                        audioSignalView1.startAudioSignal();
                        audioSignalView2.startAudioSignal();
                        audioSignalView3.startAudioSignal();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        audioSignalView1.stopAudioSignal();
                        audioSignalView2.stopAudioSignal();
                        audioSignalView3.stopAudioSignal();
                        break;
                    }
                }
                return false;
            }
        });
        setContentState(STATE_DATA_CONTENT);
    }
}
