package com.dev.kit.basemodule.util;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by cuiyan on 16/6/1 16:50
 */
public class LogUtil {
    private static boolean logOn = true;

    public static void e(String log) {
        if (logOn) {
            Log.e(getTag(), log);
        }
    }

    public static void e(String tag, String log) {
        if (logOn) {
            Log.e(tag, log);
        }
    }

    public static void e(Throwable e) {
        if (logOn) {
            Log.e(getTag(), e.getMessage(), e);
        }
    }

    public static void e(String msg, Throwable e) {
        if (logOn) {
            Log.e(getTag(), msg, e);
        }
    }

    public static void w(String log) {
        if (logOn) {
            Log.w(getTag(), log);
        }
    }

    public static void w(String tag, String log) {
        if (logOn) {
            Log.w(tag, log);
        }
    }

    public static void w(Throwable e) {
        if (logOn) {
            Log.w(getTag(), e.getMessage(), e);
        }
    }

    public static void i(String log) {
        if (logOn) {
            Log.i(getTag(), log);
        }
    }

    public static void i(String tag, String log) {
        if (logOn) {
            Log.i(tag, log);
        }
    }

    public static void i(Throwable e) {
        if (logOn) {
            Log.i(getTag(), e.getMessage(), e);
        }
    }

    public static void d(String log) {
        if (logOn) {
            Log.d(getTag(), log);
        }
    }

    public static void d(String tag, String log) {
        if (logOn) {
            Log.d(tag, log);
        }
    }

    public static void d(Throwable e) {
        if (logOn) {
            Log.d(getTag(), e.getMessage(), e);
        }
    }

    public static void v(String log) {
        if (logOn) {
            Log.v(getTag(), log);
        }
    }

    public static void v(String tag, String log) {
        if (logOn) {
            Log.v(tag, log);
        }
    }

    public static void v(Throwable e) {
        if (logOn) {
            Log.v(getTag(), e.getMessage(), e);
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "miFamily" + File.separator + "errorLog.txt";

    public static synchronized void writeException2File(Throwable ex) {// 新建或打开日志文件
        Date nowTime = new Date();
        File file = new File(LOG_FILE_PATH);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.println();
            pw.println(getTag() + "  " + sdf.format(nowTime));
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeException2File(String logText) {// 新建或打开日志文件
        Date nowTime = new Date();
        File file = new File(LOG_FILE_PATH);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.println();
            pw.println(getTag() + "-->" + sdf.format(nowTime));
            pw.println(logText);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTag() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null) {
            return "LogUtil";
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(LogUtil.class.getName())) {
                continue;
            }

            return "[" + st.getFileName() + ":" + st.getLineNumber() + "]";
        }

        return "LogUtil";
    }
}
