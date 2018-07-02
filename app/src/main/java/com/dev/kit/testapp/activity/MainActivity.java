package com.dev.kit.testapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.netRequest.util.CommonInterceptor;
import com.dev.kit.basemodule.util.FileUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.basemodule.util.PermissionRequestUtil;
import com.dev.kit.basemodule.view.AudioSignalView;
import com.dev.kit.testapp.R;
import com.dev.kit.testapp.animation.PropertyAnimationEntryActivity;
import com.dev.kit.testapp.bezierCurve.BezierCurveTestActivity;
import com.dev.kit.testapp.multiGroupHistogram.MultiGroupHistogramActivity;
import com.dev.kit.testapp.pagerTest.PagerTestActivity;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.ApiService;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.NetRequestDemoActivity;

import java.io.File;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseStateViewActivity implements View.OnClickListener {

    private AudioSignalView audioSignalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            LogUtil.e("getSavedState: " + savedInstanceState.getString("saveState"));
        }
        init();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        String language = locale.getLanguage() + "-" + locale.getCountry();
        LogUtil.e("language: " + language);
        permissionTest();
    }

    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup contentRoot) {
        return inflater.inflate(R.layout.activity_main, contentRoot, false);
    }

    private void init() {
        setText(R.id.tv_title, R.string.app_name);
        setOnClickListener(R.id.iv_left, this);
        setOnClickListener(R.id.tv_net_test, this);
        setOnClickListener(R.id.tv_upload_file, this);
        setOnClickListener(R.id.tv_vp_test, this);
        setOnClickListener(R.id.tv_property_animation, this);
        setOnClickListener(R.id.tv_MultiGroupHistogramView, this);
        setOnClickListener(R.id.tv_set_font, this);
        setOnClickListener(R.id.tv_bezier_curve, this);
        setOnClickListener(R.id.tv_audio_animation, this);
        audioSignalView = findViewById(R.id.audio_signal_view);
        setContentState(STATE_DATA_CONTENT);
    }

    private void uploadFile() {
        CommonInterceptor.updateOrInsertCommonParam("key1", "value1");
        String dirFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "123";
        File file = null;
        if (FileUtil.isDir(dirFilePath)) {
            File dirFile = new File(dirFilePath);
            File[] fileList = dirFile.listFiles();
            if (fileList != null && fileList.length > 0) {
                Random random = new Random();
                int fileIndex = random.nextInt(fileList.length);
                file = fileList[fileIndex];
            }
        } else {
            return;
        }
        if (file == null) {
            return;
        }
        LogUtil.e("fileName: " + file.getName());
        RequestBody userParamBody = RequestBody.create(null, "zhangsan");
        String fileType = FileUtil.getMimeType(file.getAbsolutePath());
        MediaType mediaType = MediaType.parse(fileType);
        RequestBody fileParamBody = RequestBody.create(mediaType, file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("userAvatar", file.getName(), fileParamBody);
        NetRequestSubscriber<String> subscriber = new NetRequestSubscriber<>(new NetRequestCallback<String>() {
        }, this);
        Observable<String> observable = BaseServiceUtil.createService(ApiService.class).uploadFile(userParamBody, filePart);
        BaseController.sendRequest(this, subscriber, observable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left: {
                finish();
                break;
            }
            case R.id.tv_net_test: {
                startActivity(new Intent(MainActivity.this, NetRequestDemoActivity.class));
                break;
            }
            case R.id.tv_upload_file: {
                if (PermissionRequestUtil.isPermissionGranted(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    uploadFile();
                } else {
                    PermissionRequestUtil.requestPermission(MainActivity.this, new PermissionRequestUtil.OnPermissionRequestListener() {
                        @Override
                        public void onPermissionsGranted() {
                            uploadFile();
                        }

                        @Override
                        public void onPermissionsDenied(String... deniedPermissions) {
                            showToast("您拒绝了存储读取权限，应用无法访问您的文件");
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            }
            case R.id.tv_vp_test: {
                startActivity(new Intent(this, PagerTestActivity.class));
                break;
            }
            case R.id.tv_property_animation: {
                startActivity(new Intent(this, PropertyAnimationEntryActivity.class));
                break;
            }
            case R.id.tv_MultiGroupHistogramView: {
                startActivity(new Intent(this, MultiGroupHistogramActivity.class));
                break;
            }
            case R.id.tv_set_font: {
                startActivity(new Intent(this, SettingActivity.class));
                break;
            }
            case R.id.tv_bezier_curve: {
                startActivity(new Intent(this, BezierCurveTestActivity.class));
                break;
            }case R.id.tv_audio_animation:{
                if (audioSignalView.isAudioSignalStarted()) {
                    audioSignalView.stopAudioSignal();
                } else {
                    audioSignalView.startAudioSignal();
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("saveState", "saveState");
        super.onSaveInstanceState(outState);
    }

    private void permissionTest() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionRequestUtil.requestPermission(this, new PermissionRequestUtil.OnPermissionRequestListener() {
            @Override
            public void onPermissionsGranted() {
                showToast("权限申请成功");
            }

            @Override
            public void onPermissionsDenied(String... deniedPermissions) {
                StringBuilder sb = new StringBuilder();
                for (String permission : deniedPermissions) {
                    sb.append(permission).append("\n");
                }
                sb.deleteCharAt(sb.length() - 1);
                showToast("您拒绝了以下权限:\n" + sb.toString());
                LogUtil.e("deniedPermissions: " + sb.toString());
            }
        }, permissions);
    }
}
