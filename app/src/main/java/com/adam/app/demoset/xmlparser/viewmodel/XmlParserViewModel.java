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

package com.adam.app.demoset.xmlparser.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.xmlparser.XmlPullParserManager;
import com.adam.app.demoset.xmlparser.model.ItemData;

import java.util.ArrayList;
import java.util.List;

public class XmlParserViewModel extends AndroidViewModel implements XmlPullParserManager.XmlPullParserListener {

    // live date: logs
    private MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());

    // live data: data set from xml file
    private MutableLiveData<List<ItemData>> mDataSet = new MutableLiveData<>(new ArrayList<>());

    public XmlParserViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        // init xml parser
        XmlPullParserManager.newInstance().init(context);
        // set listener
        XmlPullParserManager.newInstance().setListener(this);
        // parse xml file
        List<ItemData> dataSet = XmlPullParserManager.newInstance().parse();
        // update data set
        mDataSet.setValue(dataSet);
    }

    public LiveData<List<ItemData>> getDataSet() {
        return mDataSet;
    }

    public LiveData<List<String>> getLogs() {
        return mLogs;
    }

    public void addLog(String msg) {
        List<String> logs = mLogs.getValue();
        logs.add(msg);
        mLogs.setValue(logs);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // remove listener
        XmlPullParserManager.newInstance().setListener(null);

        // clear list
        XmlPullParserManager.newInstance().clearList();

    }

    @Override
    public void onMessage(String msg) {
        addLog(msg);
    }
}
