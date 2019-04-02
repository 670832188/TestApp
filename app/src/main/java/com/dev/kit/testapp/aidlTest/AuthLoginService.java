package com.dev.kit.testapp.aidlTest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.Random;

/**
 * 授权登录服务(模拟)
 * Created by cuiyan on 2019/4/2.
 */

public class AuthLoginService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AuthLoginBinder();
    }

    public class AuthLoginBinder extends IAuthLoginManager.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String authLogin() throws RemoteException {
            return generateAuth();
        }
    }

    private String generateAuth() {
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
        return "{\"auth\":\"" + sb.toString() + "\"," + "\"userId\":" + userId + ",\"userName\":" + "\"" + userName + "\"}";
    }
}
