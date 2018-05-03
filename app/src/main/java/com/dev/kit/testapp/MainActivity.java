package com.dev.kit.testapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.dev.kit.basemodule.activity.BaseActivity;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.netRequest.util.CommonInterceptor;
import com.dev.kit.basemodule.util.FileUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.RxjavaAndRetrofitTest.ApiService;
import com.dev.kit.testapp.RxjavaAndRetrofitTest.NetRequestDemoActivity;
import com.dev.kit.testapp.animation.PropertyAnimationEntryActivity;
import com.dev.kit.testapp.pagerTest.PagerTestActivity;

import java.io.File;
import java.util.Random;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        setOnClickListener(R.id.tv_net_test, this);
        setOnClickListener(R.id.tv_upload_file, this);
        setOnClickListener(R.id.tv_vp_test, this);
        setOnClickListener(R.id.tv_property_animation, this);
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
            case R.id.tv_net_test: {
                startActivity(new Intent(MainActivity.this, NetRequestDemoActivity.class));
                break;
            }
            case R.id.tv_upload_file: {
                if (checkPermission()) {
                    uploadFile();
                } else {
                    requestPermission();
                }
                break;
            }
            case R.id.tv_vp_test: {
                startActivity(new Intent(MainActivity.this, PagerTestActivity.class));
                break;
            }
            case R.id.tv_property_animation: {
                startActivity(new Intent(MainActivity.this, PropertyAnimationEntryActivity.class));
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12360 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadFile();
        }
    }

    private boolean checkPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissionCheck1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12306);
    }
}
