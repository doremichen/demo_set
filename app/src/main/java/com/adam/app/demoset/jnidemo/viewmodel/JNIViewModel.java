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

import com.adam.app.demoset.jnidemo.domain.usecase.JniUseCase;

import java.util.ArrayList;
import java.util.List;

/**
 * JNIViewModel - Refactored to follow GRASP Principles (Creator & Information Expert).
 * 
 * The ViewModel is now completely decoupled from Repository implementations.
 * It interacts solely with the JniUseCase enum, which manages its own dependencies.
 */
public class JNIViewModel extends ViewModel {
    
    // LiveData for logging messages to the UI
    private final MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    // LiveData for instance-based data from Native
    private final MutableLiveData<String> mObjData = new MutableLiveData<>("mDataFromNative : unChange");
    public LiveData<String> getObjData() {
        return mObjData;
    }

    // LiveData for class-based (static) data from Native
    private final MutableLiveData<String> mClazzData = new MutableLiveData<>("sDataFromNative : false");
    public LiveData<String> getClazzData() {
        return mClazzData;
    }

    public JNIViewModel() {
        // No repository initialization needed here.
        // JniUseCase handles its own Information Expert / Creator responsibilities.
    }

    /**
     * Appends a message to the UI log list.
     * @param msg The message to log.
     */
    public void addLog(String msg) {
        List<String> logs = mLogs.getValue();
        if (logs != null) {
            logs.add(msg);
            mLogs.setValue(logs);
        }
    }

    // --- Native Callback Methods ---

    /**
     * Updates instance data when notified by Native layer.
     */
    public void updateObjData(String data, String message) {
        mObjData.setValue("mDataFromNative : " + data);
        addLog(message);
    }

    /**
     * Updates class data when notified by Native layer.
     */
    public void updateClazzData(boolean data, String message) {
        mClazzData.setValue("sDataFromNative : " + data);
        addLog(message);
    }

    // --- UI Action Methods (Using Unified JniUseCase Enum) ---
    
    public void sayHello() {
        String result = (String) JniUseCase.GET_HELLO.execute();
        addLog("Native Response: " + result);
    }

    public void objectCallback() {
        addLog("Action: Triggering instance-level callback...");
        JniUseCase.TRIGGER_OBJECT_CALLBACK.execute();
    }

    public void classCallback() {
        addLog("Action: Triggering class-level callback...");
        JniUseCase.TRIGGER_CLASS_CALLBACK.execute();
    }

    public void clearObject() {
        addLog("Action: Resetting instance data...");
        JniUseCase.CLEAR_OBJECT.execute();
    }

    public void clearClass() {
        addLog("Action: Resetting class data...");
        JniUseCase.CLEAR_CLASS.execute();
    }
    
    public void performCalculation(int a, int b) {
        int result = (int) JniUseCase.PERFORM_CALCULATION.execute(a, b);
        addLog("JNI Calculation: " + a + " + " + b + " = " + result);
    }
    
    public void fetchSystemInfo() {
        String info = (String) JniUseCase.GET_SYSTEM_INFO.execute();
        addLog("System Architecture: " + info);
    }
}
