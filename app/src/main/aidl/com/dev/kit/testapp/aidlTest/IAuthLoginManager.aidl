// IBookManager.aidl
package com.dev.kit.testapp.aidlTest;
import com.dev.kit.testapp.aidlTest.AuthLoginInfo;
// Declare any non-default types here with import statements

interface IAuthLoginManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    String authLogin();
}
