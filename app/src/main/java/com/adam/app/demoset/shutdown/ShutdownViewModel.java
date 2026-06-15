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

package com.adam.app.demoset.shutdown;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

import java.io.IOException;

public class ShutdownViewModel extends AndroidViewModel {

    private final MutableLiveData<String> statusMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> performIntentShutdown = new MutableLiveData<>(false);
    private final Resources resources;

    public ShutdownViewModel(@NonNull Application application) {
        super(application);
        this.resources = application.getResources();
        statusMessage.setValue(getString(R.string.power_status_label, getString(R.string.power_status_waiting)));
        checkRootStatus();
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<Boolean> getPerformIntentShutdown() {
        return performIntentShutdown;
    }

    private void checkRootStatus() {
        boolean rooted = isRooted();
        Utils.info(this, "Root status checked: " + rooted);
        String status = rooted ? getString(R.string.power_status_root_access) : getString(R.string.power_status_no_root);
        statusMessage.setValue(getString(R.string.power_status_label, status));
    }

    public void onRebootClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_reboot));
        executeCommand("reboot");
    }

    public void onShutdownClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_shutdown));
        executeCommand("reboot -p");
    }

    public void onRebootRecoveryClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_recovery));
        executeCommand("reboot recovery");
    }

    public void onRebootBootloaderClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_bootloader));
        executeCommand("reboot bootloader");
    }

    public void onSoftRebootClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_soft_reboot));
        executeCommand("setprop ctl.restart zygote");
    }

    public void onScreenOffClicked() {
        statusMessage.setValue(getString(R.string.power_msg_attempting_screen_off));
        executeCommand("input keyevent 26");
    }

    public void onIntentShutdownClicked() {
        statusMessage.setValue(getString(R.string.power_msg_requesting_intent_shutdown));
        performIntentShutdown.setValue(true);
    }

    public void onIntentShutdownHandled() {
        performIntentShutdown.setValue(false);
    }

    /**
     * Updates the status message when an error occurs during an action triggered by the Activity.
     * @param error The error message to display.
     */
    public void onActionError(String error) {
        statusMessage.setValue(getString(R.string.power_status_label, error));
    }

    private void executeCommand(final String cmd) {
        new Thread(() -> {
            try {
                Utils.info(ShutdownViewModel.this, "Executing: " + cmd);
                
                // Check root status
                boolean rooted = isRooted();
                
                // Try direct execution
                Process process = Runtime.getRuntime().exec(cmd.split(" "));
                int exitCode = process.waitFor();

                if (exitCode != 0 && rooted) {
                    // If direct execution fails and the device is rooted, try using su
                    Utils.info(ShutdownViewModel.this, "Direct execution failed, trying with su...");
                    try {
                        process = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
                        exitCode = process.waitFor();
                    } catch (IOException e) {
                        Utils.error(ShutdownViewModel.this, "su execution failed: " + e.getMessage());
                        // su binary might not exist or is not executable
                    }
                }

                if (exitCode != 0) {
                    String errorDetail = !rooted ? " (Device not rooted)" : " (Permission denied)";
                    statusMessage.postValue(getString(R.string.power_status_label, 
                            getString(R.string.power_msg_command_failed, exitCode) + errorDetail));
                } else {
                    statusMessage.postValue(getString(R.string.power_status_label, getString(R.string.power_msg_command_success)));
                }
            } catch (IOException | InterruptedException e) {
                statusMessage.postValue(getString(R.string.power_status_label, getString(R.string.power_msg_error, e.getMessage())));
            }
        }).start();
    }

    private boolean isRooted() {
        try {
            Process process = Runtime.getRuntime().exec("which su");
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private String getString(int resId) {
        return resources.getString(resId);
    }

    private String getString(int resId, Object... formatArgs) {
        return resources.getString(resId, formatArgs);
    }
}
