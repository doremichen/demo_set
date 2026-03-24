/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the material demo binding adapter.
 * </p>
 * Author: Adam Chen
 * Date: 2026/03/24
 */
package com.adam.app.demoset.material.util;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.LogAdapter;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class DemoMaterailBindingAdapter {

    @BindingAdapter("logEntries")
    public static void setLogEntries(RecyclerView recyclerView, List<String> logEntries) {
        // input validation
        if (logEntries == null) {
            return;
        }

        LogAdapter adapter = (LogAdapter) recyclerView.getAdapter();
        if (adapter == null) { // initial
            adapter = new LogAdapter();
            recyclerView.setAdapter(adapter);
            // assure layout manager
            if (recyclerView.getLayoutManager() == null) {
                LinearLayoutManager manager = new LinearLayoutManager(recyclerView.getContext());
                manager.setStackFromEnd(true);
                recyclerView.setLayoutManager(manager);
            }
        }

        // submit
        adapter.submitList(new ArrayList<>(logEntries), new CompleteCallback(recyclerView, adapter));

    }

    @BindingAdapter("onSliderChanged")
    public static void setOnSliderChanged(Slider slider, final OnSliderChangeListener listener) {
        // slider listener
        slider.addOnChangeListener((s, value, fromUser) -> {
            if (listener != null) {
                listener.onChanged(value);
            }
        });
    }

    public interface OnSliderChangeListener {
        void onChanged(float value);
    }

}
