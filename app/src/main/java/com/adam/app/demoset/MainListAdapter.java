/**
 * Main list view adapter
 * <p>
 * info:
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

import java.util.List;


public class MainListAdapter extends BaseAdapter {

    private List<ItemContent> mDatas;
    private LayoutInflater mInflater;

    public MainListAdapter(Context context, List<ItemContent> datas) {
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = this.mInflater.inflate(R.layout.item_data_layout, null);
            holder = new ViewHolder();
            // get view id
            holder.mTitle = (TextView) convertView.findViewById(R.id.item_data);

            //set tag
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        // set item text
        holder.mTitle.setText(mDatas.get(position).getTitle());


        return convertView;
    }

    private class ViewHolder {
        public TextView mTitle;
    }
}
