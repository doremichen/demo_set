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

package com.adam.app.demoset.demoService.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.adam.app.demoset.demoService.model.ServiceEvent;
import com.adam.app.demoset.demoService.service.LocalService;
import com.adam.app.demoset.demoService.service.RemoteService;
import com.adam.app.demoset.demoService.service.ServiceHelper;

import java.util.ArrayList;
import java.util.List;

public class ServiceMonitorViewModel extends AndroidViewModel {

    // live date: DemoUISate
    private final MutableLiveData<DemoUISate> mDemoUISate = new MutableLiveData<>(DemoUISate.INIT);
    // live data: service status
    private final MutableLiveData<List<ServiceEvent>> mLogs =
            new MutableLiveData<>(new ArrayList<>());
    // live data: service mode
    private final MutableLiveData<Boolean> mRemoteMode = new MutableLiveData<>(false);

    // live data: bind request
    private final MutableLiveData<Boolean> mBindRequest = new MutableLiveData<>(false);
    // live data: start service
    private final MutableLiveData<Boolean> mStartService = new MutableLiveData<>(false);


    public ServiceMonitorViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<DemoUISate> getDemoUISate() {
        return mDemoUISate;
    }

    public LiveData<Boolean> getRemoteMode() {
        return mRemoteMode;
    }

    public LiveData<List<ServiceEvent>> getLogs() {
        return mLogs;
    }

    public LiveData<Boolean> getBindRequest() {
        return mBindRequest;
    }

    public LiveData<Boolean> getStartService() {
        return mStartService;
    }


    /**
     * addLog
     *
     * @param event String
     */
    public void addLog(String event) {
        List<ServiceEvent> logs = mLogs.getValue();
        logs.add(new ServiceEvent(event));
        // update
        mLogs.setValue(logs);
    }

    /**
     * startService
     */
    public void startService() {
        // update start service request
        mStartService.setValue(true);
        
        // update ui status
        mDemoUISate.setValue(DemoUISate.START_SERVICE);
    }

    /**
     * stopService
     *
     */
    public void stopService() {
        // update stop service request
        mStartService.setValue(false);

        // update ui status
        mDemoUISate.setValue(DemoUISate.INIT);
    }

    /**
     * bindService
     *
     */
    public void bindService() {
        // update bind service request
        mBindRequest.setValue(true);

        // update ui status
        mDemoUISate.setValue(DemoUISate.BIND_SERVICE);
    }

    /**
     * unbindService
     *
     */
    public void unbindService() {
        // update bind service request
        mBindRequest.setValue(false);

        // update ui status
        mDemoUISate.setValue(DemoUISate.INIT);
    }

    /**
     * sendRequest
     */
    public void sendRequest() {
        ServiceHelper helper = ServiceHelper.getInstance();
        // check whether service is bound
        if (!helper.isBound()) {
            // log
            addLog("service is not bound");
            return;
        }

        // remote service mode
        if (helper.isRemoteServiceMode()) {
            try {
                // mesage
                Message msg = Message.obtain(null, RemoteService.ACTION_ONE);
                // send
                helper.getRemoteMessenger().send(msg);
                // log
                addLog("send request");
            } catch (Exception e) {
                // log
                addLog("send request failed");
            }
        } else {
            LocalService service = helper.getLocalService();
            if (service == null) {
                // log
                addLog("local service is null");
                return;
            }
            service.action1();
            // log
            addLog("send request");
        }
    }

    /**
     * switchServiceMode
     *
     * @param isRemote boolean
     */
    public void switchServiceMode(boolean isRemote) {

        ServiceHelper.getInstance().setRemoteServiceMode(isRemote);

        mRemoteMode.setValue(isRemote);

        addLog(isRemote
                ? "Switch to Remote Service"
                : "Switch to Local Service");
    }

    /**
     * getStartEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getStartEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.INIT);
    }

    /**
     * getBindEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getBindEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.INIT);

    }

    /**
     * getSwitchEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getSwitchEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.INIT);
    }

    /**
     * getStopEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getStopEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.START_SERVICE);
    }

    /**
     * getUnbindEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getUnbindEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.BIND_SERVICE);
    }


    /**
     * getSendEnabled
     *
     * @return LiveData<Boolean>
     */
    public LiveData<Boolean> getSendEnabled() {
        return Transformations.map(mDemoUISate,
                state -> state == DemoUISate.BIND_SERVICE);
    }

    /**
     * Demo UI State
     */
    public enum DemoUISate {
        INIT,
        START_SERVICE,
        BIND_SERVICE
    }

}
