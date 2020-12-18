package com.dev.kit.basemodule.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

/**
 * @author cuiyan
 * Created on 2020/9/28.
 */
public class AppUtil {

    public static boolean isAppDebuggable(Context context, String packageName) {
        Log.e("mytag", "check app debuggable: " + packageName);
        try {
            PackageInfo pkginfo = context.getPackageManager().getPackageInfo(packageName, 1);
            if (pkginfo != null) {
                ApplicationInfo info = pkginfo.applicationInfo;
                return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            }
        } catch (Exception e) {
            Log.e("mytag", e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }
}
