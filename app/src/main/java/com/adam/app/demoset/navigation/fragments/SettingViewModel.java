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

package com.adam.app.demoset.navigation.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.adam.app.demoset.navigation.repository.SettingRepository;

public class SettingViewModel extends AndroidViewModel {
    // SettingRepository
    private final SettingRepository mRepository;

    // live date: notification enabled
    private final LiveData<Boolean> mNotificationEnabled;
    public LiveData<Boolean> getNotificationEnabled() {
        return mNotificationEnabled;
    }

    public SettingViewModel(@NonNull Application application) {
        super(application);
        // get repository
        mRepository = SettingRepository.getInstance(application);
        // publish from repository
        mNotificationEnabled = LiveDataReactiveStreams.fromPublisher(mRepository.getNotificationEnabled());
    }

    public void onNotificationChanged(boolean enabled) {
        if (mNotificationEnabled.getValue() == null) {
            return;
        }

        if (mNotificationEnabled.getValue() == enabled) {
            return;
        }

        mRepository.setNotificationEnabled(enabled);
    }

    public void clearSettings() {
        mRepository.clearAllSettings();
    }

}