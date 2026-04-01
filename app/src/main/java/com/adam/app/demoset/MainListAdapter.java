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

package com.adam.app.demoset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.adam.app.demoset.main.ItemContent;

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
