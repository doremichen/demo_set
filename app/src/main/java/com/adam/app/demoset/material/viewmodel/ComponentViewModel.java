/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the component view model.
 * </p>
 * Author: Adam Chen
 * Date: 2026/03/24
 */
package com.adam.app.demoset.material.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComponentViewModel extends ViewModel {

    // live data: logs
    private MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    // data binding
    public LiveData<List<String>> getLogs() {
        return mLogs;
    }


    /**
     * addLog
     * @param msg: log message
     */
    public void addLog(String msg) {
        List<String> currentLogs = mLogs.getValue();
        if (currentLogs == null) {
            currentLogs = new ArrayList<>();
        }
        // format current time
        String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        String formattedMsg = "[" + timeStamp + "] " + msg;

        // add first
        currentLogs.add(0, formattedMsg);

        // constraint the size of log: 50
        while (currentLogs.size() > 50) {
            currentLogs.remove(currentLogs.size() - 1);
        }

        mLogs.setValue(new ArrayList<>(currentLogs));
    }

    /**
     * clear log
     */
    public void clearLog() {
        mLogs.setValue(new ArrayList<>());
    }

}
