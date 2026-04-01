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

package com.adam.app.demoset.demoService.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ItemServiceLogBinding;
import com.adam.app.demoset.demoService.model.ServiceEvent;

public class LogAdapter  extends ListAdapter<ServiceEvent, LogAdapter.ViewHolder> {

        private static final DiffUtil.ItemCallback<ServiceEvent> DIFF_CALLBACK =
                new DiffUtil.ItemCallback<ServiceEvent>() {


                    @Override
                    public boolean areItemsTheSame(@NonNull ServiceEvent oldItem,
                                                   @NonNull ServiceEvent newItem) {
                        return oldItem.getTime() == newItem.getTime();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull ServiceEvent oldItem,
                                                      @NonNull ServiceEvent newItem) {
                        return oldItem.getMessage().equals(newItem.getMessage());
                    }
                };

        public LogAdapter() {
            super(DIFF_CALLBACK);
        }


    @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // view binding
        ItemServiceLogBinding binding = ItemServiceLogBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ServiceEvent event = getItem(position);
            holder.mBinding.txtLog.setText(event.getMessage());
        }

        /**
         * View holder
         */
        static class ViewHolder extends RecyclerView.ViewHolder {

            // view binding
            private ItemServiceLogBinding mBinding;

            public ViewHolder(@NonNull ItemServiceLogBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }
        }
}

