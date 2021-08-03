package com.adam.app.demoset.workmanager;

import android.content.Context;
import android.support.annotation.NonNull;
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
        Context appContext = getApplicationContext();

        File outputDir = new File(appContext.getFilesDir(), Utils.OUTPUT_PATH);

        try {
            // Check exists
            if (outputDir.exists()) {
                File[] files = outputDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        String name = f.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            boolean deleted = f.delete();
                            Utils.info(this, name + " is delted " + deleted);
                        }
                    }
                }
            }
            Utils.makeStatusNotification("Clean up Successful", appContext);
            return Result.SUCCESS;
        } catch (Exception e) {
            Utils.info(this, "Clean error");
            Utils.makeStatusNotification("Clean up error", appContext);
            return Result.FAILURE;
        }

    }
}
