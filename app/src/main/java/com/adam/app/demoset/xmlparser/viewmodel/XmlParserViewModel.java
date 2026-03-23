/**
 * Copyright (C) 2020 Adam Chen Demp set project. All rights reserved.
 * <p>
 * Description: This is a xml parser view model.
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/23
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
