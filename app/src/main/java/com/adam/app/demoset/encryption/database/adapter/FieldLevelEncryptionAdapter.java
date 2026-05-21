/*
 * Copyright (c) 2024 Adam Chen
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
package com.adam.app.demoset.encryption.database.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.adam.app.demoset.databinding.ItemEncryptionBinding;
import com.adam.app.demoset.encryption.database.data.model.FieldLevelEncryptionItem;

public class FieldLevelEncryptionAdapter extends ListAdapter<FieldLevelEncryptionItem, FieldLevelEncryptionAdapter.EncryptionViewHolder> {

    public FieldLevelEncryptionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FieldLevelEncryptionItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FieldLevelEncryptionItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FieldLevelEncryptionItem oldItem, @NonNull FieldLevelEncryptionItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FieldLevelEncryptionItem oldItem, @NonNull FieldLevelEncryptionItem newItem) {
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
        void bind(FieldLevelEncryptionItem item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }
}


