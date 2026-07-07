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

package com.adam.app.demoset.systemUI.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.systemUI.domain.model.SystemUIStatus;
import com.adam.app.demoset.systemUI.domain.usecase.ToggleSystemUIModeUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for System UI demo.
 */
@HiltViewModel
public class SystemUIViewModel extends AndroidViewModel {

    private final ToggleSystemUIModeUseCase mToggleUseCase;

    private final MutableLiveData<SystemUIStatus> mUiStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mExitEvent = new MutableLiveData<>();

    @Inject
    public SystemUIViewModel(@NonNull Application application, ToggleSystemUIModeUseCase toggleUseCase) {
        super(application);
        this.mToggleUseCase = toggleUseCase;

        // Initial state
        mUiStatus.setValue(new SystemUIStatus(
                false, false,
                application.getString(R.string.demo_system_ui_noraml_state),
                androidx.appcompat.R.attr.colorPrimary,
                R.string.demo_system_ui_hide_low_light_btn,
                R.string.demo_system_ui_hide_invisible
        ));
    }

    /**
     * Get UI status LiveData.
     */
    public LiveData<SystemUIStatus> getUiStatus() {
        return mUiStatus;
    }

    /**
     * Get Exit event LiveData.
     */
    public LiveData<Boolean> getExitEvent() {
        return mExitEvent;
    }

    /**
     * Handle dim button click.
     */
    public void onDimClicked() {
        SystemUIStatus current = mUiStatus.getValue();
        if (current == null) return;

        SystemUIStatus next = mToggleUseCase.execute(
                current.isLowProfile(), current.isImmersive(), true, false,
                getApplication().getString(R.string.demo_system_ui_noraml_state),
                getApplication().getString(R.string.demo_system_ui_low_light_mode),
                getApplication().getString(R.string.demo_system_ui_immerse_mode)
        );
        mUiStatus.setValue(next);
    }

    /**
     * Handle hide button click.
     */
    public void onHideClicked() {
        SystemUIStatus current = mUiStatus.getValue();
        if (current == null) return;

        SystemUIStatus next = mToggleUseCase.execute(
                current.isLowProfile(), current.isImmersive(), false, true,
                getApplication().getString(R.string.demo_system_ui_noraml_state),
                getApplication().getString(R.string.demo_system_ui_low_light_mode),
                getApplication().getString(R.string.demo_system_ui_immerse_mode)
        );
        mUiStatus.setValue(next);
    }

    /**
     * Handle exit button click.
     */
    public void onExitClicked() {
        mExitEvent.setValue(true);
    }
}
