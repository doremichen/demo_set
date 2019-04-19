package com.adam.app.demoset.workmanager;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.work.Data;
import androidx.work.WorkStatus;

public class DemoExecuteTaskAct extends AppCompatActivity {

    private MyViewModel mViewModel;
    private ImageView mImgView;
    private ProgressBar mProgress;
    private Button mBtnCancel;
    private Button mBtnExecute;
    private Button mBtnShow;


    private static final int TITLE_BLUR = 1;
    private static final int MORE_BLUR = 2;
    private static final int MOST_BLUR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_execute_task);
        mImgView = findViewById(R.id.image_view);
        mProgress = findViewById(R.id.progress_bar);
        mBtnCancel = findViewById(R.id.cancel_button);
        mBtnExecute = findViewById(R.id.go_button);
        mBtnShow = findViewById(R.id.see_file_button);

        // Get view model
        mViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Get the selected image
        Intent intent = getIntent();
        String imgUri = intent.getStringExtra(Utils.THE_SELECTED_IMAGE);
        Utils.inFo(this, "imgUri = " + imgUri);
        // Downlaod image by glid
        mViewModel.setImageUri(imgUri);
        if (mViewModel.getImageUri() != null) {
            // Load image to show
            Glide.with(this).load(mViewModel.getImageUri()).into(mImgView);
        }

        // Work status
        mViewModel.getSaveWorkStatus().observe(this, new Observer<List<WorkStatus>>() {

            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                Utils.inFo(this, "onChanged enter");

                // Check if work status exists
                if (mExecuteWork == false || workStatuses == null || workStatuses.isEmpty()) {
                    Utils.inFo(this, "No execute, No save work or save work is empty");
                    return;
                }

                // Check tag
                WorkStatus workStatus = workStatuses.get(0);

                boolean isFinished = workStatus.getState().isFinished();
                Utils.inFo(this, "isFinished = " + isFinished);

                if (isFinished) {
                    updatButtonStatus(false);

                    // Normally this processing, which is not directly related to drawing views on
                    // screen would be in the ViewModel. For simplicity we are keeping it here.
                    Data outputData = workStatus.getOutputData();
                    String imageUri = outputData.getString(Utils.THE_SELECTED_IMAGE);

                    if (!TextUtils.isEmpty(imageUri)) {
                        mViewModel.setImageUri(imageUri);
                        mBtnShow.setVisibility(View.VISIBLE);
                        mExecuteWork = false;
                    }

                } else {
                    updatButtonStatus(true);
                    mBtnShow.setVisibility(View.GONE);
                }

            }
        });

    }

    public void onCancel(View view) {
        Utils.inFo(this, "onCancel enter");
        mViewModel.cancelWork();
    }

    boolean mExecuteWork;

    public void onExecute(View view) {
        Utils.inFo(this, "onExecute enter");
        mExecuteWork = true;
        mViewModel.applyBlur(getRadioOption());
    }

    public void onResult(View view) {
        Utils.inFo(this, "onResult enter");
        Uri imgUri = mViewModel.getImageUri();
        if (imgUri != null) {
            Intent actionView = new Intent(Intent.ACTION_VIEW, imgUri);
            if (actionView.resolveActivity(getPackageManager()) != null) {
                startActivity(actionView);
            }
        }
    }

    private int getRadioOption() {
        Utils.inFo(this, "getRadioOption enter");
        RadioGroup group = findViewById(R.id.radio_blur_group);
        switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_blur_lv_1:
                return TITLE_BLUR;
            case R.id.radio_blur_lv_2:
                return MORE_BLUR;
            case R.id.radio_blur_lv_3:
                return MOST_BLUR;
        }
        return TITLE_BLUR;
    }

    private void updatButtonStatus(boolean isWorking) {
        Utils.inFo(this, "updatButtonStatus enter isWorking = " + isWorking);
        mProgress.setVisibility((isWorking) ? View.VISIBLE : View.GONE);
        mBtnCancel.setVisibility((isWorking) ? View.VISIBLE : View.GONE);
        mBtnExecute.setVisibility((isWorking) ? View.GONE : View.VISIBLE);
    }
}
