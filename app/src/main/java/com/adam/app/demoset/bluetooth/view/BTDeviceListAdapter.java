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

package com.adam.app.demoset.bluetooth.view;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.bluetooth.model.BtDeviceItem;
import com.adam.app.demoset.databinding.ItemBtScanLayoutBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern ListAdapter for displaying Bluetooth devices using Data Binding and RecyclerView.
 */
public class BTDeviceListAdapter extends ListAdapter<BtDeviceItem, BTDeviceListAdapter.ViewHolder> {

    /**
     * DiffUtil callback for calculating item differences.
     */
    private static final DiffUtil.ItemCallback<BtDeviceItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<BtDeviceItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull BtDeviceItem oldItem, @NonNull BtDeviceItem newItem) {
            return oldItem.getDevice().getAddress().equals(newItem.getDevice().getAddress());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BtDeviceItem oldItem, @NonNull BtDeviceItem newItem) {
            return oldItem.getDevice().getBondState() == newItem.getDevice().getBondState() &&
                    oldItem.isConnected() == newItem.isConnected();
        }
    };
    private final OnItemButtonClickListener mButtonListener;
    private final OnItemNameClickListener mNameListener;

    /**
     * Constructor with listeners.
     *
     * @param buttonListener Listener for button clicks.
     * @param nameListener   Listener for name clicks.
     */
    public BTDeviceListAdapter(OnItemButtonClickListener buttonListener, OnItemNameClickListener nameListener) {
        super(DIFF_CALLBACK);
        this.mButtonListener = buttonListener;
        this.mNameListener = nameListener;
    }

    /**
     * Sets the data for the adapter.
     *
     * @param devices The list of Bluetooth devices.
     */
    public void setData(List<BluetoothDevice> devices) {
        List<BtDeviceItem> items = new ArrayList<>();
        if (devices != null) {
            for (BluetoothDevice device : devices) {
                items.add(new BtDeviceItem(device));
            }
        }
        submitList(items);
    }

    /**
     * Clears all items from the adapter.
     */
    public void clearItems() {
        submitList(null);
    }

    /**
     * Updates the connection state of a device at a specific position.
     *
     * @param position    The position of the device.
     * @param isConnected True if the device is connected, false otherwise.
     */
    public void updateConnectionState(int position, boolean isConnected) {
        if (position >= 0 && position < getItemCount()) {
            BtDeviceItem item = getItem(position);
            item.setConnected(isConnected);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBtScanLayoutBinding binding = ItemBtScanLayoutBinding.inflate(inflater, parent, false);
        // Bind listeners once during ViewHolder creation
        return new ViewHolder(binding, mButtonListener, mNameListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BtDeviceItem item = getItem(position);
        holder.bind(item);
    }

    /**
     * Callback interface for item button clicks.
     */
    public interface OnItemButtonClickListener {
        void onClick(BtDeviceItem item);
    }

    /**
     * Callback interface for item name clicks.
     */
    public interface OnItemNameClickListener {
        void onClick(BtDeviceItem item);
    }

    /**
     * ViewHolder for optimizing list performance using View Binding.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBtScanLayoutBinding mBinding;

        public ViewHolder(ItemBtScanLayoutBinding binding, OnItemButtonClickListener buttonListener,
                         OnItemNameClickListener nameListener) {
            super(binding.getRoot());
            this.mBinding = binding;
            // Bind listeners only once in the constructor
            mBinding.setButtonListener(buttonListener);
            mBinding.setNameListener(nameListener);
        }

        public void bind(BtDeviceItem item) {
            // Only update the data item
            mBinding.setItem(item);
            mBinding.executePendingBindings();
        }
    }
}
