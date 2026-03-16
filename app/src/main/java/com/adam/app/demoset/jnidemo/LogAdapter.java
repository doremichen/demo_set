/**
 * Copyright (C) 2026 Adam Chen Demo app project. All rights reserved.
 * <p>
 * Description: This is a log adapter
 * </p>
 *
 * Author: Adam Chen
 * Date: 2026/03/16
 */
package com.adam.app.demoset.jnidemo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.databinding.ItemLogBinding;

public class LogAdapter extends ListAdapter<String, LogAdapter.LogViewHolder> {

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<String>() {

                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            };
    ;
    // view binding
    private ItemLogBinding mBinding;

    protected LogAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = ItemLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LogViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        String log = getItem(position);
        holder.bind(log);
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {

        private ItemLogBinding mBinding;

        public LogViewHolder(@NonNull ItemLogBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(String log) {
            mBinding.tvLog.setText(log);
        }
    }


}
