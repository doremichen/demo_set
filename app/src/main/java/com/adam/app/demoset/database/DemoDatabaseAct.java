package com.adam.app.demoset.database;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.database.dialog.CreateNoteDialog;
import com.adam.app.demoset.database.dialog.NoteDialog;
import com.adam.app.demoset.database.dialog.UpdateNoteDialog;
import com.adam.app.demoset.database.provider.MyDBProvider;

import java.util.ArrayList;
import java.util.List;

public class DemoDatabaseAct extends AppCompatActivity {

    private static final int ACTION_SHOW_OPTION = 0X2456;
    public static final int REQUEST_VIBRATOR_PERMISSION_CODE = 0X1357;

    private RecyclerView mRecyclerView;
    private TextView mEmptyNoteView;

    private UIListAdapter mAdapter;
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

    private ArrayList<Note> mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate enter");
        setContentView(R.layout.activity_demo_database);


        mRecyclerView = this.findViewById(R.id.recycler_view);
        mEmptyNoteView = this.findViewById(R.id.empty_notes_view);

        // Set content resolver
        DBController.INSTANCE.setContentResolver(this.getContentResolver());

        // list view
        mNotes = new ArrayList<Note>();

        // Create list adapter
        mAdapter = new UIListAdapter(this, mNotes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
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

        loadData(mNotes);

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

    private void loadData(List<Note> notes) {
        Utils.info(this, "updateList enter");
        Cursor c = DBController.INSTANCE.queryNote("");

        // loop all data and add to list
        if (c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_ID));
                String timeStamp = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_TIMESTAMP));
                String content = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_NOTE));
                Note note = new Note(id, timeStamp, content);
                notes.add(note);
            } while (c.moveToNext());
        }

        // Update list
        mAdapter.notifyDataSetChanged();

    }

    private void showEmptyIfNoData() {
        Utils.info(this, "showEmptyIfNoData enter");
        if (mNotes.size() != 0) {
            mEmptyNoteView.setVisibility(View.GONE);
        } else {
            mEmptyNoteView.setVisibility(View.VISIBLE);
        }
    }

    private void showAddNoteDlg() {
        // Show create note dialog
        NoteDialog dlg = new CreateNoteDialog(DemoDatabaseAct.this);
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {
            @Override
            public void info(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoDatabaseAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");

                // Query data form database
                Cursor c = DBController.INSTANCE.queryNote(content);

                if (c != null) {
                    c.moveToFirst();
                    String id = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_ID));
                    String timeStamp = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_TIMESTAMP));
                    String note = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_NOTE));
                    Note newNote = new Note(id, timeStamp, note);
                    mNotes.add(newNote);

                    // Notify list adapter
                    mAdapter.notifyDataSetChanged();
                }

                showEmptyIfNoData();
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

        final Note note = mNotes.get(position);

        NoteDialog dlg = new UpdateNoteDialog(DemoDatabaseAct.this, note.getId());
        dlg.registerListener(new NoteDialog.OnControllerCallBack() {
            @Override
            public void info(String msg) {
                Utils.info(this, "info enter");
                Utils.showToast(DemoDatabaseAct.this, msg);
            }

            @Override
            public void updateList(String content) {
                Utils.info(this, "updateList enter");

                // Query data form database
                Cursor c = DBController.INSTANCE.queryNote(content);

                if (c != null) {
                    c.moveToFirst();
                    String strNote = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_NOTE));
                    String strTime = c.getString(c.getColumnIndex(MyDBProvider.COLUMN_TIMESTAMP));
                    // Update note content
                    note.updateNote(strNote);
                    note.updateTimeStamp(strTime);
                    mNotes.set(position, note);

                    // Notify list adapter
                    mAdapter.notifyDataSetChanged();
                }

                showEmptyIfNoData();
            }
        });
        dlg.create().show();
    }

    private void deleteNote(int position) {
        Utils.info(this, "deleteNote enter");
        Note note = mNotes.get(position);
        DBController.INSTANCE.deleteNote(note.getId());
        // reload data
        mNotes.clear();
        loadData(mNotes);
        showEmptyIfNoData();
    }

}
