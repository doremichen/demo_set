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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComponentViewModel extends ViewModel {

    // tag categories
    public final List<String> mCategories = Arrays.asList("Android", "Embedded", "Medical", "UI/UX");
    // live data: logs
    private MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());

    // data binding
    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    /**
     * onCategorySelected
     *
     * @param category  category name
     * @param isChecked checked
     */
    public void onCategorySelected(String category, boolean isChecked) {
        if (isChecked) {
            addLog("Mode switched to: " + category);
        }
    }

    /**
     * onFilterChanged
     *
     * @param filter    filter name
     * @param isChecked checked
     */
    public void onFilterChanged(String filter, boolean isChecked) {
        String status = isChecked ? "Applied" : "Removed";
        addLog("Filter [" + filter + "] " + status);
    }


    /**
     * addLog
     *
     * @param msg: log message
     */
    public void addLog(String msg) {
        List<String> currentLogs = mLogs.getValue();
        if (currentLogs == null) {
            currentLogs = new ArrayList<>();
        }
        // format current time
        String id = currentLogs.size() + ": ";
        String timeStamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
        String formattedMsg = id + "[" + timeStamp + "] " + msg;

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
