/**
 * Utility function
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */
package com.adam.app.demoset;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public abstract class Utils {

    public static final String ITEM_START_SERVICE = "start service";
    public static final String ITEM_STOP_SERVICE = "stop service";
    public static final String ITEM_BIND_SERVICE = "bind service";
    public static final String ITEM_UNBIND_SERVICE = "unbind service";
    public static final String ITEM_SERVICE_REQUEST = "service request";
    public static final String ACTION_SHOW_SNACKBAR = "show snackbar";
    public static final String KEY_MSG = "service status";
    private static final String TAG = "DemoSet";
    public static boolean sIsRemoteService = false;
    public static boolean sIsBound = false;

    public static LocalService sLocalSvr;
    public static Messenger sMessenger;   // for remote service

    public static ServiceConnection sConnection;

    public static void inFo(Object obj, String str) {
        Log.i(TAG, obj.getClass().getSimpleName() + ": " + str);
    }

    public static void inFo(Class<?> clazz, String str) {
        Log.i(TAG, clazz.getSimpleName() + ": " + str);
    }


    public static void showSnackBar(Context context, String message) {
        Intent intent = new Intent(ACTION_SHOW_SNACKBAR);
        if (!TextUtils.isEmpty(message)) {
            intent.putExtra(KEY_MSG, context.getClass().getSimpleName() + " " + message);
        }
        context.sendBroadcast(intent);

    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, context.getClass().getSimpleName() + " " + message, Toast.LENGTH_SHORT).show();
    }
}
