package com.dev.kit.testapp.ipcTest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;

/**
 * Created by cuiyan on 2019/4/2.
 */

public class MessengerService extends Service {

    private static final int MSG_FROM_CLIENT = 1;
    private Messenger messenger = new Messenger(new MessageHandler());
    @Override
    public IBinder onBind(Intent intent) {
        new Messenger(new IBinder() {
            @Override
            public String getInterfaceDescriptor() throws RemoteException {
                return null;
            }

            @Override
            public boolean pingBinder() {
                return false;
            }

            @Override
            public boolean isBinderAlive() {
                return false;
            }

            @Override
            public IInterface queryLocalInterface(String descriptor) {
                return null;
            }

            @Override
            public void dump(FileDescriptor fd, String[] args) throws RemoteException {

            }

            @Override
            public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {

            }

            @Override
            public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                return false;
            }

            @Override
            public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {

            }

            @Override
            public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
                return false;
            }
        });
        return messenger.getBinder();
    }
    private static class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_CLIENT:{
                    Bundle bundle = msg.getData();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

}
