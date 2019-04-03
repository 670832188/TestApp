package com.dev.kit.testapp.ipcTest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import ipcDemo.business.AuthLoginInfo;
import ipcDemo.business.IAuthLoginManager;
import ipcDemo.business.IOnAuthChangeListener;

/**
 * 授权登录服务(模拟)
 * Created by cuiyan on 2019/4/2.
 */

public class AuthLoginService extends Service {

    private static final AtomicBoolean isServiceRunning = new AtomicBoolean(false);
    private AuthLoginManager authLoginManager = new AuthLoginManager();
    private RemoteCallbackList<IOnAuthChangeListener> authChangeListenerList = new RemoteCallbackList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning.set(true);
        startChangeAuth();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authLoginManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning.set(false);
    }

    private void randomChangeAuth() {
        int count = authChangeListenerList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            try {
                authChangeListenerList.getBroadcastItem(i).onAuthChanged((String) generateAuth(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        authChangeListenerList.finishBroadcast();
    }

    public class AuthLoginManager extends IAuthLoginManager.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String authLogin() throws RemoteException {
            return (String) generateAuth(true);
        }

        @Override
        public AuthLoginInfo authLogin1() throws RemoteException {
            return (AuthLoginInfo) generateAuth(false);
        }

        @Override
        public void registerAuthChangeListener(IOnAuthChangeListener onAuthChangeListener) throws RemoteException {
            authChangeListenerList.register(onAuthChangeListener);
        }

        @Override
        public void unRegisterAuthChangeListener(IOnAuthChangeListener onAuthChangeListener) throws RemoteException {
            authChangeListenerList.unregister(onAuthChangeListener);
        }
    }

    private Object generateAuth(boolean getString) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int len = 10 + Math.abs(random.nextInt() % 11);
        int currentLen = 0;
        while (currentLen < len) {
            int ascii = 48 + Math.abs(random.nextInt() % 75);
            switch (ascii) {
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96: {
                    continue;
                }
            }
            currentLen++;
            sb.append((char) ascii);
        }
        long userId = Math.abs(random.nextLong());
        String userName = "zhangsan" + (1 + random.nextInt(10000));
        if (getString) {
            return "{\"auth\":\"" + sb.toString() + "\"," + "\"userId\":" + userId + ",\"userName\":" + "\"" + userName + "\"}";
        } else {
            AuthLoginInfo info = new AuthLoginInfo();
            info.setAuth(sb.toString());
            info.setUserId(userId);
            info.setUserName(userName);
            return info;
        }
    }

    private void startChangeAuth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isServiceRunning.get()) {
                    randomChangeAuth();
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
