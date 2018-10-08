package com.adam.app.demoset.jnidemo;

public class NativeUtils {

    static {
        System.loadLibrary("demo-jni");
    }


    public static native String sayHello();
}
