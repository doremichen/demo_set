/**
 * Copyright (C) Adam Demo app Project
 * All Rights Reserved
 *
 * Description: This class is for BT Device List Adapter
 *
 * Author: Adam Chen
 * Date: 2019/12/17
 */
package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.ArrayList;

public class BTDeviceListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<BtDeviceItem> mDevices = new ArrayList<>();
    private OnItemButtonClickListener mButtonListener;
    private OnItemNameClickListener mNameListener;


    public BTDeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    /**
     * Setup BT device item list
     */
    public void setData(ArrayList<BluetoothDevice> devices) {
        mDevices.clear();
        if (devices != null) {
            for (BluetoothDevice device : devices) {
                mDevices.add(new BtDeviceItem(device));
            }
        }
        notifyDataSetChanged();

    }

    /**
     * Update connect status
     */
    public void updateConnectionState(int position, boolean isConnected) {
        if (position >= 0 && position < mDevices.size()) {
            mDevices.get(position).isConnected = isConnected;
            notifyDataSetChanged();
        }
    }

    public void setButtonListener(OnItemButtonClickListener listener) {
        mButtonListener = listener;
    }

    public void setNameListener(OnItemNameClickListener listener) {
        mNameListener = listener;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bt_scan_layout, parent, false);
            holder = new ViewHolder();
            holder.mName = convertView.findViewById(R.id.tv_bt_name);
            holder.mAddress = convertView.findViewById(R.id.tv_bt_address);
            holder.mAction = convertView.findViewById(R.id.btn_bt_pair);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // log position
        Utils.info(this, "position = " + position);

        BtDeviceItem item = mDevices.get(position);
        BluetoothDevice device = item.device;

        // set name
        holder.mName.setText(device.getName());

        // set pair info in button
        holder.mAction.setText(
                (device.getBondState() == BluetoothDevice.BOND_BONDED)
                        ? mContext.getString(R.string.demo_bt_unpair)
                        : mContext.getString(R.string.demo_bt_pair)
        );

        // Set address or connect info
        holder.mAddress.setText(
                item.isConnected
                        ? mContext.getString(R.string.demo_bt_connect)
                        : device.getAddress()
        );

        // set action button listener
        holder.mAction.setOnClickListener(v -> {
            if (mButtonListener != null) {
                mButtonListener.onClick(position);
            }
        });

        // set name listener
        holder.mName.setOnClickListener(v -> {
            if (mNameListener != null) {
                mNameListener.onClick(position);
            }
        });

        return convertView;
    }

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        TextView mName;
        TextView mAddress;
        Button mAction;
    }

    /**
     * BtDeviceItem device + connect status
     */
    private static class BtDeviceItem {
        BluetoothDevice device;
        boolean isConnected;

        BtDeviceItem(BluetoothDevice device) {
            this.device = device;
            this.isConnected = false;
        }

        /**
         * toString
         */
        @NonNull
        @Override
        public String toString() {
            return "BtDeviceItem{" +
                    "device=" + device.getName() +
                    ", isConnected=" + isConnected +
                    '}';
        }

    }

    /**
     * Callback for button click
     */
    interface OnItemButtonClickListener {
        void onClick(int position);
    }

    /**
     * Callback for name click
     */
    interface OnItemNameClickListener {
        void onClick(int position);
    }
}

