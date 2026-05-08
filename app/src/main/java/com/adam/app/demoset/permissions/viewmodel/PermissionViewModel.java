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

package com.adam.app.demoset.permissions.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;

/**
 * ViewModel for Permission Demo.
 * Handles the logic of checking and managing permission states.
 */
public class PermissionViewModel extends AndroidViewModel {

    /**
     * Interface to delegate system-level permission requests back to the Activity.
     */
    public interface PermissionRequester {
        void onLaunchSinglePermission(String permission);
        void onLaunchMultiplePermissions(String[] permissions);
        void onHandleDenied(String permission);
    }

    private final MutableLiveData<String> mPermissionStatus = new MutableLiveData<>("");
    private final Context mContext;
    private PermissionRequester mRequester;

    public PermissionViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
    }

    public void setRequester(PermissionRequester requester) {
        this.mRequester = requester;
    }

    public LiveData<String> getPermissionStatus() {
        return mPermissionStatus;
    }

    /**
     * Updates the permission status message.
     */
    public void updateStatus(String status) {
        mPermissionStatus.setValue(status);
    }

    /**
     * Logic for requesting camera permission.
     */
    public void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            updateStatus(mContext.getString(R.string.msg_permission_granted, Manifest.permission.CAMERA));
            return;
        }

        if (mRequester != null) {
            mRequester.onLaunchSinglePermission(Manifest.permission.CAMERA);
        }
    }

    /**
     * Logic for requesting multiple permissions.
     */
    public void requestMultiplePermissions() {
        if (mRequester != null) {
            mRequester.onLaunchMultiplePermissions(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            });
        }
    }

    /**
     * Logic for checking current permission status.
     */
    public void checkPermissionStatus() {
        boolean camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean audio = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        
        String status = "CAMERA: " + (camera ? "GRANTED" : "DENIED") + "\n" +
                        "RECORD_AUDIO: " + (audio ? "GRANTED" : "DENIED");
        updateStatus(status);
    }

    /**
     * Handles the result when a permission is denied.
     */
    public void handlePermissionResult(String permission, boolean isGranted) {
        if (isGranted) {
            updateStatus(mContext.getString(R.string.msg_permission_granted, permission));
        } else {
            if (mRequester != null) {
                mRequester.onHandleDenied(permission);
            }
        }
    }
}
