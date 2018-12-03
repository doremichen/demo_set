/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;

import java.io.FileNotFoundException;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWork extends Worker {

    public MyWork(@NonNull Context context,
                  @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.inFo(this, "doWork enter");
        try {
            Context appContext = this.getApplicationContext();

            // Get image from data object
            String imgUri = getInputData().getString(Utils.THE_SELECTED_IMAGE);

            // Check if image exists or not
            if (TextUtils.isEmpty(imgUri)) {
                throw new IllegalArgumentException("Invalid input imgUri");
            }

            ContentResolver resolver = appContext.getContentResolver();
            // Create a bitmap
            Bitmap picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(imgUri)));

            // Blur image
            Bitmap output = Utils.blurBitmap(picture, appContext);

            // Write image to temp file
            Uri uriOutput = Utils.writeBitmapToFile(appContext, output);

            // Return ouptput for the temp file
            setOutputData(new Data.Builder().putString(Utils.THE_SELECTED_IMAGE, uriOutput.toString()).build());

            // Notification
            Utils.makeStatusNotification("output: " + uriOutput.toString(), appContext);

            return Result.SUCCESS;
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return Result.FAILURE;
        } catch (Exception e) {
            e.printStackTrace();

            return Result.FAILURE;
        }


    }

}
