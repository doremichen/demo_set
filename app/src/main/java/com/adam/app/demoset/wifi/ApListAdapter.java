package com.adam.app.demoset.wifi;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.List;

public class ApListAdapter extends RecyclerView.Adapter<ApListAdapter.MyViewHolder> {

    private List<String> mSSIDs;
    private Context mCtx;

    public ApListAdapter(Context context) {
        Utils.info(this, "Constructor");
        mCtx = context;
    }

    public void setData(@NonNull List<String> ssids) {
        Utils.info(this, "setNotes enter mSSIDs = " + mSSIDs);
        this.mSSIDs = ssids;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Utils.info(this, "onCreateViewHolder enter");
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_ap_layout,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Utils.info(this, "onBindViewHolder enter");
        if (mSSIDs != null) {
            String ssidName = mSSIDs.get(position);
            // Set item information
            holder.mApName.setText(ssidName);
        }

    }

    @Override
    public int getItemCount() {
        Utils.info(this, "getItemCount enter ");
        int count = (mSSIDs != null) ? mSSIDs.size() : 0;
        Utils.info(this, "count = " + count);
        return count;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mApName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mApName = itemView.findViewById(R.id.item_ap);
        }
    }
}
