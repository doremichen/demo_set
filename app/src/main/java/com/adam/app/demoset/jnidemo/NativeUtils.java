package com.adam.app.demoset.jnidemo;

import android.support.annotation.NonNull;

import com.adam.app.demoset.Utils;

public class NativeUtils {

    static {
        System.loadLibrary("demo-jni");
    }

    private INative mNativeInterface;

    private NativeUtils() {}

    public static class Helper {

        private static NativeUtils sInstance;

        public static NativeUtils getInstance() {
            if (sInstance == null) {
                sInstance = new NativeUtils();
            }
            return sInstance;
        }
    }

    public void setNativeInterface(@NonNull INative native_int) {
        mNativeInterface = native_int;
    }

    public String getData() {
        return dataFromNative;
    }

    /**
     * As following information are triggered by native layer
     */
    String dataFromNative = "unChange";

    public void notify(String message) {
        Utils.inFo(this, "notify is called and message: " + message);

        if (mNativeInterface != null) {
            mNativeInterface.onNotify(message);
        }

    }

    /**
     * For Activity
     * @return
     */
    interface INative {
        void onNotify(String str);
    }

    public native String sayHello();
}
