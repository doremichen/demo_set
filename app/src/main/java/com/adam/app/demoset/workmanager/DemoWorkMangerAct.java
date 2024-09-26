/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.io.File;
import java.util.List;

public class DemoWorkMangerAct extends AppCompatActivity {

    private final static String WORK_REQUEST_TAG = "request tag";
    boolean mShowImage;
    private WorkManager mWorkManger;
    private Button mBtnTest;
    private Uri mImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_work_manger);
        mBtnTest = this.findViewById(R.id.btn_test_wm);

        this.mImgUri = buildImageUri(this);

        mWorkManger = WorkManager.getInstance(this);
        // prune work
        this.mWorkManger.pruneWork();

        // observer work info state
        this.mWorkManger.getWorkInfosByTagLiveData(WORK_REQUEST_TAG)
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {

                        // preprocess
                        if (workInfos.isEmpty()) {
                            Utils.info(DemoWorkMangerAct.this, "No work info list!!!");
                            return;
                        }

                        // check work status
                        WorkInfo workInfo = workInfos.get(0);
                        Utils.info(DemoWorkMangerAct.this, "onChange: " + workInfo.toString());
                        boolean isFinished = workInfo.getState().isFinished();
                        Utils.info(DemoWorkMangerAct.this, "isFinished: " + isFinished);

                        if (isFinished) {
                            Data outputData = workInfo.getOutputData();

                            // get output image uri
                            String OutputImageUriStr = outputData.getString(Utils.THE_SELECTED_IMAGE);
                            Utils.info(DemoWorkMangerAct.this, "OutputImageUriStr: " + OutputImageUriStr);
                            // the see file button is visble when the file exists
                            if (!TextUtils.isEmpty(OutputImageUriStr)) {
                                // enable show button
                                mBtnTest.setText(DemoWorkMangerAct.this.getResources().getString(R.string.action_show_img));
                            }
                        }

                    }
                });
    }

    public void onMyWork(View v) {

        if (!mShowImage) {
            // work request
            OneTimeWorkRequest blurRequest = new OneTimeWorkRequest.Builder(MyWork.class)
                    .setInputData(createInputDataForUri())
                    .addTag(WORK_REQUEST_TAG)
                    .build();
            // start
            this.mWorkManger.enqueue(blurRequest);
        } else {
            showImage();

            // enable test button
            mBtnTest.setText(this.getResources().getString(R.string.action_test_blur_img));
        }

        mShowImage = !mShowImage;


//        if (mShowImage && !Utils.areAllNotNull(Utils.sImagePath)) {
//            Utils.showToast(this, "the file path does not exist.");
//            return;
//        }
//
//
//        if (!mShowImage) {
//            // reset
//            Utils.sImagePath = null;
//
//            mWorkManger.enqueue(OneTimeWorkRequest.from(MyWork.class));
//            mBtnTest.setText(this.getResources().getString(R.string.action_show_img));
//        } else {
//            showImage();
//        }
//
//        mShowImage = !mShowImage;

    }

    private void showImage() {
        Utils.info(this, "showImage enter");

        File file = new File(Utils.sImagePath);

        if (!file.exists()) {
            Utils.showToast(this, "No image to show");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.filemanager.provider", file);
        Utils.info(this, "<content>" + contentUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "image/*");
        this.startActivity(intent);

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

    /**
     * Create the input data according the image resource uri
     *
     * @return
     */
    private Data createInputDataForUri() {
        Utils.info(this, "createInputDataForUri enter mImageUri = " + this.mImgUri.toString());
        Data.Builder builder = new Data.Builder();
        builder.putString(Utils.THE_SELECTED_IMAGE, this.mImgUri.toString());
        return builder.build();
    }

    /**
     * Build image Uri according to app resource
     */
    private Uri buildImageUri(Context ctx) {
        Utils.info(this, "getImageUri enter");
        Resources resources = ctx.getResources();

        // build uri
        Uri imageUri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.test))
                .appendPath(resources.getResourceTypeName(R.drawable.test))
                .appendPath(resources.getResourceEntryName(R.drawable.test))
                .build();

        Utils.info(this, "imageUri: " + Uri.parse(imageUri.toString()));
        return imageUri;
    }
}
