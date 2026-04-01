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

package com.adam.app.demoset.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.utils.Utils;

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
        Utils.makeStatusNotification(appCtx, "Clean up old temporary files");
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
