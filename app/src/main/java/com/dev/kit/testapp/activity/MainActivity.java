package com.dev.kit.testapp.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.activity.VideoRecordActivity;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.netRequest.util.CommonInterceptor;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.FileUtil;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.basemodule.util.PermissionRequestUtil;
import com.dev.kit.basemodule.util.ToastUtil;
import com.dev.kit.testapp.R;
import com.dev.kit.testapp.animation.PropertyAnimationEntryActivity;
import com.dev.kit.testapp.dbAndProvider.StudentInfo;
import com.dev.kit.testapp.dbAndProvider.dbTest.TestDbHelper;
import com.dev.kit.testapp.dbAndProvider.providerTest.TestProvider;
import com.dev.kit.testapp.indicator.CustomIndicatorActivity;
import com.dev.kit.testapp.mediaSelectorTest.MediaSelectorTestActivity;
import com.dev.kit.testapp.multiGroupHistogram.MultiGroupHistogramActivity;
import com.dev.kit.testapp.pagerTest.PagerTestActivity;
import com.dev.kit.testapp.recordingAnimation.RecordingAnimationActivity;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.ApiService;
import com.dev.kit.testapp.rxJavaAndRetrofitTest.NetRequestDemoActivity;
import com.dev.kit.testapp.serviceTest.TestIntentService;
import com.dev.kit.testapp.serviceTest.TestService;
import com.dev.kit.testapp.videoRecord.RecordVideoActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseStateViewActivity implements View.OnClickListener, ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();

        String language = locale.getLanguage() + "-" + locale.getCountry();
        LogUtil.e("language: " + language);
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
        setOnClickListener(R.id.tv_indicator, this);
        setOnClickListener(R.id.tv_audio_animation, this);
        setOnClickListener(R.id.tv_media_selector, this);
        setOnClickListener(R.id.tv_video_record1, this);
        setOnClickListener(R.id.tv_video_record2, this);
        setOnClickListener(R.id.tv_db_test, this);
        setOnClickListener(R.id.tv_provider_test, this);
        setContentState(STATE_DATA_CONTENT);
        ImageView iv1 = findViewById(R.id.iv_1);
        ImageView iv2 = findViewById(R.id.iv_2);
        Bitmap bitmap0 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_delete);
        Bitmap bitmap1 = ((BitmapDrawable) iv1.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) iv2.getDrawable()).getBitmap();

        LogUtil.e("mytag", "bitmapWH: " + bitmap0.getWidth() + "*" + bitmap0.getHeight() + "---" + bitmap1.getWidth() + "*" + bitmap1.getHeight() + "---" + bitmap2.getWidth() + "*" + bitmap2.getHeight());
        LogUtil.e("mytag", "bitmapSize: " + bitmap0.getByteCount() + " " + bitmap1.getByteCount() + " " + bitmap2.getByteCount());
        LogUtil.e("mytag", "bitmapMemory: " + bitmap0.getAllocationByteCount() + " " + bitmap1.getAllocationByteCount() + " " + bitmap2.getAllocationByteCount());
        LogUtil.e("mytag", "density: " + DisplayUtil.getScaleFactor() + " " + DisplayUtil.getDensityDpi() + " " + DisplayUtil.getScreenResolution());
        float w = DisplayUtil.getScreenWidth() / DisplayUtil.getXDpi();
        float h = DisplayUtil.getScreenHeight() / DisplayUtil.getYdpi();
        LogUtil.e("mytag", "pWH: " + w + " " + h + " " + DisplayUtil.getScreenHeight());
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
//                finish();
                for (int i = 0; i < 10; i++) {
                    Intent intent = new Intent(this, TestIntentService.class);
                    startService(intent);
                }
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
            case R.id.tv_indicator: {
                startActivity(new Intent(this, CustomIndicatorActivity.class));
                break;
            }
            case R.id.tv_audio_animation: {
                startActivity(new Intent(this, RecordingAnimationActivity.class));
                break;
            }
            case R.id.tv_media_selector: {
                startActivity(new Intent(this, MediaSelectorTestActivity.class));
                break;
            }
            case R.id.tv_video_record1: {
                startVideoRecord(1);
                break;
            }
            case R.id.tv_video_record2: {
                startVideoRecord(2);
                break;
            }
            case R.id.tv_db_test: {
                dbTest();
                break;
            }
            case R.id.tv_provider_test: {
//                providerTest();
                for (int i = 0; i < 10; i++) {
                    startService(new Intent(this, TestService.class));
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

    private void startVideoRecord(final int flag) {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionRequestUtil.isPermissionGranted(this, permissions)) {
            if (flag == 1) {
                startActivityForResult(new Intent(this, RecordVideoActivity.class), 101);
            } else {
                startActivityForResult(new Intent(this, VideoRecordActivity.class), 101);
            }
            return;
        }
        PermissionRequestUtil.requestPermission(this, new PermissionRequestUtil.OnPermissionRequestListener() {
            @Override
            public void onPermissionsGranted() {
                startVideoRecord(flag);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            String videoPath = data.getStringExtra(VideoRecordActivity.RECORDED_VIDEO_PATH);
            ToastUtil.showToast(this, "视频录制完成: " + videoPath.substring(videoPath.lastIndexOf(File.separator)));
        }
    }

    private void dbTest() {
        TestDbHelper dbHelper = new TestDbHelper(this);
        Cursor cursor = dbHelper.queryAllStudent();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                StudentInfo info = new StudentInfo();
                info.setStudentNumber(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_NO)));
                info.setName(cursor.getString(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_NAME)));
                info.setGradeMathematics(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_MATH)));
                info.setGradeChinese(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_CHINESE)));
                info.setGradePhysics(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_PHYSICS)));
                info.setGradeChemistry(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_CHEMISTRY)));
                int classIndex = cursor.getColumnIndex("class");
                String studentClass = "";
                if (classIndex > -1) {
                    studentClass = cursor.getString(cursor.getColumnIndex("class"));
                }
                info.setStudentClassName(studentClass);
                if (TextUtils.isEmpty(studentClass) && dbHelper.getWritableDatabase().getVersion() >= 2) {
                    studentClass = "一班";
                    ContentValues values = new ContentValues();
                    values.put("class", studentClass);
                    dbHelper.getWritableDatabase().update(TestDbHelper.STUDENT_TABLE_NAME, values, "studentNumber=?", new String[]{String.valueOf(info.getStudentNumber())});
                }
                LogUtil.e("mytag", "studentInfo000: " + info.toString());
            }
        } else {
            List<StudentInfo> infoList = new ArrayList<>();
            Random random = new Random();
            int size = Math.abs(random.nextInt() % 20) + 1;
            int studentNo = 2018001;
            String name = "张三";
            for (int i = 0; i < size; i++) {
                StudentInfo info = new StudentInfo();
                info.setStudentNumber(studentNo);
                info.setName(name + (i + 1));
                info.setGradeMathematics(Math.abs(random.nextInt() % 101));
                info.setGradeChinese(Math.abs(random.nextInt() % 101));
                info.setGradePhysics(Math.abs(random.nextInt() % 101));
                info.setGradeChemistry(Math.abs(random.nextInt() % 101));
                infoList.add(info);
                studentNo++;
            }
            dbHelper.insertOrUpdateStudent(infoList);
        }
    }

    private void providerTest() {
        Cursor cursor = getContentResolver().query(TestProvider.getStudentInfoUri(), null, "math >= ?", new String[]{"70"}, null);
        if (cursor == null) {
            LogUtil.e("mytag", "1111111111");
            return;
        }
        while (cursor.moveToNext()) {
            StudentInfo info = new StudentInfo();
            info.setStudentNumber(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_NO)));
            info.setName(cursor.getString(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_NAME)));
            info.setGradeMathematics(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_MATH)));
            info.setGradeChinese(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_CHINESE)));
            info.setGradePhysics(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_PHYSICS)));
            info.setGradeChemistry(cursor.getInt(cursor.getColumnIndex(TestDbHelper.REPORT_COLUMN_CHEMISTRY)));
            LogUtil.e("mytag", "studentInfo111: " + info.toString());
        }
        cursor.close();
    }

    @Override
    public void onResume() {
//        bindService(new Intent(this, TestService.class), this, BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
//        unbindService(this);
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, final IBinder service) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Looper myLooper = Looper.myLooper();
                        if (myLooper != null) {
                            myLooper.quit();
                            LogUtil.e("mytag", "looper quit");
                        }
                    }
                });
                Looper.loop();
                // ToDo 上述为Handler相关验证测试代码
                TestService.MyBinder binder = (TestService.MyBinder) service;
                binder.getService().setRunning(true);
                binder.log();
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.e("mytag", "onServiceDisconnected");
    }
}
