package com.dev.kit.testapp.activity;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dev.kit.basemodule.activity.BaseStateViewActivity;
import com.dev.kit.basemodule.util.LogUtil;
import com.dev.kit.testapp.R;
import com.dev.kit.testapp.animation.PropertyAnimationEntryActivity;
import com.dev.kit.testapp.dbAndProvider.StudentInfo;
import com.dev.kit.testapp.dbAndProvider.dbTest.TestDbHelper;
import com.dev.kit.testapp.dbAndProvider.providerTest.TestProvider;
import com.dev.kit.testapp.indicator.CustomIndicatorActivity;
import com.dev.kit.testapp.multiGroupHistogram.MultiGroupHistogramActivity;
import com.dev.kit.testapp.pagerTest.PagerTestActivity;
import com.dev.kit.testapp.serviceTest.TestService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MainActivity extends BaseStateViewActivity implements View.OnClickListener, ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            Log.e("mytag", "savedInstanceState: " + new Gson().toJson(savedInstanceState));
//        }
//        LogUtil.e("mytag", "isMIUI: " + isMIUI());
//        LogUtil.e("mytag", "canBackgroundStart " + canBackgroundStart(this));
        init();

//        try {
//            TypedValue value = new TypedValue();
//            getResources().openRawResource(R.mipmap.iv_ts0, value);
//            Log.e("mytag", "TypedValue000: " + new Gson().toJson(value));
//            value = new TypedValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        registerReceiver();
        getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            void ts() {
                Log.e("mytag", "9999999999999");
            }
        });
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left: {
//                finish();
                PopupWindow popupWindow = new PopupWindow(this);
                TextView textView = new TextView(this);
                textView.setText("hahahahaha");
                popupWindow.setContentView(textView);
                popupWindow.showAsDropDown(findViewById(R.id.iv_left));
                break;
            }
            case R.id.tv_net_test: {
                startActivity(new Intent(MainActivity.this, DecorationTestActivity.class));
                break;
            }
            case R.id.tv_upload_file: {
                startActivity(new Intent(this, BitmapTestActivity.class));
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
            case R.id.tv_db_test: {
                dbTest();
                break;
            }
            case R.id.tv_provider_test: {
                providerTest();
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
        Log.e("mytag", "onSaveInstanceState: " + getClass().getSimpleName());
        outState.putString("saveState", "saveState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.e("mytag", "onRestoreInstanceState: " + getClass().getSimpleName());
        super.onRestoreInstanceState(savedInstanceState);
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

    Object object;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        Log.e("mytag", "00000000000000");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(getPackageName() + ".qwe");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        registerReceiver(receiver, filter);
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

    private void getMemoryInfo() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        Log.e("mytag", "系统总内存:" + (info.totalMem / (1024 * 1024)) + "M");
        Log.e("mytag", "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
        Log.e("mytag", "系统是否处于低内存运行：" + info.lowMemory);
        Log.e("mytag", "系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M时为低内存运行");
        Log.e("mytag", "应用内存分配：" + manager.getMemoryClass());
        Log.e("mytag", "应用内存分配上限：" + manager.getLargeMemoryClass());
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        Log.e("mytag", "应用内存分配上限：" + maxMemory);
        Log.e("mytag", "当前应用总内存：" + totalMemory);
        Log.e("mytag", "当前应用空闲内存：" + freeMemory);
    }


    /**
     * 检测小米手机是否具有后台弹出界面权限
     */
    public static boolean canBackgroundStart(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (ops == null) {
            return false;
        }
        try {
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", int.class, int.class, String.class);
            Integer result = (Integer) method.invoke(ops, op, Process.myUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mytag", e.getMessage(), e);
        }
        return false;
    }

    public static boolean isMIUI() {
        String line = null;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("mytag", "version: " + line);
        return !TextUtils.isEmpty(line);
    }
}
