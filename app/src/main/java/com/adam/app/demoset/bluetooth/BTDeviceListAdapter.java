package com.adam.app.demoset.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.adam.app.demoset.R;

import java.util.ArrayList;

public class BTDeviceListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private OnItemButtonClickListener mListener;

    public BTDeviceListAdapter(Context context) {

        mInflater = LayoutInflater.from(context);

    }

    public void setData(ArrayList<BluetoothDevice> devices) {
        mDevices = devices;
    }

    public void setButtonListener (OnItemButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return (mDevices == null)? 0 : mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return (mDevices == null)? null : mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mDevices == null)? 0: position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bt_scan_layout, null);
            holder = new ViewHolder();

            // get View handler
            holder.mName = (TextView)convertView.findViewById(R.id.tv_bt_name);
            holder.mAddress = (TextView)convertView.findViewById(R.id.tv_bt_address);
            holder.mAction = (Button)convertView.findViewById(R.id.btn_bt_pair);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mDevices.get(position);

        holder.mName.setText(device.getName());
        holder.mAddress.setText(device.getAddress());
        holder.mAction.setText((device.getBondState() == BluetoothDevice.BOND_BONDED)? "Unpair": "Pair");
        // register button click
        holder.mAction.setOnClickListener(new ItemButtonListener(position));


        return convertView;
    }


    private static class ViewHolder {
        public TextView mName;
        public TextView mAddress;
        public Button mAction;
    }

    interface OnItemButtonClickListener {
        void onClick(int position);
    }

    /**
     * Button CallBack
     */
    private class ItemButtonListener implements View.OnClickListener{
        private int mPosition;

        public ItemButtonListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(mPosition);
            }
        }
    }
}
