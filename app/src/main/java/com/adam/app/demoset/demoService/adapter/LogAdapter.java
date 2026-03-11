/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the log adapter.
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/11
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

