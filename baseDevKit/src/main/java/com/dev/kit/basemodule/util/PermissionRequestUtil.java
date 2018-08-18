package com.dev.kit.basemodule.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请工具类
 * 需要申请权限的Activity(您可以在BaseActivity中编写统一处理代码)通过调用该工具类的requestPermission方法辅助申请权限，
 * Activity需要重写onRequestPermissionsResult方法，获取结果后回调该工具类的onRequestPermissionsResult方法完成权限申请处理
 *
 * @see #requestPermission(Activity, OnPermissionRequestListener, String...)
 * @see #onRequestPermissionsResult(int, String[], int[])
 * @see Activity#onRequestPermissionsResult(int, String[], int[])
 * Created by cuiyan on 2018/6/21.
 */
public class PermissionRequestUtil {

    private static int autoIncrementingRequestCode = 0;
    private static SparseArray<OnPermissionRequestListener> permissionListenerSparseArray = new SparseArray<>();

    /**
     * 权限申请辅助方法
     *
     * @param activity           需要申请权限的Activity
     * @param desiredPermissions 需要申请的权限
     */
    public static synchronized void requestPermission(@NonNull Activity activity, @NonNull OnPermissionRequestListener listener, @NonNull String... desiredPermissions) {
        String[] neededPermissions = filterPermissions(activity, desiredPermissions);
        if (neededPermissions.length == 0) {
            listener.onPermissionsGranted();
        } else {
            permissionListenerSparseArray.put(autoIncrementingRequestCode, listener);
            ActivityCompat.requestPermissions(activity, neededPermissions, autoIncrementingRequestCode);
            autoIncrementingRequestCode++;
        }
    }

    /**
     * 过滤已授权的权限
     *
     * @param context            上下文
     * @param desiredPermissions 需要申请的权限
     * @return 返回未获得授权的权限
     */
    public static synchronized String[] filterPermissions(@NonNull Context context, @NonNull String... desiredPermissions) {
        List<String> neededPermissions = new ArrayList<>();
        for (String desiredPermission : desiredPermissions) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(context, desiredPermission)) {
                neededPermissions.add(desiredPermission);
            }
        }
        return neededPermissions.toArray(new String[neededPermissions.size()]);
    }

    /**
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     * 在重写的上述方法中回调该方法，完成逻辑处理
     */
    public static synchronized void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        OnPermissionRequestListener listener = permissionListenerSparseArray.get(requestCode);
        if (listener != null) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.size() == 0) {
                listener.onPermissionsGranted();
            } else {
                listener.onPermissionsDenied(deniedPermissions.toArray(new String[deniedPermissions.size()]));
            }
            permissionListenerSparseArray.delete(requestCode);
        }
    }

    public static synchronized boolean isPermissionGranted(Context context, String permission) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, permission);
    }

    public static synchronized boolean isPermissionGranted(Context context, String... permission) {
        return filterPermissions(context, permission).length <= 0;
    }

    public interface OnPermissionRequestListener {
        void onPermissionsGranted();

        void onPermissionsDenied(String... deniedPermissions);
    }
}
