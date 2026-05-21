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
import com.adam.app.demoset.databinding.ItemFullEncryptionBinding;
import com.adam.app.demoset.encryption.database.data.model.FullDbEncryptionItem;

public class FullDbEncryptionAdapter extends ListAdapter<FullDbEncryptionItem, FullDbEncryptionAdapter.ViewHolder> {

    public FullDbEncryptionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FullDbEncryptionItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<FullDbEncryptionItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull FullDbEncryptionItem oldItem, @NonNull FullDbEncryptionItem newItem) {
            return oldItem.getId() == newItem.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull FullDbEncryptionItem oldItem, @NonNull FullDbEncryptionItem newItem) {
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
        private final ItemFullEncryptionBinding mBinding;
        ViewHolder(ItemFullEncryptionBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
        void bind(FullDbEncryptionItem item) {
            mBinding.setItem(item);
            mBinding.executePendingBindings();
        }
    }
}


