/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the demo device admin view model.
 *
 * Author: Adam Chen
 * Date: 2026/03/20
 */
package com.adam.app.demoset.lockscreen.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdminViewModel extends ViewModel {
    public static final String UNKNOWN = "Unknown";
    // live data: logs
    private final MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    // live data: strategy
    private final MutableLiveData<List<Strategy>> mStrategy = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Strategy>> getStrategy() {
        return mStrategy;
    }

    // live data: navigation
    private final MutableLiveData<String> mNavigation = new MutableLiveData<>(UNKNOWN);
    public LiveData<String> getNavigation() {
        return mNavigation;
    }

    // construct
    public DeviceAdminViewModel() {
        buildStrategy();
    }

    /**
     * add log
     */
    public void addLog(String log) {
        List<String> logs = this.mLogs.getValue();
        logs.add(log);
        this.mLogs.setValue(logs);
    }

    /**
     * done navigation
     */
    public void doneNavigation() {
        this.mNavigation.setValue(UNKNOWN);
    }

    public void buildStrategy() {
        List<Strategy> strategy = new ArrayList<>();
        for (Strategy s : Strategy.values()) {
            strategy.add(s);
        }
        this.mStrategy.setValue(strategy);
    }

    public enum Strategy {
        ENABLE_ADAMIN {

            @Override
            public int getResIdName() {
                return R.string.item_enable_admin;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
                // navigate to enable admin activity
                vm.mNavigation.setValue(this.name());
            }
        },
        DISABLE_ADMIN {

            @Override
            public int getResIdName() {
                return R.string.item_disable_admin;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
                // navigate to disable admin activity
                vm.mNavigation.setValue(this.name());
            }
        },
        LOCK_SCREEN {

            @Override
            public int getResIdName() {
                return R.string.item_lock_screen;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
                // navigate to lock screen activity
                vm.mNavigation.setValue(this.name());
            }
        },
        SECURITY_LOCK {

            @Override
            public int getResIdName() {
                return R.string.item_security_lock;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
                // navigate to security lock activity
                vm.mNavigation.setValue(this.name());
            }
        },
        EXIT {

            @Override
            public int getResIdName() {
                return R.string.label_exit;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
                // navigate to exit activity
                vm.mNavigation.setValue(this.name());
            }
        };

        public abstract int getResIdName();
        public abstract void process(DeviceAdminViewModel vm);
    }


}
