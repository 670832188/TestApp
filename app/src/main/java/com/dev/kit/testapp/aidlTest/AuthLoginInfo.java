package com.dev.kit.testapp.aidlTest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cuiyan on 2019/4/2.
 */

public class AuthLoginInfo implements Parcelable {

    private String token;
    private String userId;
    private String userName;

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
         dest.writeString(token);
         dest.writeString(userId);
         dest.writeString(userName);
    }

    public static Creator<AuthLoginInfo> CREATOR = new Creator<AuthLoginInfo>() {
        @Override
        public AuthLoginInfo createFromParcel(Parcel source) {
            AuthLoginInfo info = new AuthLoginInfo();
            info.token = source.readString();
            info.userId = source.readString();
            info.userName = source.readString();
            return null;
        }

        @Override
        public AuthLoginInfo[] newArray(int size) {
            return new AuthLoginInfo[size];
        }
    };
}
