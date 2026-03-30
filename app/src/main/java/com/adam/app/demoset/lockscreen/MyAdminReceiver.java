/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the demo device admin receiver.
 * </p>
 * Author: Adam Chen
 * Date: 2018/10/11
 */
package com.adam.app.demoset.lockscreen;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.adam.app.demoset.utils.Utils;

public class MyAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Utils.info(this, "My device admin: Enable...");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Utils.info(this, "My device admin: Disable...");
    }
}
