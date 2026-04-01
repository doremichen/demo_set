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
