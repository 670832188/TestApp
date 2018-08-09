package com.dev.kit.basemodule.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.basemodule.util.ToastUtil;
import com.dev.kit.basemodule.util.VideoUtil;

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
    public static final String RECORDED_VIDEO_PATH = "recordedVideoPath";
    private boolean isRecording;
    private List<String> videosPathList = new ArrayList<>();
    private String targetVideoPath;
    private Camera camera;
    private CheckBox ckbRecordTrigger;
    private CheckBox ckbPauseTrigger;
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
        setContentState(STATE_DATA_CONTENT);
        SurfaceView surfaceView = findViewById(R.id.record_surface);
        ckbRecordTrigger = findViewById(R.id.ckb_record_trigger);
        ckbPauseTrigger = findViewById(R.id.ckb_pause_trigger);
        ckbRecordTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!(isRecording = startRecord())) {
                        ckbRecordTrigger.setChecked(false);
                    } else {
                        ckbPauseTrigger.setVisibility(View.VISIBLE);
                    }
                } else {
                    finishRecord();
                }
            }
        });

        ckbPauseTrigger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pauseRecord();
                } else {
                    isRecording = startRecord();
                }
            }
        });

        //配置SurfaceHolder
        surfaceHolder = surfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        surfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        surfaceHolder.setKeepScreenOn(true);
        //回调接口
        surfaceHolder.addCallback(surfaceCallBack);
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
//        initCamera();
        //录制视频前必须先解锁Camera
        camera.unlock();
        String videoDirPath = getVideoDirPath();
        if (TextUtils.isEmpty(videoDirPath)) {
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
            LogUtil.e(e.getMessage(), e);
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
        isRecording = false;
    }

    private void pauseRecord() {
        if (camera != null) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success)
                        camera.cancelAutoFocus();
                }
            });
        }
        stopRecord();
    }

    private void finishRecord() {
        ckbRecordTrigger.setEnabled(false);
        stopRecord();
        handleRecord();
        ckbPauseTrigger.setVisibility(View.GONE);
    }

    private void handleRecord() {
        if (videosPathList.size() > 1) {
            VideoUtil.mergeVideos(getVideoDirPath() + File.separator + getVideoName(), videosPathList, new VideoUtil.VideoMergeListener() {
                @Override
                public void onMergeStart() {

                }

                @Override
                public void onMergeSuccess(String mergedVideoPath) {
                    targetVideoPath = mergedVideoPath;
                    for (String path : videosPathList) {
                        File file = new File(path);
                        Log.e(getClass().getSimpleName(), "delete video clip " + path + " " + file.delete());
                    }
                    videosPathList.clear();
                    showFinishUI();
                }

                @Override
                public void onMergeFailed(Exception e) {
                    LogUtil.e(e.getMessage(), e);
                }
            });
        } else {
            targetVideoPath = videosPathList.get(0);
            videosPathList.clear();
            showFinishUI();
        }
    }

    private void showFinishUI() {
        final View completeView = findViewById(R.id.tv_complete);
        completeView.setVisibility(View.VISIBLE);
        completeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(RECORDED_VIDEO_PATH, targetVideoPath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        final View resetView = findViewById(R.id.tv_reset);
        resetView.setVisibility(View.VISIBLE);
        resetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(targetVideoPath);
                Log.e(getClass().getSimpleName(), "reset video record and delete recorded video " + file.getAbsolutePath() + " " + file.delete());
                ckbRecordTrigger.setEnabled(true);
                completeView.setVisibility(View.GONE);
                resetView.setVisibility(View.GONE);
            }
        });

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

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording) {
            ckbPauseTrigger.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            initCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}
