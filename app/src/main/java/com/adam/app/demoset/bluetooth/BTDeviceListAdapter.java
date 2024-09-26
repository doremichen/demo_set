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
    private OnItemButtonClickListener mButtonListener;
    private OnItemNameClickListener mNameListener;
    private ViewHolder mHolder;
    private boolean mConnect;

    public BTDeviceListAdapter(Context context) {

        mInflater = LayoutInflater.from(context);

    }

    public void setData(ArrayList<BluetoothDevice> devices) {
        mDevices = devices;
    }

    public void setButtonListener(OnItemButtonClickListener listener) {
        mButtonListener = listener;
    }

    public void setNameListner(OnItemNameClickListener listener) {
        mNameListener = listener;
    }

    public void updateAdressContent(boolean isConnect) {

        mConnect = isConnect;


    }

    @Override
    public int getCount() {
        return (mDevices == null) ? 0 : mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return (mDevices == null) ? null : mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (mDevices == null) ? 0 : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_bt_scan_layout, null);
            mHolder = new ViewHolder();

            // get View handler
            mHolder.mName = convertView.findViewById(R.id.tv_bt_name);
            mHolder.mAddress = convertView.findViewById(R.id.tv_bt_address);
            mHolder.mAction = convertView.findViewById(R.id.btn_bt_pair);

            convertView.setTag(mHolder);

        } else {

            mHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mDevices.get(position);

        mHolder.mName.setText(device.getName());
//        mHolder.mAddress.setText(device.getAddress());
        mHolder.mAction.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair" : "Pair");
        mHolder.mAddress.setText((mConnect == true) ? "Connect..." : device.getAddress());
        // register button click
        mHolder.mAction.setOnClickListener(new ItemButtonListener(position));
        // register name item click
        mHolder.mName.setOnClickListener(new ItemNameClickListener(position));

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

    interface OnItemNameClickListener {
        void onClick(int position);
    }

    /**
     * Name Click CallBack
     */
    private class ItemNameClickListener implements View.OnClickListener {
        private int mPosition;

        public ItemNameClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mNameListener != null) {
                mNameListener.onClick(mPosition);
            }
        }
    }

    /**
     * Button CallBack
     */
    private class ItemButtonListener implements View.OnClickListener {
        private int mPosition;

        public ItemButtonListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mButtonListener != null) {
                mButtonListener.onClick(mPosition);
            }
        }
    }
}
