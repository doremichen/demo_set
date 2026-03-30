/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This class is the Wifi adapter.
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2025/10/07
 */
package com.adam.app.demoset.wifi2;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ApListAdapter extends RecyclerView.Adapter<ApListAdapter.ViewHolder> {

    private List<ScanResult> mList = new ArrayList<>();
    private final OnItemLongClickListener mListener;

    ApListAdapter(OnItemLongClickListener listener) {
        this.mListener = listener;
    }


    void updateList(List<ScanResult> list) {
        Utils.info(this, "updateList");
        Utils.info(this, "list: " + list.toString());
        this.mList = list;
        // update list view
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = this.mList.get(position);
        holder.mSSID.setText(result.SSID);
        holder.mBSSID.setText(result.BSSID);
        holder.mCap.setText(result.capabilities);
        holder.mFren.setText(String.valueOf(result.frequency));
        holder.mLevel.setText(String.valueOf(result.level));
        // notify ui
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ApListAdapter.this.mListener.onLongClick(result);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    interface OnItemLongClickListener {
        void onLongClick(ScanResult result);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mSSID;
        private final TextView mBSSID;
        private final TextView mCap;
        private final TextView mFren;
        private final TextView mLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mSSID = itemView.findViewById(R.id.item_label_SSID);
            mBSSID = itemView.findViewById(R.id.item_label_BSSID);
            mCap = itemView.findViewById(R.id.item_label_Cap);
            mFren = itemView.findViewById(R.id.item_label_Frequency);
            mLevel = itemView.findViewById(R.id.item_label_Level);

        }
    }

}
