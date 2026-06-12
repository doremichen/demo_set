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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.database.common.MyTouchItemListener;
import com.adam.app.demoset.database.contentprovider.entity.Note;
import com.adam.app.demoset.database.contentprovider.viewmodel.NoteViewModel;
import com.adam.app.demoset.database.dialog.CreateNoteDialog;
import com.adam.app.demoset.database.dialog.NoteDialog;
import com.adam.app.demoset.database.dialog.UpdateNoteDialog;
import com.adam.app.demoset.databinding.ActivityDemoDatabaseBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;

public class DemoDatabaseAct extends AppCompatActivity {

    private final ArrayList<Note> mNotes = new ArrayList<>();
    private ActivityDemoDatabaseBinding mBinding;
    private UIListAdapter mAdapter;
    private MyTouchItemListener mTouchListener;
    private NoteViewModel mViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");

        mBinding = ActivityDemoDatabaseBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.toolbarLayout);

        // Set technical description
        mBinding.content.demoDescription.setText(R.string.demo_content_provider_description);

        // Setup RecyclerView
        mAdapter = new UIListAdapter(mNotes);
        mBinding.content.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.content.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.content.recyclerView.setAdapter(mAdapter);

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        mViewModel.getNotes().observe(this, notes -> {
            Utils.info(this, "Notes observed update");
            mNotes.clear();
            if (notes != null) {
                mNotes.addAll(notes);
            }
            mAdapter.notifyDataSetChanged();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void triggerVibrator() {
        Utils.info(this, "triggerVibrator enter");
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vib != null) {
            vib.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void showEmptyIfNoData() {
        Utils.info(this, "showEmptyIfNoData enter");
        mBinding.content.emptyNotesView.setVisibility(mNotes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddNoteDlg() {
        NoteDialog dlg = new CreateNoteDialog(this);
        dlg.registerListener(new NoteDialog.OnDlgCallBack() {
            @Override
            public void onShowMessage(String msg) {
                Utils.showToast(DemoDatabaseAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                mViewModel.addNote(content);
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
        if (position < 0 || position >= mNotes.size()) return;
        final Note note = mNotes.get(position);

        NoteDialog dlg = new UpdateNoteDialog(this);
        dlg.registerListener(new NoteDialog.OnDlgCallBack() {
            @Override
            public void onShowMessage(String msg) {
                Utils.showToast(DemoDatabaseAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                mViewModel.updateNote(note.getId(), content);
            }
        });
        dlg.create().show();
    }

    private void deleteNote(int position) {
        if (position < 0 || position >= mNotes.size()) return;
        Note note = mNotes.get(position);
        mViewModel.deleteNote(note.getId());
    }
}
