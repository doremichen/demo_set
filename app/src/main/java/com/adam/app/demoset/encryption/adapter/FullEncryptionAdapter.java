/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.adam.app.demoset.databinding.ItemFullEncryptionBinding;
import com.adam.app.demoset.encryption.data.model.FullEncryptionItem;

public class FullEncryptionAdapter extends ListAdapter<FullEncryptionItem, FullEncryptionAdapter.ViewHolder> {

    public FullEncryptionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FullEncryptionItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FullEncryptionItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FullEncryptionItem oldItem, @NonNull FullEncryptionItem newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull FullEncryptionItem oldItem, @NonNull FullEncryptionItem newItem) {
            return oldItem.getAlias().equals(newItem.getAlias()) && oldItem.getSecretInfo().equals(newItem.getSecretInfo());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFullEncryptionBinding binding = ItemFullEncryptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFullEncryptionBinding binding;
        ViewHolder(ItemFullEncryptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(FullEncryptionItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }
}
