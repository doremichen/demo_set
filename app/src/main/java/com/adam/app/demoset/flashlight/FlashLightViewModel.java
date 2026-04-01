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

import com.adam.app.demoset.utils.Utils;

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
