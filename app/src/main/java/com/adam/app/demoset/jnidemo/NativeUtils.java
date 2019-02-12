/**
 * Native utils
 */
package com.adam.app.demoset.jnidemo;

import com.adam.app.demoset.Utils;

public class NativeUtils {

    // Load the jni shared lib
    static {
        System.loadLibrary("demo-jni");
    }


    private NativeUtils() {
    }

    /**
     * Singleton Bill Pugh
     */
    private static class Helper {

        public static final NativeUtils INSTANCE = new NativeUtils();
    }

    public static NativeUtils newInstance() {
        return Helper.INSTANCE;
    }


    /**
     * As following information are triggered by native layer
     */
    private String mDataFromNative = "unChange";
    public static boolean sDataFromNative = false;

    private void notifyObj(String message) {
        Utils.inFo(this, "notify is called and message: " + message);
        DemoJNIAct.notifyUI(mDataFromNative, message);

    }

    private static void notifyClazz(String message) {
        Utils.inFo(NativeUtils.class, "notify is called and message: " + message);
        DemoJNIAct.notifyUI(sDataFromNative, message);

    }

    public native String sayHello();

    public native void objectCallBack();

    public native void clearObjData();

    public static native void clearClazzData();

    public static native void classCallBack();
}
