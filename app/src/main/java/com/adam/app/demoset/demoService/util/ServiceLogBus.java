/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the service log bus util.
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/11
 */
package com.adam.app.demoset.demoService.util;

import android.content.Context;
import android.content.Intent;

public final class ServiceLogBus {

    private ServiceLogBus() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static final String ACTION_LOG =
            "com.adam.service.LOG";

    public static final String KEY_MSG = "msg";

    public static void send(Context ctx, String msg) {
        Intent intent = new Intent(ACTION_LOG);
        intent.putExtra(KEY_MSG, msg);
        ctx.sendBroadcast(intent);
    }
}
