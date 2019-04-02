package ipcDemo.business;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cuiyan on 2019/4/2.
 */

public class AuthLoginInfo implements Parcelable {

    private String auth;
    private long userId;
    private String userName;

    public String getAuth() {
        return auth;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setUserId(long userId) {
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
        dest.writeString(auth);
        dest.writeLong(userId);
        dest.writeString(userName);
    }

    public static Creator<AuthLoginInfo> CREATOR = new Creator<AuthLoginInfo>() {
        @Override
        public AuthLoginInfo createFromParcel(Parcel source) {
            AuthLoginInfo info = new AuthLoginInfo();
            info.auth = source.readString();
            info.userId = source.readLong();
            info.userName = source.readString();
            return null;
        }

        @Override
        public AuthLoginInfo[] newArray(int size) {
            return new AuthLoginInfo[size];
        }
    };
}
