package com.adam.app.demoset.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adam.app.demoset.Utils;

public class MyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.showToast(context, "The message is from alarm");
        Intent it = new Intent(DemoAlarmAct.ACTION_UPDATE_INFO);
        context.sendBroadcast(it);
    }
}
