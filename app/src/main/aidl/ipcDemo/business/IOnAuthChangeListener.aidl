// IOnAuthChangeListener.aidl
package ipcDemo.business;

// Declare any non-default types here with import statements

interface IOnAuthChangeListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void onAuthChanged(String newAuth);
}
