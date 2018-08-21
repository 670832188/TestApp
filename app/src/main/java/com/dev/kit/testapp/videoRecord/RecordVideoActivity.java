package com.dev.kit.testapp.videoRecord;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.daasuu.camerarecorder.CameraRecordListener;
import com.daasuu.camerarecorder.CameraRecorder;
import com.daasuu.camerarecorder.CameraRecorderBuilder;
import com.daasuu.camerarecorder.LensFacing;
import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.util.ToastUtil;
import com.dev.kit.testapp.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by cuiyan on 2018/8/20.
 */
public class RecordVideoActivity extends BaseActivity {
    //    private FilterRecoderView recorderViews;
    private CameraRecorder cameraRecorder;
    private String recordFilePath;
    private String compressFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initRecorder();
        initView();
    }

    private void initView() {
        setOnClickListener(R.id.btn_start, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFilePath = getVideoOutputFilePath();
                cameraRecorder.start(recordFilePath);
            }
        });
        setOnClickListener(R.id.btn_stop, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraRecorder.stop();
            }
        });
    }

    private void initRecorder() {
        GLSurfaceView surfaceView = findViewById(R.id.GLSurfaceView);
        cameraRecorder = new CameraRecorderBuilder(this, surfaceView)
                .lensFacing(LensFacing.BACK)
                .videoSize(720, 1280)
                .cameraSize(1280, 720)
                .recordNoFilter(true)
                .cameraRecordListener(new CameraRecordListener() {
                    @Override
                    public void onGetFlashSupport(boolean b) {

                    }

                    @Override
                    public void onRecordComplete() {
                        ToastUtil.showToast(RecordVideoActivity.this, "视频录制完成");
                        compressVideo();
                    }

                    @Override
                    public void onError(Exception e) {

                    }

                    @Override
                    public void onCameraThreadFinish() {

                    }
                })
                .build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraRecorder != null) {
            cameraRecorder.release();
        }
    }

    private String getVideoOutputFilePath() {
        String videoDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "suiXueEdu";
        File videoDirFile = new File(videoDirPath);
        if (!videoDirFile.exists() && !videoDirFile.mkdir()) {
            return null;
        }
        String videoFileName = "suiXue_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".mp4";
        return videoDirPath + File.separator + videoFileName;
    }

    private void compressVideo() {

    }
}
