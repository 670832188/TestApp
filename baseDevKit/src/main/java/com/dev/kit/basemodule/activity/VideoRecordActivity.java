package com.dev.kit.basemodule.activity;

import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by cuiyan on 2018/8/8.
 */
public class VideoRecordActivity extends BaseStateViewActivity {

    public static final int RECORDER_STATE_INIT = 0;
    public static final int RECORDER_STATE_RECORDING = 1;
    public static final int RECORDER_STATE_PAUSE = 2;
    private int recorderState;
    private List<String> videosPathList = new ArrayList<>();
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder.Callback surfaceCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            releaseCamera();
        }
    };
    private MediaRecorder.OnErrorListener recordErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_video_record, contentRoot, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.record_surface);
        final CheckBox ckbRecordTrigger = findViewById(R.id.ckb_record_trigger);
        final CheckBox ckbPauseTrigger = findViewById(R.id.ckb_pause_trigger);
        ImageView ivResetRecord = findViewById(R.id.iv_reset_record);
        ckbRecordTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recorderState = startRecord() ? RECORDER_STATE_RECORDING : RECORDER_STATE_INIT;
                    if (recorderState == RECORDER_STATE_INIT) {
                        ckbRecordTrigger.setChecked(false);
                    } else {
                        ckbPauseTrigger.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (recorderState == RECORDER_STATE_RECORDING) {
                        ckbRecordTrigger.setEnabled(false);
                        stopRecord();
                        // ToDo 录制结束处理
                    }
                }
            }
        });

        ckbPauseTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pauseRecord();
                } else {
                    startRecord();
                    ckbPauseTrigger.setVisibility(View.GONE);
                }
            }
        });

        //配置SurfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        surfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        surfaceHolder.setKeepScreenOn(true);
        //回调接口
        surfaceHolder.addCallback(surfaceCallBack);
        setContentState(STATE_DATA_CONTENT);
    }


    private void initCamera() {
        if (camera != null) {
            releaseCamera();
        }

        camera = Camera.open();
        if (camera == null) {
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //将相机与SurfaceHolder绑定
            camera.setPreviewDisplay(surfaceHolder);
            //配置CameraParams
            configCameraParams();
            //启动相机预览
            camera.startPreview();
        } catch (IOException e) {
            //有的手机会因为兼容问题报错，这就需要开发者针对特定机型去做适配了
            e.printStackTrace();
        }
    }

    private void configCameraParams() {
        Camera.Parameters params = camera.getParameters();
        //设置相机的横竖屏(竖屏需要旋转90°)
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
        } else {
            params.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
        }
        //设置聚焦模式
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        //缩短Recording启动时间
        params.setRecordingHint(true);
        //影像稳定能力
        if (params.isVideoStabilizationSupported())
            params.setVideoStabilization(true);
        camera.setParameters(params);
    }


    /**
     * 释放摄像头资源
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void configMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setOnErrorListener(recordErrorListener);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
//        mediaRecorder.setAudioChannels(2);
        //设置最大录像时间 单位：毫秒
//        mediaRecorder.setMaxDuration(60 * 1000);
        //设置最大录制的大小 单位，字节
//        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(90);
        //设置录像的分辨率
        mediaRecorder.setVideoSize(352, 288);
        //设置录像视频输出地址
        mediaRecorder.setOutputFile(videosPathList.get(videosPathList.size() - 1));
    }

    public boolean startRecord() {
        initCamera();
        //录制视频前必须先解锁Camera
        camera.unlock();
        String videoDirPath = getVideoDirPath();
        if (TextUtils.isEmpty(videoDirPath)) {
            // ToDo
            ToastUtil.showToast(this, "创建文件夹失败");
            return false;
        }
        String videoPath = videoDirPath + File.separator + getVideoName();
        videosPathList.add(videoPath);
        configMediaRecorder();
        try {
            //开始录制
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            videosPathList.remove(videosPathList.size() - 1);
            return false;
        }
        return true;
    }

    public void stopRecord() {
        // 设置后不会崩
        mediaRecorder.setOnErrorListener(null);
        mediaRecorder.setPreviewDisplay(null);
        //停止录制
        mediaRecorder.stop();
        mediaRecorder.reset();
        //释放资源
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void pauseRecord() {
        recorderState = RECORDER_STATE_PAUSE;
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success)
                    camera.cancelAutoFocus();
            }
        });
        stopRecord();
    }

    private String getVideoDirPath() {
        String videoDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "suiXueEdu";
        File videoDirFile = new File(videoDirPath);
        if (!videoDirFile.exists()) {
            if (videoDirFile.mkdir()) {
                return videoDirPath;
            } else {
                return null;
            }
        }
        return videoDirPath;
    }

    private String getVideoName() {
        return "suiXue_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".mp4";
    }
}
