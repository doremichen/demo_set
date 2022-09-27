/**
 * Flash light view model
 */
package com.adam.app.demoset.flashlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.adam.app.demoset.Utils;

import java.util.List;

public class FlashLightViewModel extends ViewModel {

    private static final String TAG_FLASH_LIGHT_WORK = "tag.flash.light.work";

    private WorkManager mWorkManger;
    private LiveData<List<WorkInfo>> mListOfWork;

    interface ViewModelCallBack {
        public void onUpdate();
    }

    private ViewModelCallBack mCallback;


    public FlashLightViewModel(@NonNull AppCompatActivity ctx, @NonNull ViewModelCallBack cb) {
        super();
        this.mCallback = cb;
        // initial
        this.mWorkManger = WorkManager.getInstance(ctx.getApplicationContext());
        // list of work
        this.mListOfWork = this.mWorkManger.getWorkInfosByTagLiveData(TAG_FLASH_LIGHT_WORK);
        // observer
        this.mListOfWork.observe(ctx, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                Utils.info(FlashLightViewModel.this, "onChange!!!");
                if (workInfos == null || workInfos.isEmpty() == true) {
                    Utils.info(FlashLightViewModel.this, "No work info!!!");
                    return;
                }

                // check work status
                WorkInfo workInfo = workInfos.get(0);
                boolean isFinished = workInfo.getState().isFinished();
                if (isFinished == true) {
                    mCallback.onUpdate();
                }

            }
        });
    }

    /**
     * Enable flash light
     * @param enabled
     *        true: turn on
     *        false: turn off
     */
    public void enableFlashlight(boolean enabled) {
        Utils.info(this, "enableFlashlight");
        // put flash light work in work manager
        OneTimeWorkRequest.Builder flashListReqBuilder = new OneTimeWorkRequest.Builder(FlashLightWork.class);
        Data inputData = buildData(enabled);
        // put data in request
        flashListReqBuilder.setInputData(inputData);
        flashListReqBuilder.addTag(TAG_FLASH_LIGHT_WORK);
        this.mWorkManger.enqueue(flashListReqBuilder.build());
    }

    /**
     * Build data to work
     * @param value
     * @return
     */
    private Data buildData(boolean value) {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putBoolean(FlashLightWork.KEY_ON, value);
        return  dataBuilder.build();
    }

}
