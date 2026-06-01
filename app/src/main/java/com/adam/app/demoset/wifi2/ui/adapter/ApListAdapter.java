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

package com.adam.app.demoset.wifi2.ui.adapter;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.databinding.ItemWifiResultBinding;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ApListAdapter extends RecyclerView.Adapter<ApListAdapter.ViewHolder> {

    private List<ScanResult> mList = new ArrayList<>();
    private String mConnectedSsid;
    private final OnItemLongClickListener mListener;

    public ApListAdapter(OnItemLongClickListener listener) {
        this.mListener = listener;
    }


    public void updateList(List<ScanResult> list) {
        Utils.info(this, "updateList");
        if (list != null) {
            Utils.info(this, "list: " + list);
            this.mList = list;
            // update list view
            notifyDataSetChanged();
        }
    }

    public void updateConnectedSsid(String ssid) {
        this.mConnectedSsid = ssid;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWifiResultBinding binding = ItemWifiResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = this.mList.get(position);
        holder.bind(result, mConnectedSsid, mListener);
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public interface OnItemLongClickListener {
        void onLongClick(ScanResult result);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemWifiResultBinding binding;

        public ViewHolder(@NonNull ItemWifiResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ScanResult result, String connectedSsid, OnItemLongClickListener listener) {
            binding.setScanResult(result);
            binding.setIsConnected(result.SSID != null && result.SSID.equals(connectedSsid));
            binding.getRoot().setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onLongClick(result);
                }
                return true;
            });
            binding.executePendingBindings();
        }
    }

}
