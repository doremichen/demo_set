package com.adam.app.demoset.database2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database2.room.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private List<Note> mNotes;
    private Context mCtx;

    public NoteListAdapter(Context context) {
        Utils.inFo(this, "Constructor");
        mCtx = context;
    }

    public void setNotes(@NonNull List<Note> notes) {
        Utils.inFo(this, "setNotes enter notes = " + notes);
        this.mNotes = notes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Utils.inFo(this, "onCreateViewHolder enter");
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_note_layout,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Utils.inFo(this, "onBindViewHolder enter");
        if (mNotes != null) {
            Note note = mNotes.get(position);

            // Set item information
            holder.mTimeStamp.setText(formatDate(note.getTimeStamp()));
            holder.mNote.setText(note.getNote());
        }

    }

    @Override
    public int getItemCount() {
        Utils.inFo(this, "getItemCount enter ");
        int count = (mNotes != null) ? mNotes.size() : 0;
        Utils.inFo(this, "count = " + count);
        return count;
    }

    /**
     * @param timeStamp
     * @return
     */
    private String formatDate(String timeStamp) {
        Utils.inFo(this, "formatDate enter");
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
