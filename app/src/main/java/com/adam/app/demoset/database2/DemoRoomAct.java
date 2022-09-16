package com.adam.app.demoset.database2;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database2.dialog.CreateNoteDialog;
import com.adam.app.demoset.database2.dialog.NoteDialog;
import com.adam.app.demoset.database2.dialog.UpdateNoteDialog;
import com.adam.app.demoset.database2.room.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DemoRoomAct extends AppCompatActivity {

    private static final int ACTION_SHOW_OPTION = 0X2456;

    private RecyclerView mRecyclerView;
    private TextView mEmptyNoteView;

    private NoteListAdapter mAdapter;
    private MyTouchItemListener mTouchListener;

    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.info(this, "UI handleMessage enter");
            switch (msg.what) {
                case ACTION_SHOW_OPTION:
                    int position = msg.arg1;
                    showOptionDlg(position);
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Utils.info(this, "Floating button clicked....");
            showAddNoteDlg();
        }
    };

    private List<Note> mAllNotes;
    private NoteViewModel mViewModel;

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
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(VibrationEffect.createOneShot(1000L, VibrationEffect.DEFAULT_AMPLITUDE));
    }


    private void showEmptyIfNoData() {
        Utils.info(this, "showEmptyIfNoData enter");
        Utils.info(this, "mAllNotes = " + mAllNotes);
        Utils.info(this, "mEmptyNoteView = " + mEmptyNoteView);
        if ((mAllNotes != null) && (mAllNotes.size() != 0)) {
            mEmptyNoteView.setVisibility(View.GONE);
        } else {
            mEmptyNoteView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddNoteDlg() {
        // Show create note dialog
        NoteDialog dlg = new CreateNoteDialog(DemoRoomAct.this);
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {

            @Override
            public void info(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");
                Utils.info(this, "content = " + content);
                Note note = new Note();
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                // Insert data
                mViewModel.insert(note);

            }
        });
        dlg.create().show();
    }

    private void showOptionDlg(final int position) {
        Utils.info(this, "showOptionDlg enter position = " + position);
        CharSequence[] items = new CharSequence[]{"Update", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Option:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.info(this, "onClick enter which = " + which);
                switch (which) {
                    case 0:     // Update
                        showUpdateDlg(position);
                        break;
                    case 1:     // Delete
                        deleteNote(position);
                        break;
                }
            }
        });
        Utils.info(this, "showOptionDlg show");
        builder.create().show();
    }

    private void showUpdateDlg(final int position) {
        Utils.info(this, "showUpdateDlg enter");

        final Note note = mAllNotes.get(position);

        NoteDialog dlg = new UpdateNoteDialog(DemoRoomAct.this);
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {
            @Override
            public void info(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoRoomAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");
                note.setNote(content);
                note.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                // Update note
                mViewModel.update(note);

            }
        });
        dlg.create().show();
    }

    private void deleteNote(int position) {
        Utils.info(this, "deleteNote enter");
        if (mAllNotes != null) {
            Note note = mAllNotes.get(position);
            // Delete note
            mViewModel.delete(note);
        }

    }

}
