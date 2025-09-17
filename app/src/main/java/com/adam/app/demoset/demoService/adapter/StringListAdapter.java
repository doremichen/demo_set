/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is adapter of recycle list view.
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.demoService.ServiceActItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringListAdapter extends RecyclerView.Adapter<StringListAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    private final List<String> mItems;

    // item string to ServiceActItems mapping table
    private final Map<String, ServiceActItems> mItemMap = new HashMap<String, ServiceActItems>();

    /**
     * Constructor
     */
    public StringListAdapter(List<String> items) {
        mItems = items;

        // build item map
        for (int i = 0; i < mItems.size(); i++) {
            mItemMap.put(mItems.get(i), ServiceActItems.values()[i]);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_action, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = mItems.get(position);
        // set item text
        holder.mTextView.setText(item);
        // set item click listener
        holder.itemView.setOnClickListener(v -> {
            // get service action item from map
            ServiceActItems ActionItem = mItemMap.get(item);
            // check if item is valid
            if (ActionItem == null) {
                // show toast
                Utils.showToast(v.getContext(), "No such item in map: " + item);
                return;
            }

            // do service action item is clicked
            ServiceActItems itemType = ServiceActItems.getItemBy(ActionItem.getType());
            // check if item is valid
            if (itemType == null) {
                // show toast
                Utils.showToast(v.getContext(), "Item is not valid: " + item);
                return;
            }

            // execute item
            itemType.execute();
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
        }
    }
}
