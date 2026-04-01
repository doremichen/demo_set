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

package com.adam.app.demoset.database.contentprovider;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.database.contentprovider.entity.Note;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UIListAdapter extends RecyclerView.Adapter<UIListAdapter.MyViewHolder> {

    // data formate: "yyyy-MM-dd HH:mm:ss"
    private final DateTimeFormatter mInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    // output formate: "yyyy-MM-dd HH:mm"
    private final DateTimeFormatter mOutputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault());
    private final List<Note> mNotes;

    public UIListAdapter(@NonNull List<Note> notes) {
        Utils.info(this, "UIListAdapter constructor");
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
        Note note = mNotes.get(position);
        holder.mTimeStamp.setText(formateDate(note.getTimeStamp()));
        holder.mNote.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return mNotes != null ? mNotes.size() : 0;
    }

    /**
     * Format date to "yyyy-MM-dd HH:mm"
     */
    private String formateDate(String timeStamp) {
        if (timeStamp == null || timeStamp.isEmpty()) return "unknown";

        try {
            // parse
            LocalDateTime dateTime = LocalDateTime.parse(timeStamp, mInputFormatter);
            // format
            return dateTime.format(mOutputFormatter);
        } catch (DateTimeParseException e) {
            Utils.info(this, "formateDate error: " + e.getMessage());
            // return unknown
            return "unknown";
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTimeStamp;
        TextView mNote;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTimeStamp = itemView.findViewById(R.id.timestamp);
            mNote = itemView.findViewById(R.id.note);
        }
    }
}