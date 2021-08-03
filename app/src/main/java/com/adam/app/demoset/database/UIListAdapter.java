package com.adam.app.demoset.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UIListAdapter extends RecyclerView.Adapter<UIListAdapter.MyViewHolder> {

    private List<Note> mNotes;
    private Context mCtx;

    public UIListAdapter(Context context, @NonNull List<Note> notes) {
        Utils.info(this, "UIListAdapter constructor");
        mCtx = context;
        this.mNotes = notes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Utils.info(this, "onCreateViewHolder enter");
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_note_layout,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Utils.info(this, "onBindViewHolder enter");
        Note note = mNotes.get(position);

        // Set item information
        holder.mTimeStamp.setText(formateDate(note.getTimeStamp()));
        holder.mNote.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        Utils.info(this, "getItemCount enter size = " + mNotes.size());
        return mNotes.size();
    }

    /**
     * @param timeStamp
     * @return
     */
    private String formateDate(String timeStamp) {
        Utils.info(this, "formateDate enter");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(timeStamp);
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTimeStamp;
        TextView mNote;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTimeStamp = itemView.findViewById(R.id.timestamp);
            mNote = itemView.findViewById(R.id.note);
        }
    }
}
