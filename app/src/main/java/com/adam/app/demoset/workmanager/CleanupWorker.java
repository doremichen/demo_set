package com.adam.app.demoset.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.Utils;

import java.io.File;
import java.util.Arrays;

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

        // Make a notification when the work starts and slows down the work
        Utils.makeStatusNotification("Clean up old temporary files", appCtx);
        Utils.delay(Utils.DELAY_TIME_MILLIS);

        try {
            File outputDir = new File(appCtx.getFilesDir(), Utils.OUTPUT_PATH);
            File[] files = outputDir.exists() ? outputDir.listFiles() : null;

            if (!Utils.isArrayNullOrEmpty(files)) {
                Arrays.stream(files)
                        .filter(file -> file.getName().endsWith(".png"))
                        .forEach(file -> {
                            boolean deleted = file.delete();
                            Utils.info(this, String.format("Deleted file: %s - %s", file.getName(), deleted));
                        });
            }

            Utils.info(this, "Done!!!: pass: true");
            return Result.success();

        } catch (Exception e) {
            Utils.info(this, "Done!!!: pass: false");
            return Result.failure();
        }
    }
}
