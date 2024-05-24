/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.io.File;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class DemoWorkMangerAct extends AppCompatActivity {

    private WorkManager mWorkManger;
    private Button mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_work_manger);
        mBtnTest = this.findViewById(R.id.btn_test_wm);

        mWorkManger = WorkManager.getInstance();
    }

    boolean mIshow;

    public void onMyWork(View v) {
        if (!mIshow) {
            // reset
            Utils.sImagePath = null;

            mWorkManger.enqueue(OneTimeWorkRequest.from(MyWork.class));
            mBtnTest.setText(this.getResources().getString(R.string.action_show_img));
            mIshow = true;
        } else {
            showImage();
        }

    }

    private void showImage() {
        Utils.info(this, "showImage enter");
        if (Utils.sImagePath == null) {
            Utils.showToast(this, "the file path does not exist.");
            return;
        }

        // Switch button states
        mBtnTest.setText(this.getResources().getString(R.string.action_test_blur_img));
        mIshow = false;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Utils.sImagePath);
        if (file.exists()) {
            Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.fileprovider", file);
            Utils.info(this, "<content>" + contentUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "image/*");

            this.startActivity(intent);
        } else {
            Utils.showToast(this, "No image to show");
        }
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
}
