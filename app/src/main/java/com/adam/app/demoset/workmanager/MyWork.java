/**
 * Reference: https://codelabs.developers.google.com/codelabs/android-workmanager/#0
 */
package com.adam.app.demoset.workmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.io.FileNotFoundException;

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
        try {
            Context appContext = this.getApplicationContext();

            // Decode picture by BitmapFactory
            Bitmap pic = BitmapFactory.decodeResource(appContext.getResources(),
                    R.drawable.test);

            // Blur image
            Bitmap output = Utils.blurBitmap(pic, appContext);

            // Write image to temp file
            Uri uriOutput = Utils.writeBitmapToFile(appContext, output);

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
