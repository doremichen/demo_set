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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.database.common.MyTouchItemListener;
import com.adam.app.demoset.database.dialog.CreateNoteDialog;
import com.adam.app.demoset.database.dialog.NoteDialog;
import com.adam.app.demoset.database.dialog.UpdateNoteDialog;
import com.adam.app.demoset.database.room.entity.Note;
import com.adam.app.demoset.database.room.viewmodel.NoteViewModel;
import com.adam.app.demoset.databinding.ActivityDemoDatabaseBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DemoRoomAct extends AppCompatActivity {

    private ActivityDemoDatabaseBinding mBinding;
    private NoteListAdapter mAdapter;
    private MyTouchItemListener mTouchListener;
    private List<Note> mAllNotes;
    private NoteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");

        mBinding = ActivityDemoDatabaseBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.toolbarLayout);

        // Set technical description
        mBinding.content.demoDescription.setText(R.string.demo_room_description);

        // Setup Adapter
        mAdapter = new NoteListAdapter();
        mBinding.content.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mBinding.content.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.content.recyclerView.setAdapter(mAdapter);

        // Get view Model
        mViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        mViewModel.getAllNotes().observe(this, notes -> {
            Utils.info(this, "onChanged enter");
            mAllNotes = notes;
            if (mAllNotes != null) {
                mAdapter.setNotes(mAllNotes);
                mAdapter.notifyDataSetChanged();
            }
            showEmptyIfNoData();
        });

        // Click listener
        mTouchListener = new MyTouchItemListener();
        mTouchListener.setonItemClickListener(position -> {
            Utils.info(this, "onLongClick");
            triggerVibrator();
            runOnUiThread(() -> showOptionDlg(position));
        });
        mBinding.content.recyclerView.addOnItemTouchListener(mTouchListener);

        setSupportActionBar(mBinding.toolbar);
        mBinding.fab.setOnClickListener(view -> showAddNoteDlg());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume enter");
        showEmptyIfNoData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy enter");
        if (mTouchListener != null) {
            mTouchListener.release();
        }
        mBinding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_exit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.demo_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void triggerVibrator() {
        Utils.info(this, "triggerVibrator enter");
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Backward compatibility for older APIs
                vibrator.vibrate(1000L);
            }
        } else {
            Utils.info(this, "Vibrator service not available or device lacks a vibrator.");
        }
    }

    private void showEmptyIfNoData() {
        Utils.info(this, "showEmptyIfNoData enter");
        boolean hasNotes = (mAllNotes != null) && (!mAllNotes.isEmpty());
        mBinding.content.emptyNotesView.setVisibility(hasNotes ? View.GONE : View.VISIBLE);
    }

    private void showAddNoteDlg() {
        NoteDialog dlg = new CreateNoteDialog(this);
        dlg.registerListener(new NoteDialog.OnDlgCallBack() {
            @Override
            public void onShowMessage(String msg) {
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Note note = new Note();
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                mViewModel.insert(note);
            }
        });
        dlg.create().show();
    }

    private void showOptionDlg(final int position) {
        Utils.info(this, "showOptionDlg enter position = " + position);
        CharSequence[] items = new CharSequence[]{
                getString(R.string.demo_database_dlg_update),
                getString(R.string.demo_database_dlg_delete)};

        new AlertDialog.Builder(this)
                .setTitle(R.string.demo_database_dlg_option)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        showUpdateDlg(position);
                    } else if (which == 1) {
                        deleteNote(position);
                    }
                })
                .show();
    }

    private void showUpdateDlg(final int position) {
        if (mAllNotes == null || position < 0 || position >= mAllNotes.size()) {
            Utils.showToast(this, "No data to update!!!");
            return;
        }

        final Note note = mAllNotes.get(position);
        NoteDialog dlg = new UpdateNoteDialog(this);
        dlg.registerListener(new NoteDialog.OnDlgCallBack() {
            @Override
            public void onShowMessage(String msg) {
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                mViewModel.update(note);
            }
        });
        dlg.create().show();
    }

    private void deleteNote(int position) {
        if (mAllNotes != null && position >= 0 && position < mAllNotes.size()) {
            mViewModel.delete(mAllNotes.get(position));
        }
    }
}
