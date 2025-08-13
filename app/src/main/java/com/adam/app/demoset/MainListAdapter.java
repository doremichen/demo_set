/**
 * Main list view adapter
 * <p>
 * info: This class is the main list view adapter.
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */
package com.adam.app.demoset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import java.util.List;


public class MainListAdapter extends BaseAdapter {

    private final List<ItemContent> mDataList;
    private final LayoutInflater mInflater;

    public MainListAdapter(@NonNull Context context, @NonNull List<ItemContent> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public ItemContent getItem(int position) {
        return mDataList != null && position < mDataList.size() ? mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public @NonNull View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_data_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // bind data
        ItemContent item = getItem(position);
        holder.bind(item);

        return convertView;
    }

    /**
     * ViewHolder class
     */
    private static class ViewHolder {

        private final TextView mTitleTextView;

        public ViewHolder(View itemView) {
            mTitleTextView = itemView.findViewById(R.id.item_data);
        }

        public void bind(ItemContent item) {
            if (item != null) {
                mTitleTextView.setText(item.getTitle());
            } else {
                mTitleTextView.setText("");
            }
        }
    }
}
