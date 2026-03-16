/**
 * Copyright (C) 2026 Adam Chen Demo app project. All rights reserved.
 * <p>
 * Description: This is a jni view model
 * </p>
 *
 * Author: Adam Chen
 * Date: 2026/03/16
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
