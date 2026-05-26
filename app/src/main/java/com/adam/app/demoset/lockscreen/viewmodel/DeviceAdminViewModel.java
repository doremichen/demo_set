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

/**
 * DeviceAdminViewModel
 *
 * ViewModel that manages Device Admin actions and console logs.
 */
public class DeviceAdminViewModel extends ViewModel {
    public static final String UNKNOWN = "Unknown";
    private final MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AdminAction>> mAdminActions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> mNavigation = new MutableLiveData<>(UNKNOWN);

    public DeviceAdminViewModel() {
        buildAdminActions();
    }

    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    public LiveData<List<AdminAction>> getAdminActions() {
        return mAdminActions;
    }

    public LiveData<String> getNavigation() {
        return mNavigation;
    }

    /**
     * Add a log entry to the console.
     * @param log Log message
     */
    public void addLog(String log) {
        List<String> logs = this.mLogs.getValue();
        if (logs != null) {
            logs.add(log);
            this.mLogs.setValue(logs);
        }
    }

    /**
     * Clear the navigation event state.
     */
    public void doneNavigation() {
        this.mNavigation.setValue(UNKNOWN);
    }

    /**
     * Build the list of available admin actions from the enum.
     */
    public void buildAdminActions() {
        List<AdminAction> actions = new ArrayList<>();
        for (AdminAction a : AdminAction.values()) {
            actions.add(a);
        }
        this.mAdminActions.setValue(actions);
    }

    /**
     * AdminAction Enum
     * Defines the strategies for different Device Admin and Security tasks.
     */
    public enum AdminAction {
        ENABLE_ADMIN {
            @Override
            public int getResIdName() {
                return R.string.item_enable_admin;
            }

            @Override
            public void process(DeviceAdminViewModel vm) {
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
                vm.mNavigation.setValue(this.name());
            }
        };

        public abstract int getResIdName();
        public abstract void process(DeviceAdminViewModel vm);
    }
}
