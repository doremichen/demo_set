/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
