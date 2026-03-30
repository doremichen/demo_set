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

import com.adam.app.demoset.utils.Utils;

import com.adam.app.demoset.jnidemo.viewmodel.JNIViewModel;

public class NativeUtils {



    // jni view model
    private static JNIViewModel mViewModel;


    // Load the jni shared lib
    static {
        System.loadLibrary("demo-jni");
    }

    /**
     * As following information are triggered by native layer
     */
    private String mDataFromNative = "unChange";
    public static boolean sDataFromNative = false;

    private NativeUtils() {
    }

    public static NativeUtils newInstance() {
        return Helper.INSTANCE;
    }

    /**
     * set view model
     */
    public static void setViewModel(JNIViewModel viewModel) {
        mViewModel = viewModel;
    }

    private static void notifyClazz(String message) {
        Utils.info(NativeUtils.class, "notify is called and message: " + message);
        if (mViewModel == null) {
            throw new NullPointerException("mViewModel is null");
        }
        mViewModel.updateClazzData(sDataFromNative, message);

// legacy        DemoJNIAct.notifyUI(sDataFromNative, message);

    }

    private void notifyObj(String message) {
        Utils.info(this, "notify is called and message: " + message);
 // legacy       DemoJNIAct.notifyUI(mDataFromNative, message);
        if (mViewModel == null) {
            throw new NullPointerException("mViewModel is null");
        }
        mViewModel.updateObjData(mDataFromNative, message);
    }

    // --- native function ---
    public native String sayHello();

    public native void objectCallBack();

    public native void clearObjData();

    public static native void clearClazzData();

    public static native void classCallBack();

    /**
     * Singleton Bill Pugh
     */
    private static class Helper {

        public static final NativeUtils INSTANCE = new NativeUtils();
    }
}
