package com.adam.app.demoset.workmanager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.adam.app.demoset.Utils;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SaveToFileWorker extends Worker {

    private static final String TITLE = "Blureed Image";
    private static final SimpleDateFormat DATE_FORMATE =
            new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());

    public SaveToFileWorker(@NonNull Context context,
                            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.inFo(this, "doWork enter");
        Context appContext = getApplicationContext();

        // get resolver
        ContentResolver resolver = appContext.getContentResolver();

        String imageUri = getInputData().getString(Utils.THE_SELECTED_IMAGE);
        Utils.inFo(this, "imageUri = " + imageUri);
        try {
            Bitmap map = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(imageUri)));
            String saveUri = MediaStore.Images.Media.insertImage(
                    resolver, map, TITLE, DATE_FORMATE.format(new Date())
            );

            Utils.inFo(this, "saveUri = " + saveUri);
            // Check exists
            if (TextUtils.isEmpty(saveUri)) {
                return Result.FAILURE;
            }

            // Return ouptput for the temp file
            setOutputData(new Data.Builder().putString(Utils.THE_SELECTED_IMAGE, saveUri).build());
            Utils.makeStatusNotification("Save success", appContext);
            return Result.SUCCESS;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.makeStatusNotification("File no found", appContext);
            return Result.FAILURE;
        }
    }
}
