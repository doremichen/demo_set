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

package com.adam.app.demoset.material.util;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.utils.LogAdapter;
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
