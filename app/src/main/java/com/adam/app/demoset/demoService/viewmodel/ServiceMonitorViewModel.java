/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the service monitor view model.
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/11
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
     *
     * @param view View
     */
    public void startService(View view) {
        Context context = view.getContext();
        Intent intent = ServiceHelper.getInstance().buildServiceIntent(context);
        context.startService(intent);
        // log
        addLog("start service");
        // update ui status
        mDemoUISate.setValue(DemoUISate.START_SERVICE);
    }

    /**
     * stopService
     *
     * @param view View
     */
    public void stopService(View view) {
        Context context = view.getContext();
        Intent intent = ServiceHelper.getInstance().buildServiceIntent(context);
        context.stopService(intent);
        // log
        addLog("stop service");
        // update ui status
        mDemoUISate.setValue(DemoUISate.INIT);
    }

    /**
     * bindService
     *
     * @param view View
     */
    public void bindService(View view) {
        Activity act = (Activity) view.getContext();
        ServiceHelper.getInstance().bindService(act);
        // log
        addLog("bind service");
        // update ui status
        mDemoUISate.setValue(DemoUISate.BIND_SERVICE);
    }

    /**
     * unbindService
     *
     * @param view View
     */
    public void unbindService(View view) {
        Activity act = (Activity) view.getContext();
        ServiceHelper.getInstance().unbindService(act);
        // log
        addLog("unbind service");
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
