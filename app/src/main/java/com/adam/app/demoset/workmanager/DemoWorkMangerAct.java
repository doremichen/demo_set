/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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
            mWorkManger.enqueue(OneTimeWorkRequest.from(MyWork.class));
            mBtnTest.setText(this.getResources().getString(R.string.action_show_img));
            mIshow = true;
        } else {
            showImage();
            mBtnTest.setText(this.getResources().getString(R.string.action_test_blur_img));
            mIshow = false;
        }

    }

    private void showImage() {
        Utils.inFo(this, "showImage enter");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(Utils.sImagePath);
        if (file.exists()) {
            Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.fileprovider", file);
            Utils.showToast(this, "<content>" + contentUri);
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
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                this.finish();
                return true;
        }

        return false;
    }
}