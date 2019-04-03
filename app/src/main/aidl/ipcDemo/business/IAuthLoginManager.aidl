// IBookManager.aidl
package ipcDemo.business;
import ipcDemo.business.AuthLoginInfo;
import ipcDemo.business.IOnAuthChangeListener;
// Declare any non-default types here with import statements

interface IAuthLoginManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
    String authLogin();
    AuthLoginInfo authLogin1();
    void registerAuthChangeListener(IOnAuthChangeListener onAuthChangeListener);
    void unRegisterAuthChangeListener(IOnAuthChangeListener onAuthChangeListener);
}
