/**
 * Utility function
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */
package com.adam.app.demoset;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public abstract class Utils {

    public static final String ITEM_START_SERVICE = "start service";
    public static final String ITEM_STOP_SERVICE = "stop service";
    public static final String ITEM_BIND_SERVICE = "bind service";
    public static final String ITEM_UNBIND_SERVICE = "unbind service";
    public static final String ITEM_SERVICE_REQUEST = "service request";
    public static final String ITEM_EXIT = "exit";
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

    /**
     * When the activity use this method, it must override onRequestPermissionsResult method that
     * would receive the response of the client request permission.
     *
     * @param context
     * @param permission
     * @param requestcode
     * @return
     */
    public static boolean askPermission(final Activity context, @NonNull final String permission, final int requestcode) {
        boolean ret = false;
        if (ContextCompat.checkSelfPermission(context,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    permission)) {

                AlertDialog.Builder builder = buildAlertDialog(context, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(context,
                                new String[]{permission},
                                requestcode);
                    }
                });

                builder.create().show();

            } else {
                ActivityCompat.requestPermissions(context,
                        new String[]{permission},
                        requestcode);
            }
        } else {
            ret = true;
            // Permission granted
        }

        return ret;
    }


    public static boolean askPermission(final Activity context, @NonNull final String[] permissions, final int requestcode) {
        Utils.inFo(Utils.class, "askPermission enter");
        boolean ret = false;
        int allowNum = 0;
        boolean showDlg = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                        permission)) {
                    showDlg = true;
                    break;
                }
            } else {
                allowNum++;
            }
        }

        if (allowNum == permissions.length) {
            // Permission granted
            ret = true;
        } else {
            if (showDlg) {

                AlertDialog.Builder builder = buildAlertDialog(context, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(context,
                                permissions,
                                requestcode);
                    }
                });

                builder.create().show();
            } else {
                ActivityCompat.requestPermissions(context,
                        permissions,
                        requestcode);
            }

        }
        Utils.inFo(Utils.class, "ret = " + ret);

        return ret;
    }

    private static AlertDialog.Builder buildAlertDialog(Context context, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rational permission request");
        builder.setMessage("Please press ok to request permission.");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", listener);

        return builder;
    }


    /**
     * There must be create receiver in the owner activity and show snack info
     *
     * @param context
     * @param message
     */
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

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info:");
        builder.setMessage(msg);
        builder.setPositiveButton(context.getResources().getString(R.string.label_ok_btn),
                listener);

        builder.create().show();
    }

    public static void hideSoftKeyBoardFrom(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
