package com.adam.app.demoset.workmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.Utils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveToFileWorker extends Worker {

    private static final String TITLE = "Blurred Image";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());

    public SaveToFileWorker(@NonNull Context context,
                            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork enter");
        Context appCtx = getApplicationContext();

        Utils.makeStatusNotification("Saving data!!!", appCtx);
        Utils.delay(Utils.DELAY_TIME_MILLIS);

        // get resolver
        ContentResolver resolver = appCtx.getContentResolver();

        String imageUri = getInputData().getString(Utils.THE_SELECTED_IMAGE);
        Utils.info(this, "imageUri = " + imageUri);
        try {
            Bitmap picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(imageUri)));

            String outputUri = MediaStore.Images.Media.insertImage(
                    resolver, picture, TITLE, DATE_FORMAT.format(new Date())
            );

            Utils.info(this, "outputUri = " + outputUri);
            // Check exists
            if (TextUtils.isEmpty(outputUri)) {
                return Result.failure();
            }

            // Return output for the temp file
            Data outputData = new Data.Builder()
                    .putString(Utils.THE_SELECTED_IMAGE, outputUri.toString())
                    .build();
            Utils.makeStatusNotification("Save success", appCtx);
            return Result.success(outputData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.makeStatusNotification("File no found", appCtx);
        }
        return Result.failure();
    }
}
