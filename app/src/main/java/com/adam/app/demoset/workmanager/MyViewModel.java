package com.adam.app.demoset.workmanager;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.List;
import java.util.stream.IntStream;


public class MyViewModel extends ViewModel {

    private @NonNull Uri mImageUri;
    private @NonNull WorkManager mManager;
    private @NonNull LiveData<List<WorkInfo>> mSaveWorkInfo;
    private Uri mOutputUri;


    public MyViewModel(@NonNull Application app) {
        super();
        Utils.info(this, "MyViewModel constructor");
        // work manager instance
        this.mManager = WorkManager.getInstance(app);
        // prune work
        this.mManager.pruneWork();
        // image uri
        this.mImageUri = buildImageUri(app.getApplicationContext());

        // UI listen to work change
        this.mSaveWorkInfo = this.mManager.getWorkInfosByTagLiveData(Utils.TAG_IMG_OUTPUT);
    }

    public void updateImgUri(@NonNull Uri imgUri) {
        this.mImageUri = imgUri;
    }

    /**
     * Return work info of Save process
     * @return
     */
    LiveData<List<WorkInfo>> getMyWorkInfo() {
        return this.mSaveWorkInfo;
    }


    /**
     * Set ouput uri
     */
    void setOutputUri(String outputUri) {
        Utils.info(this, "setImageUri enter uri = " + outputUri);
        this.mOutputUri = uriOrNull(outputUri);
    }

    /**
     * Get output uri
     * @return
     */
    Uri getOutputUri() {
        Utils.info(this, "getOutputUri");
        return this.mOutputUri;
    }

    /**
     * Cancel work process
     */
    void cancelWork() {
        Utils.info(this, "cancelWork enter");
        mManager.cancelUniqueWork(Utils.IMAGE_MANIPULATION_WORK_NAME);
    }


    /**
     *
     *      chain of work example
     *         // clean up
     *         OneTimeWorkRequest cleanupRequest = OneTimeWorkRequest.from(CleanupWorker.class);
     *
     *         // blurred image
     *         OneTimeWorkRequest blurRequest = new OneTimeWorkRequest.Builder(MyWork.class)
     *                 .setInputData(createInputDataForUri())
     *                 .build();
     *
     *         // save to file
     *         OneTimeWorkRequest saveRequest = new OneTimeWorkRequest.Builder(SaveToFileWorker.class).build();
     *
     *         // chain of work task
     *         WorkContinuation continuation = this.mManager.beginWith(cleanupRequest);
     *         continuation = continuation.then(blurRequest);
     *         continuation = continuation.then(saveRequest);
     *
     *         // start to work
     *         continuation.enqueue();
     *
     *         single work example
     *         // enqueue single task
     *         this.mManager.enqueue(blurRequest);
     * @param level
     */
    void applyBlur(int level) {
        Utils.info(this, "applyBlur enter leve: " + level);
        Utils.info(this, "applyBlur enter level: " + level);

        // Start with cleanup work request
        WorkContinuation continuation = mManager.beginUniqueWork(
                Utils.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker.class)
        );

        // Chain blur work requests based on the level
        /**
            int i = 0;
            while (i < level) {
                // my work request builder
                OneTimeWorkRequest.Builder myWorkBuilder = new OneTimeWorkRequest.Builder(MyWork.class);
                // Input Uri for the first blur image request
                if (i == 0) {
                    myWorkBuilder.setInputData(createInputDataForUri());
                }

                   // Add my work request to work manager
                continuation = continuation.then(myWorkBuilder.build());
                i++;
            }
        */
        continuation = IntStream.range(0, level)
                .mapToObj(i -> new OneTimeWorkRequest.Builder(MyWork.class)
                        .setInputData(i == 0 ? createInputDataForUri() : Data.EMPTY)
                        .build())
                .reduce(continuation, WorkContinuation::then, (a, b) -> b);

        // Constraints and save request
        OneTimeWorkRequest saveReq = new OneTimeWorkRequest.Builder(SaveToFileWorker.class)
                .setConstraints(new Constraints.Builder().setRequiresCharging(false).build())
                .addTag(Utils.TAG_IMG_OUTPUT)
                .build();

        // Chain save request and start work process
        continuation.then(saveReq).enqueue();
//        // clean up work request
//        OneTimeWorkRequest cleanupReq = OneTimeWorkRequest.from(CleanupWorker.class);
//        // Add start request to work manager
//        WorkContinuation continuation = this.mManager.beginUniqueWork(Utils.IMAGE_MANIPULATION_WORK_NAME,
//                ExistingWorkPolicy.REPLACE,
//                cleanupReq);
//
//        // Add my work request to work manager depend on the level
//        int i = 0;
//        while (i < level) {
//            // my work request builder
//            OneTimeWorkRequest.Builder myWorkBuilder = new OneTimeWorkRequest.Builder(MyWork.class);
//            // Input Uri for the first blur image request
//            if (i == 0) {
//                myWorkBuilder.setInputData(createInputDataForUri());
//            }
//
//            // Add my work request to work manager
//            continuation = continuation.then(myWorkBuilder.build());
//            i++;
//        }
//
//        // Create constraint for work request
//        Constraints constraints = new Constraints.Builder()
//                .setRequiresCharging(false)
//                .build();
//
//        // save image work request
//        OneTimeWorkRequest saveReq = new OneTimeWorkRequest.Builder(SaveToFileWorker.class)
//                .setConstraints(constraints)
//                .addTag(Utils.TAG_IMG_OUTPUT)
//                .build();
//
//        // Add save image work request to work manager
//        continuation = continuation.then(saveReq);
//
//        // start work process
//        continuation.enqueue();

    }


    /**
     * Check Uri validity
     * @param uriString
     * @return
     */
    private Uri uriOrNull(@NonNull String uriString) {
        if (!TextUtils.isEmpty(uriString)) {
            return Uri.parse(uriString);
        }
        return null;
    }

    /**
     * Create the input data according the image resource uri
     * @return
     */
    private Data createInputDataForUri() {
        Utils.info(this, "createInputDataForUri enter mImageUri = " + mImageUri.toString());
        Data.Builder data = new Data.Builder();
        data.putString(Utils.THE_SELECTED_IMAGE, mImageUri.toString());
        return data.build();
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
