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
