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

package com.adam.app.demoset.binder.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.binder.service.BinderType;

import java.util.ArrayList;
import java.util.List;

public class BinderViewModel extends ViewModel implements BinderType.Callback {
    private final MutableLiveData<String> mResultC = new MutableLiveData<>("");
    private final MutableLiveData<List<String>> mOperationLogs = new MutableLiveData<>(new ArrayList<>());
    // live data: input A, input B, result C, operation logs
    public MutableLiveData<String> mInputA = new MutableLiveData<>("");
    public MutableLiveData<String> mInputB = new MutableLiveData<>("");
    // used to control which binder type: aidl/messenger
    private BinderType mBinderType = BinderType.AIDL;

    public LiveData<String> getResultC() {
        return mResultC;
    }

    public LiveData<List<String>> getOperationLogs() {
        return mOperationLogs;
    }


    public BinderViewModel() {
        // log
        addLog("BinderViewModel enter");
        // Set Binder type
        setBinderType(BinderType.AIDL);
    }


    public void setBinderType(BinderType type) {
        mBinderType = type;
        // setup callback
        mBinderType.setCallback(this);
    }

    // add log
    private void addLog(String log) {
        List<String> logs = mOperationLogs.getValue();
        logs.add(log);
        mOperationLogs.setValue(logs);
    }

    public void onAIDLCall() {
        // add log
        addLog("onAIDLCall enter");
        setBinderType(BinderType.AIDL);
    }

    public void onMessengerCall() {
        // add log
        addLog("onMessengerCall enter");
        setBinderType(BinderType.MESSENGER);
    }

    public void onExecuteCall() {
        // add log
        addLog("onExecuteCall enter");
        try {
            // get input
            int a = Integer.parseInt(mInputA.getValue().isEmpty() ? "0" : mInputA.getValue());
            int b = Integer.parseInt(mInputB.getValue().isEmpty() ? "0" : mInputB.getValue());

            // execute binder
            mBinderType.execute(a, b);
        } catch (Exception e) {
            // add log
            addLog("onExecuteCall error: " + e.getMessage());
            Utils.info(this, "onExecuteCall error");
        }


    }

    @Override
    public void result(int c) {
        // add log
        addLog("result enter");
        mResultC.setValue(String.valueOf(c));
    }

    @Override
    public void showLog(String msg) {
        // add log
        addLog(msg);
        Utils.info(this, msg);
    }
}
