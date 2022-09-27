/**
 * Flash light work
 */
package com.adam.app.demoset.flashlight;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adam.app.demoset.Utils;

public class FlashLightWork extends Worker {

    public static final String KEY_ON = "key.on";

    private CameraManager mCameraManger;

    public FlashLightWork(@NonNull Context context,
                          @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.mCameraManger = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * Flash light process
     * @return
     */
    @NonNull
    @Override
    public Result doWork() {
        Utils.info(this, "doWork");
        // get data from UI
        boolean state = this.getInputData().getBoolean(KEY_ON, false);
        Utils.info(this, "flash light state: " + state);
        boolean isException = false;
        try {
            String cameraId = this.mCameraManger.getCameraIdList()[0];
            this.mCameraManger.setTorchMode(cameraId, state);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            isException = true;
        }
        return (isException == false)? Result.success(): Result.failure();
    }
}
