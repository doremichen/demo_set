/**
 * Copyright (C) 2019 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is the native utils class
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2018/10/08
 */
package com.adam.app.demoset.jnidemo;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.jnidemo.legacy.DemoJNIAct;

public class NativeUtils {

    public static boolean sDataFromNative = false;

    // Load the jni shared lib
    static {
        System.loadLibrary("demo-jni");
    }

    /**
     * As following information are triggered by native layer
     */
    private String mDataFromNative = "unChange";

    private NativeUtils() {
    }

    public static NativeUtils newInstance() {
        return Helper.INSTANCE;
    }

    private static void notifyClazz(String message) {
        Utils.info(NativeUtils.class, "notify is called and message: " + message);
        DemoJNIAct.notifyUI(sDataFromNative, message);

    }

    public static native void clearClazzData();

    public static native void classCallBack();

    private void notifyObj(String message) {
        Utils.info(this, "notify is called and message: " + message);
        DemoJNIAct.notifyUI(mDataFromNative, message);

    }

    public native String sayHello();

    public native void objectCallBack();

    public native void clearObjData();

    /**
     * Singleton Bill Pugh
     */
    private static class Helper {

        public static final NativeUtils INSTANCE = new NativeUtils();
    }
}
