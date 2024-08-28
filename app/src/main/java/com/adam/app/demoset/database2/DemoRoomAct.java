package com.adam.app.demoset.database2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database2.dialog.CreateNoteDialog;
import com.adam.app.demoset.database2.dialog.NoteDialog;
import com.adam.app.demoset.database2.dialog.UpdateNoteDialog;
import com.adam.app.demoset.database2.room.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DemoRoomAct extends AppCompatActivity {

    private static final int ACTION_SHOW_OPTION = 0X2456;

    private RecyclerView mRecyclerView;
    private TextView mEmptyNoteView;

    private NoteListAdapter mAdapter;
    private MyTouchItemListener mTouchListener;
    private List<Note> mAllNotes;
    private NoteViewModel mViewModel;
    private final Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Utils.info(this, "UI handleMessage enter");
            if (msg.what == ACTION_SHOW_OPTION) {
                int position = msg.arg1;
                showOptionDlg(position);
            }
        }
    };
    private final View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Utils.info(this, "Floating button clicked....");
            showAddNoteDlg();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");
        setContentView(R.layout.activity_demo_database2);


        mRecyclerView = this.findViewById(R.id.recycler_view);
        mEmptyNoteView = this.findViewById(R.id.empty_notes_view);

        // Get view Model
        mViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mViewModel.mAllNotes.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                Utils.info(this, "onChanged enter");
                mAllNotes = notes;

                // Update list
                assert mAllNotes != null;
                mAdapter.setNotes(mAllNotes);
                mAdapter.notifyDataSetChanged();

                showEmptyIfNoData();
            }
        });

        // Create list adapter
        mAdapter = new NoteListAdapter(this);

        // Build list
        RecyclerView.LayoutManager mGridManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        // Click listener
        mTouchListener = new MyTouchItemListener();
        mTouchListener.setonItemClickListener(new MyTouchItemListener.onItemClickListener() {
            @Override
            public void onLongClick(int position) {
                Utils.info(this, "onLongClick");

                triggerVibrator();

                Message msg = Message.obtain();
                msg.what = ACTION_SHOW_OPTION;
                msg.arg1 = position;
                mUIHandler.sendMessage(msg);
            }
        });
        mRecyclerView.addOnItemTouchListener(mTouchListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(mFabClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume enter");

        showEmptyIfNoData();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Utils.info(this, "onPause enter");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy enter");
        mTouchListener.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                this.finish();
                return true;
        }

        return false;
    }

    private void triggerVibrator() {
        Utils.info(this, "triggerVibrator enter");
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        if (!Utils.areAllNotNull(vibrator)) {
            Utils.info(this, "Vibrator service not available.");
            return; // Early return if vibrator service is not available
        }

        if (!vibrator.hasVibrator()) {
            Utils.info(this, "Device does not have a vibrator.");
            return; // Early return if device lacks a vibrator
        }

        // Vibrator is available, proceed with vibration
        vibrator.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE));
    }


    private void showEmptyIfNoData() {
        Utils.info(this, "showEmptyIfNoData enter");
        Utils.info(this, "mAllNotes = " + mAllNotes);
        Utils.info(this, "mEmptyNoteView = " + mEmptyNoteView);
        boolean hasNotes = (mAllNotes != null) && (!mAllNotes.isEmpty());
        mEmptyNoteView.setVisibility(hasNotes ? View.GONE : View.VISIBLE);
    }

    private void showAddNoteDlg() {
        // Show create note dialog
        NoteDialog dlg = new CreateNoteDialog(DemoRoomAct.this);
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {

            @Override
            public void onShowMessage(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");
                Utils.info(this, "content = " + content);
                Note note = new Note();
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                // Insert data
                mViewModel.insert(note);

            }
        });
        dlg.create().show();
    }

    private void showOptionDlg(final int position) {
        Utils.info(this, "showOptionDlg enter position = " + position);
        CharSequence[] items = {"Update", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle("Option:")
                .setItems(items, (dialog, which) -> {
                    Utils.info(this, "onClick enter which = " + which);
                    if (which == 0) {
                        showUpdateDlg(position);
                    } else if (which == 1) {
                        deleteNote(position);
                    }
                })
                .show();

        Utils.info(this, "showOptionDlg show");
    }

    private void showUpdateDlg(final int position) {
        Utils.info(this, "showUpdateDlg enter");
        if (!Utils.areAllNotNull(mAllNotes)) {
            Utils.showToast(this, "No data to update!!!");
            return;
        }

        final Note note = mAllNotes.get(position);

        NoteDialog dlg = new UpdateNoteDialog(DemoRoomAct.this);
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {
            @Override
            public void onShowMessage(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                // Update note
                mViewModel.update(note);

            }
        });
        dlg.create().show();
    }

    private void deleteNote(int position) {
        Utils.info(this, "deleteNote enter");
        if (Utils.areAllNotNull(mAllNotes)) {
            Note note = mAllNotes.get(position);
            // Delete note
            mViewModel.delete(note);
        }

    }

}
