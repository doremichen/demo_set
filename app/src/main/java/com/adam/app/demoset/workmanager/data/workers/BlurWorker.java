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

package com.adam.app.demoset.workmanager.data.workers;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.utils.DemoAppConstants;
import com.adam.app.demoset.utils.Utils;

import java.io.FileNotFoundException;

/**
 * Worker to blur an image.
 */
public class BlurWorker extends Worker {

    public BlurWorker(@NonNull Context context,
                      @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork enter");
        Context appCtx = this.getApplicationContext();

        Utils.makeStatusNotification(appCtx, "Blurring Image!!!");
        Utils.delay(DemoAppConstants.DELAY_TIME_MILLIS);

        String resUri = getInputData().getString(DemoAppConstants.THE_SELECTED_IMAGE);

        if (TextUtils.isEmpty(resUri)) {
            Utils.info(this, "Invalid input uri...");
            Utils.makeStatusNotification(appCtx, "Invalid input uri...");
            return Result.failure();
        }

        try {
            Bitmap picture = BitmapFactory.decodeStream(appCtx.getContentResolver().openInputStream(Uri.parse(resUri)));

            // Blur the bitmap
            Bitmap output = Utils.blurBitmap(picture, appCtx);

            // Write bitmap to a temp file and show notification
            Uri outputUri = Utils.writeBitmapToFile(appCtx, output);
            Utils.makeStatusNotification(appCtx, "Output: " + outputUri.toString());

            // Prepare Result Data to pass
            Data outputData = new Data.Builder()
                    .putString(DemoAppConstants.THE_SELECTED_IMAGE, outputUri.toString())
                    .build();

            return Result.success(outputData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}
