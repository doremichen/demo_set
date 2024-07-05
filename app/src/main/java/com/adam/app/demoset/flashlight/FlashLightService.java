package com.adam.app.demoset.flashlight;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.adam.app.demoset.Utils;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Turn on/off Flash light interface
     */
    private abstract class FlashLightAction {

        public void handler() {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            try {
                String cameraId = manager.getCameraIdList()[0];
                manager.setTorchMode(cameraId, isEnabled());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        protected abstract boolean isEnabled();
    }

    private class On extends FlashLightAction {

        @Override
        protected boolean isEnabled() {
            return true;
        }
    }

    private class Off extends FlashLightAction {

        @Override
        protected boolean isEnabled() {
            return false;
        }
    }


    /**
     * Turn on/off Flash light context
     */
    private class FlashFightSwitchContext {
        private Map<String , FlashLightAction> mMap = new HashMap<>() {
            {
                put(CMD_FLASH_LIGHT_ON, new On());
                put(CMD_FLASH_LIGHT_OFF, new Off());
            }
        };

        private void process(String enabled) {
            // process flash light function
            this.mMap.get(enabled).handler();
        }

    }



    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Utils.info(this, "onHandleWork enter");

        String cmd = intent.getStringExtra(KEY_COMMEND);

        new FlashFightSwitchContext().process(cmd);

    }


}
