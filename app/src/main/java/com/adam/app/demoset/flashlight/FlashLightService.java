package com.adam.app.demoset.flashlight;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.adam.app.demoset.Utils;

public class FlashLightService extends JobIntentService {

    private static final int JOB_ID = 0x0316;
    private static final String KEY_COMMEND = "key.commend";

    public static final String CMD_FLASH_LIGHT_ON = "flash light on";
    public static final String CMD_FLASH_LIGHT_OFF = "flash light off";


    public static void execute(Context context, String cmd) {
        Intent work = new Intent();
        work.putExtra(KEY_COMMEND, cmd);
        JobIntentService.enqueueWork(context, FlashLightService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Utils.inFo(this, "onHandleWork enter");

        String cmd = intent.getStringExtra(KEY_COMMEND);

        if (CMD_FLASH_LIGHT_ON.equals(cmd)) {

            enableFlash(true);

        } else if (CMD_FLASH_LIGHT_OFF.equals(cmd)) {

            enableFlash(false);

        }


    }


    private void enableFlash(boolean on) {
        Utils.inFo(this, "startFlash enter");
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = manager.getCameraIdList()[0];
            manager.setTorchMode(cameraId, on);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

}
