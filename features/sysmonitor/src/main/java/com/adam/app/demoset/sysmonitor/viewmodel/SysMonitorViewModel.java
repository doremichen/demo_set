/*
 * MIT License
 *
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

package com.adam.app.demoset.sysmonitor.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.adam.app.demoset.sysmonitor.domain.model.SystemStatus;
import com.adam.app.demoset.sysmonitor.domain.usecase.GetSystemStatusUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * SysMonitorViewModel manages the UI state for the System Monitor screen.
 * It now uses UseCases to interact with the domain layer.
 */
@HiltViewModel
public class SysMonitorViewModel extends ViewModel {

    private final GetSystemStatusUseCase getSystemStatusUseCase;
    private final LiveData<SystemStatus> systemStatus;

    @Inject
    public SysMonitorViewModel(GetSystemStatusUseCase getSystemStatusUseCase) {
        this.getSystemStatusUseCase = getSystemStatusUseCase;
        this.systemStatus = this.getSystemStatusUseCase.execute();
    }

    public LiveData<SystemStatus> getSystemStatus() {
        return systemStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getSystemStatusUseCase.cleanup();
    }
}
