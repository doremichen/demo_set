package com.adam.app.demoset.workmanager;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;

import java.util.List;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class MyViewModel extends ViewModel {

    public static final String IMAGE_TAG = "Image tag";
    public static final String IMAGE_WROK_PROCESS = "Image wrok process";
    private Uri mImageUri;
    private WorkManager mManager;
    private LiveData<List<WorkStatus>> mSaveWorkStatus;


    public MyViewModel() {
        mManager = WorkManager.getInstance();
        // Get work status
        mSaveWorkStatus = mManager.getStatusesByTagLiveData(IMAGE_TAG);
    }

    public LiveData<List<WorkStatus>> getSaveWorkStatus() {
        return mSaveWorkStatus;
    }

    void applyBlur(int level) {
        Utils.inFo(this, "applyBlur enter");
        // Work1: clean up
        // replace this code
//        WorkContinuation continuation = mManager.beginWith(OneTimeWorkRequest.from(CleanupWorker.class));
        WorkContinuation continuation = mManager.beginUniqueWork(IMAGE_WROK_PROCESS, ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker.class));

        // Work2: image blur
        for (int i = 0; i < level; i++) {
            OneTimeWorkRequest.Builder blurBuilder = new OneTimeWorkRequest.Builder(MyWork.class);

            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri());
            }

            continuation = continuation.then(blurBuilder.build());
        }


//        OneTimeWorkRequest blurRequest = new OneTimeWorkRequest.Builder(MyWork.class).
//                setInputData(createInputDataForUri()).build();
//        continuation = continuation.then(blurRequest);

        // Work3: save image
        OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(SaveToFileWorker.class).
                addTag(IMAGE_TAG).
                build();
        continuation = continuation.then(saveRequest);

        // Start work
        continuation.enqueue();

//        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(MyWork.class).
//                setInputData(createInputDataForUri()).build();
//
//        mManager.enqueue(request);
    }

    void cancelWork() {
        Utils.inFo(this, "cancelWork enter");
        mManager.cancelUniqueWork(IMAGE_WROK_PROCESS);
    }


    private Uri uriOrNull(@NonNull String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    /**
     * Setters
     */
    void setImageUri(String uri) {
        mImageUri = uriOrNull(uri);
    }

    /**
     * Getters
     */
    Uri getImageUri() {
        return mImageUri;
    }

    private Data createInputDataForUri() {
        Data.Builder builder = new Data.Builder();
        if (mImageUri != null) {
            builder.putString(Utils.THE_SELECTED_IMAGE, mImageUri.toString());
        }

        return builder.build();
    }

}
