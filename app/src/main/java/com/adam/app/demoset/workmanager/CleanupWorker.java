package com.adam.app.demoset.workmanager;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;

import java.io.File;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CleanupWorker extends Worker {
    public CleanupWorker(@NonNull Context context,
                         @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork enter");
        Context appCtx = getApplicationContext();

        // Make a notification when the work start and slows down the work
        // so that see each workRequest start
        Utils.makeStatusNotification("Clean up old temporary files", appCtx);
        Utils.delay(Utils.DELAY_TIME_MILLIS);

        boolean pass = false;

        try {
            File outputDir = new File(appCtx.getFilesDir(), Utils.OUTPUT_PATH);
            // check exists
            if (outputDir.exists()) {
                File[] files = outputDir.listFiles();
                if (files != null && files.length > 0) {
                    // loop
                    for (File f: files) {
                        String name = f.getName();
                        // delete if the file is png type
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            // delete
                            boolean deleted = f.delete();
                            Utils.info(this, String.format("deleted file: %s - %s", name, deleted));
                        }
                    }
                }
            }
            pass = true;
        } catch (Exception e) {

        }
        Utils.info(this, "Done!!!: pass: " + pass);
        return (pass == true)? Result.success(): Result.failure();
    }
}
