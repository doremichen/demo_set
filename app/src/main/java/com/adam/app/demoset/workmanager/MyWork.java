/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.Utils;

import java.io.FileNotFoundException;

public class MyWork extends Worker {

    public MyWork(@NonNull Context context,
                  @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork enter");
        Context appCtx = this.getApplicationContext();

        Utils.makeStatusNotification("Blurring Image!!!", appCtx);
        Utils.delay(Utils.DELAY_TIME_MILLIS);

        String resUri = getInputData().getString(Utils.THE_SELECTED_IMAGE);

        if (TextUtils.isEmpty(resUri)) {
            Utils.info(this, "Invalid input uri...");
            Utils.makeStatusNotification("Invalid input uri...", appCtx);
            return Result.failure();
        }

        try {
            Bitmap picture = BitmapFactory.decodeStream(appCtx.getContentResolver().openInputStream(Uri.parse(resUri)));

            // Blur the bitmap
            Bitmap output = Utils.blurBitmap(picture, appCtx);

            // Write bitmap to a temp file and show notification
            Uri outputUri = Utils.writeBitmapToFile(appCtx, output);
            Utils.makeStatusNotification("Output: " + outputUri.toString(), appCtx);

            // Prepare Result Data to pass
            Data outputData = new Data.Builder()
                    .putString(Utils.THE_SELECTED_IMAGE, outputUri.toString())
                    .build();

            return Result.success(outputData);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

}
