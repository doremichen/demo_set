package com.adam.app.demoset.flashlight;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.adam.app.demoset.Utils;

public class FlashLightService extends JobIntentService {

    private static final int JOB_ID = 0x0316;
    private static final String KEY_COMMEND = "key.commend";

    public static final String CMD_FLASH_LIGHT_ON = "flash light on";
    public static final String CMD_FLASH_LIGHT_OFF = "flash light off";

    private Camera mCamera;
    private android.hardware.Camera.Parameters parameters;


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

            startFlash();

        } else if (CMD_FLASH_LIGHT_OFF.equals(cmd)) {

            stopFalsh();

        }


    }


    private void startFlash() {
        Utils.inFo(this, "startFlash enter");
        mCamera = Camera.open();
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mCamera.startPreview();
    }

    private void stopFalsh() {
        Utils.inFo(this, "stopFalsh enter");
        mCamera = Camera.open();
        mCamera.stopPreview();
        mCamera.release();
    }

}
