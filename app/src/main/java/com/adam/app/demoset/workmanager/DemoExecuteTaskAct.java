package com.adam.app.demoset.workmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.databinding.ActivityDemoExecuteTaskBinding;

import java.util.List;


public class DemoExecuteTaskAct extends AppCompatActivity {

    private MyViewModel mViewModel;
    private ActivityDemoExecuteTaskBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBinding =  ActivityDemoExecuteTaskBinding.inflate(getLayoutInflater());
        setContentView(this.mBinding.getRoot());

        // instance view model
        this.mViewModel = new MyViewModel(getApplication());

        // Ui observer according to work process
        this.mViewModel.getMyWorkInfo().observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> ListOfWorkInfo) {
                Utils.info(DemoExecuteTaskAct.this, "View modele onChange!!!");
                // preprocess
                if (ListOfWorkInfo == null || ListOfWorkInfo.isEmpty()) {
                    Utils.info(DemoExecuteTaskAct.this, "No work info list!!!");
                    return;
                }

                // check work status
                WorkInfo workInfo = ListOfWorkInfo.get(0);
                boolean isFinished = workInfo.getState().isFinished();
                Utils.info(DemoExecuteTaskAct.this, "isFinished: " + isFinished);
                if (!isFinished) {
                    updateButtonView(true);
                    mBinding.seeFileButton.setVisibility(ViewState.findby(false).toValue());
                } else {
                    updateButtonView(false);
                    Data outputData = workInfo.getOutputData();
                    // get output image uri
                    String OutputImageUriStr = outputData.getString(Utils.THE_SELECTED_IMAGE);
                    Utils.info(DemoExecuteTaskAct.this, "OutputImageUriStr: " + OutputImageUriStr);
                    // the see file button is visble when the file exists
                    if (!TextUtils.isEmpty(OutputImageUriStr)) {
                        mViewModel.setOutputUri(OutputImageUriStr);
                        mBinding.seeFileButton.setVisibility(ViewState.findby(true).toValue());
                    }
                }

            }
        });

        // Execute button
        this.mBinding.goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go
                mViewModel.applyBlur(getLevel());
            }
        });

        // see file
        this.mBinding.seeFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(DemoExecuteTaskAct.this, "seeFileButton");
                Utils.info(DemoExecuteTaskAct.this, "seeFileButton is clicked!!!");
                Uri currentUri = mViewModel.getOutputUri();
                Utils.info(DemoExecuteTaskAct.this, "currentUri: " + currentUri);
                if (currentUri != null) {
                    Intent actionView = new Intent(Intent.ACTION_VIEW, currentUri);
                    if (actionView.resolveActivity(getPackageManager()) != null) {
                        startActivity(actionView);
                    }
                }
            }
        });


        // cancel task
        this.mBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(DemoExecuteTaskAct.this, "cancelButton");
                mViewModel.cancelWork();
            }
        });

    }


    private enum ViewState {
        SHOW(true) {
            @Override
            int toValue() {
                return View.VISIBLE;
            }
        },
        HIDE(false) {
            @Override
            int toValue() {
                return View.GONE;
            }
        };

        private boolean mKey;
        private ViewState(boolean key) {
            this.mKey = key;
        }

        public static ViewState findby(boolean key) {
            for (ViewState e: ViewState.values()) {
                if (e.mKey == key) {
                    return e;
                }
            }
            return null;
        }

        abstract int toValue();
    }


    /**
     * update Button according the work status
     *      process: progressBar, cancel Visible and go, see file Gone
     *      finish: progressBar, cancel Gone and go, see file Visible
     */
    private void updateButtonView(boolean isShow) {
        this.mBinding.progressBar.setVisibility(ViewState.findby(isShow).toValue());
        this.mBinding.cancelButton.setVisibility(ViewState.findby(isShow).toValue());
        this.mBinding.goButton.setVisibility(ViewState.findby(!isShow).toValue());

    }

    /**
     * Finish state
     */
    private void showWorkFinish() {

    }


    /**
     * Depend on user choice
     * @return
     */
    private int getLevel() {
        int choiceId = this.mBinding.radioBlurGroup.getCheckedRadioButtonId();

        switch (choiceId) {
            case R.id.radio_blur_lv_1:
                return 1;
            case R.id.radio_blur_lv_2:
                return 2;
            case R.id.radio_blur_lv_3:
                return 3;
        }

        return 1;
    }

}
