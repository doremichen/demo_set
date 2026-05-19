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
import com.adam.app.demoset.databinding.ItemEncryptionBinding;
import com.adam.app.demoset.encryption.data.model.EncryptionItem;

public class EncryptionAdapter extends ListAdapter<EncryptionItem, EncryptionAdapter.EncryptionViewHolder> {

    public EncryptionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<EncryptionItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<EncryptionItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull EncryptionItem oldItem, @NonNull EncryptionItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EncryptionItem oldItem, @NonNull EncryptionItem newItem) {
            return oldItem.getAlias().equals(newItem.getAlias()) &&
                    oldItem.getEncryptedData().equals(newItem.getEncryptedData()) &&
                    (oldItem.getDecryptedData() != null && oldItem.getDecryptedData().equals(newItem.getDecryptedData()));
        }
    };

    @NonNull
    @Override
    public EncryptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEncryptionBinding binding = ItemEncryptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EncryptionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EncryptionViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class EncryptionViewHolder extends RecyclerView.ViewHolder {
        private final ItemEncryptionBinding binding;
        EncryptionViewHolder(ItemEncryptionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(EncryptionItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }
}
