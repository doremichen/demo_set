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

package com.adam.app.demoset.database.room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.room.entity.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    private List<Note> mNotes;

    public NoteListAdapter() {
        Utils.info(this, "Constructor");
    }

    public void setNotes(@NonNull List<Note> notes) {
        Utils.info(this, "setNotes enter notes = " + notes);
        this.mNotes = notes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Utils.info(this, "onCreateViewHolder enter");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note_layout,
                viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Utils.info(this, "onBindViewHolder enter");
        if (mNotes != null) {
            Note note = mNotes.get(position);

            // Set item information
            holder.mTimeStamp.setText(formatDate(note.getTimeStamp()));
            holder.mNote.setText(note.getNote());
        }

    }

    @Override
    public int getItemCount() {
        Utils.info(this, "getItemCount enter ");
        int count = (mNotes != null) ? mNotes.size() : 0;
        Utils.info(this, "count = " + count);
        return count;
    }

    /**
     * @param timeStamp
     * @return
     */
    private String formatDate(String timeStamp) {
        Utils.info(this, "formatDate enter");
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
