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

package com.adam.app.demoset.jnidemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.jnidemo.NativeUtils;

import java.util.ArrayList;
import java.util.List;

public class JNIViewModel extends ViewModel {
    // live date: logs
    private MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    // live data: object state
    private MutableLiveData<String> mObjData = new MutableLiveData<>("mDataFromNative : unChange");
    public LiveData<String> getObjData() {
        return mObjData;
    }

    // live data: class state
    private MutableLiveData<String> mClazzData = new MutableLiveData<>("sDataFromNative : false");
    public LiveData<String> getClazzData() {
        return mClazzData;
    }

    // NativeUtils
    private NativeUtils mNativeUtils = NativeUtils.newInstance();

    /**
     * addLog
     * @param msg String
     */
    public void addLog(String msg) {
        List<String> logs = mLogs.getValue();
        logs.add(msg);
        mLogs.setValue(logs);
    }

    // --- jni callback update ---
    public void updateObjData(String data, String message) {
        mObjData.setValue("mDataFromNative : " + data);
        addLog(message);
    }

    public void updateClazzData(boolean data, String message) {
        mClazzData.setValue("sDataFromNative : " + data);
        addLog(message);
    }


    // --- Buttons  action ---
    public void objectCallback() {
        addLog("objectCallback is called");
        mNativeUtils.objectCallBack();
    }

    public void classCallback() {
        addLog("classCallback is called");
        NativeUtils.classCallBack();
    }

    public void clearObject() {
        addLog("clearObject is called");
        mNativeUtils.clearObjData();
    }

    public void clearClass() {
        addLog("clearClass is called");
        NativeUtils.clearClazzData();
    }

}
