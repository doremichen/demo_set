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

package com.adam.app.demoset.xmlparser;

import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoXmlParserBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.xmlparser.model.ItemData;
import com.adam.app.demoset.xmlparser.viewmodel.XmlParserViewModel;

import java.util.ArrayList;
import java.util.List;

public class DemoXmlParserAct extends AppCompatActivity {

    // view binding
    private ActivityDemoXmlParserBinding mBinding;

    // log dapter
    private LogAdapter mLogAdapter;


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "[onCreate]");
        // view binding
        mBinding = ActivityDemoXmlParserBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // init view model
        XmlParserViewModel viewModel = new ViewModelProvider(this).get(XmlParserViewModel.class);

        initLogRecycler();


        // observer data set
        viewModel.getDataSet().observe(this, this::updateData);

        // observer log
        viewModel.getLogs().observe(this, this::updateLog);


    }

    private void initLogRecycler() {
        mLogAdapter = new LogAdapter();

        // set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mBinding.rvLogs.setLayoutManager(linearLayoutManager);
        mBinding.rvLogs.setAdapter(mLogAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private void updateData(List<ItemData> dataSet) {
        // build list view
        ArrayAdapter<ItemData> arrayAdapter = new ArrayAdapter<ItemData>(this, android.R.layout.simple_list_item_1, dataSet);
        //set adapter
        mBinding.listViewData.setAdapter(arrayAdapter);

    }

    private void updateLog(List<String> logs) {
        // submit
        mLogAdapter.submitList(new ArrayList<>(logs), () -> {
            mBinding.rvLogs.scrollToPosition(mLogAdapter.getItemCount() - 1);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmlPullParserManager.newInstance().clearList();
    }
}