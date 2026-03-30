/**
 * Copyright (C) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is the demo alarm receiver.
 * <p>
 * @author Adam Chen
 * @version 1.0.0 - 20218/11/2
 */
package com.adam.app.demoset.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.adam.app.demoset.utils.Utils;

public class MyAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.info(this, "Alarm receiver is triggered...");
        PowerManager powerManager = context.getApplicationContext().getSystemService(PowerManager.class);
        assert powerManager != null;
        PowerManager.WakeLock screenLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "demoset:wakelock");

        screenLock.acquire(5000L);
        Utils.info(this, "send action o UI!!!");
        Intent it = new Intent(DemoAlarmAct.ACTION_UPDATE_INFO);
        context.sendBroadcast(it);
    }
}
