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

import com.adam.app.demoset.jnidemo.viewmodel.JNIViewModel;
import com.adam.app.demoset.utils.Utils;

/**
 * NativeUtils - Acts as the Infrastructure/Data Layer Bridge for JNI operations.
 * This class is the lowest level of the Clean Architecture in this module,
 * responsible for direct communication with the native shared library.
 */
public class NativeUtils {

    // Load the native shared library
    static {
        System.loadLibrary("demo-native");
    }

    // ViewModel instance for UI callbacks
    private static JNIViewModel mViewModel;

    // Fields updated by the native layer via reflection
    private String mDataFromNative = "unChange";
    public static boolean sDataFromNative = false;

    private NativeUtils() {
        // Private constructor for singleton
    }

    /**
     * Get instance of NativeUtils (Singleton)
     * @return NativeUtils instance
     */
    public static NativeUtils newInstance() {
        return Helper.INSTANCE;
    }

    /**
     * Set the ViewModel to receive callbacks from JNI
     * @param viewModel The JNIViewModel instance
     */
    public static void setViewModel(JNIViewModel viewModel) {
        mViewModel = viewModel;
    }

    /**
     * Static callback triggered by the native layer
     * @param message Message sent from native code
     */
    private static void notifyClazz(String message) {
        Utils.info(NativeUtils.class, "Native callback (Static): " + message);
        if (mViewModel != null) {
            mViewModel.updateClazzData(sDataFromNative, message);
        }
    }

    /**
     * Instance callback triggered by the native layer
     * @param message Message sent from native code
     */
    private void notifyObj(String message) {
        Utils.info(this, "Native callback (Instance): " + message);
        if (mViewModel != null) {
            mViewModel.updateObjData(mDataFromNative, message);
        }
    }

    // --- Native Method Declarations ---

    public native String sayHello();

    public native void objectCallBack();

    public native void clearObjData();

    public native int calculate(int a, int b);

    public native String getSystemInfo();

    public static native void clearClazzData();

    public static native void classCallBack();

    /**
     * Singleton helper using Bill Pugh initialization
     */
    private static class Helper {
        private static final NativeUtils INSTANCE = new NativeUtils();
    }
}
