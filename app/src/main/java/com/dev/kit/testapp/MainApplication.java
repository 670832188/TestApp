package com.dev.kit.testapp;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.dev.kit.testapp.activity.SettingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cuiyan on 2018/5/16.
 */
public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private List<Activity> activityList;
    private float fontScale;
    private SharedPreferences preferences;
    private static MainApplication mainApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mainApplication = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontScale = getFontScale();
        registerActivityLifecycleCallbacks(this);
    }

    public static float getFontScale() {
        float fontScale = 1.0f;
        if (mainApplication != null) {
            fontScale = mainApplication.preferences.getFloat("fontScale", 1.0f);
        }
        return fontScale;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        // 禁止字体大小随系统设置变化
        Resources resources = activity.getResources();
        if (resources != null && resources.getConfiguration().fontScale != fontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        activityList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activityList != null) {
            activityList.remove(activity);
        }
    }

    public static void setAppFontSize(float fontScale) {
        if (mainApplication != null) {
            List<Activity> activityList = mainApplication.activityList;
            if (activityList != null) {
                for (Activity activity : activityList) {
                    if (activity instanceof SettingActivity) {
                        continue;
                    }
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        android.content.res.Configuration configuration = resources.getConfiguration();
                        configuration.fontScale = fontScale;
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        activity.recreate();
                        if (fontScale != mainApplication.fontScale) {
                            mainApplication.fontScale = fontScale;
                            mainApplication.preferences.edit().putFloat("fontScale", fontScale).apply();
                        }
                    }
                }
            }
        }
    }

    public static MainApplication getMainApplication() {
        return mainApplication;
    }
}
