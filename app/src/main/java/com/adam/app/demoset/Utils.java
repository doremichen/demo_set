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
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Messenger;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Utils {

    public static final String ITEM_START_SERVICE = "start service";
    public static final String ITEM_STOP_SERVICE = "stop service";
    public static final String ITEM_BIND_SERVICE = "bind service";
    public static final String ITEM_UNBIND_SERVICE = "unbind service";
    public static final String ITEM_SERVICE_REQUEST = "service request";
    public static final String ITEM_EXIT = "exit";
    public static final String ACTION_SHOW_SNACKBAR = "show snackbar";
    public static final String KEY_MSG = "service status";
    public static final String THE_SELECTED_IMAGE = "The selected image";
    private static final String TAG = "DemoSet";
    public static final String TRUE = "True";
    public static final String FALSE = "False";
    public static final String NOTIFY_CHANNEL_ID = "0x1357";

    public static boolean sIsRemoteService = false;
    public static boolean sIsBound = false;

    public static final String OUTPUT_PATH = "blur_filter_outputs";
    public static final String TAG_IMG_OUTPUT = "OUTPUT";
    // The name of the image manipulation work
    public static final String IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work";

    public static LocalService sLocalSvr;
    public static Messenger sMessenger;   // for remote service

    public static ServiceConnection sConnection;

    public static volatile String sImagePath;

    private static final long DELAY_TIME_MILLIS = 3000L;

    // reference: https://github.com/orhanobut/logger/tree/master
    static {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().
                tag(TAG).
                build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }


    public static void info(Object obj, String str) {
        Log.i(TAG, obj.getClass().getSimpleName() + ": " + str);
        //Logger.i(obj.getClass().getSimpleName() + ": " + str);
    }

    public static void info(Class<?> clazz, String str) {
        Log.i(TAG, clazz.getSimpleName() + ": " + str);
        //Logger.i(clazz.getSimpleName() + ": " + str);
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
        info(Utils.class, "[askPermission] +++");
        // check permission state
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            info(Utils.class, "permission granted!!!");
            info(Utils.class, "[askPermission] xxx");
            return true;
        }

        // request permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                permission)) {
            info(Utils.class, "should show request dialog!!!");
            // show Alert dialog
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
            info(Utils.class, "do not need to show request dialog!!!");
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    requestcode);
        }

        info(Utils.class, "[askPermission] xxx");
        return false;
    }

    public static boolean askPermission(final Activity context, @NonNull final String[] permissions, final int requestcode) {
        Utils.info(Utils.class, "askPermission enter");
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
        Utils.info(Utils.class, "ret = " + ret);

        return ret;
    }

    private static AlertDialog.Builder buildAlertDialog(Context context, DialogInterface.OnClickListener listener) {
        StringBuilder stb = new StringBuilder();
        stb.append("Please press ok to request permission again.\n");
        stb.append("Important: Do not ask permission again if you still press not allow.\n");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rational permission request");
        builder.setMessage(stb.toString());
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

    public static void showCustomizedToast(Context context, String message) {
        // create customized toast view
        View toastView = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView toastText = toastView.findViewById(R.id.toast_text);
        toastText.setText(message);

        // show
        Toast customToast = new Toast(context);
        customToast.setView(toastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER, 0, 0);
        customToast.show();
    }

    public static void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info:");
        builder.setMessage(msg);
        builder.setPositiveButton(context.getResources().getString(R.string.label_ok_btn),
                listener);

        builder.create().show();
    }

    public static void showAlertDialog(Context context,
                                       String msg,
                                       DialogInterface.OnClickListener listener1,
                                       DialogInterface.OnClickListener listener2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = LayoutInflater.inflate(R.layout.layout_alert_dialog, null);
        TextView message = dialogView.findViewById(R.id.dlg_message);
        message.setText(msg);

        builder.setTitle("Info:");
        builder.setView(dialogView);
        builder.setNegativeButton(context.getString(R.string.label_off),
                listener1);
        builder.setPositiveButton(context.getString(R.string.label_on),
                listener2);

        builder.create().show();
    }

    public static AlertDialog showProgressDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = LayoutInflater.inflate(R.layout.dialog_progress, null);
        TextView info = dialogView.findViewById(R.id.progess_dlg_info);
        info.setText(msg);
        builder.setTitle("Info:");
        builder.setView(dialogView);
        return builder.create();
    }

    public static void hideSoftKeyBoardFrom(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideSystemUI(View v) {
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void enableLog(String enable, String path) {
        Utils.info(Utils.class, "enableLog enter");
        if (TRUE.equals(enable)) {
            Utils.info(Utils.class, "true");
            // Clear logcat
            executeCommend("logcat -c");
            // Log file
            executeCommend("logcat -f " + path);

        } else {
            Utils.info(Utils.class, "false");
            // Dump log and exits
            executeCommend("logcat -d");
        }
    }


    private static void executeCommend(String cmd) {
        int pid = android.os.Process.myPid();
        Utils.info(Utils.class, "pid: " + pid);

        ExecutorService executorSvr = Executors.newSingleThreadExecutor();
        executorSvr.execute(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {
                    process = Runtime.getRuntime().exec(cmd);
                    String inStr = consumeInputStream(process.getInputStream());
                    String errStr = consumeInputStream(process.getErrorStream());

                    int exitCode = process.waitFor();
                    Utils.info(Utils.class, "cmd: " + cmd + " " + "exitCode: " + String.valueOf(exitCode));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (process != null) {
                        process.destroy();
                    }
                }
            }
        });

        executorSvr.shutdown();

    }


    private static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s ;
        StringBuilder sb = new StringBuilder();
        while((s=br.readLine())!=null){
            System.out.println(s);
            sb.append(s);
        }
        return sb.toString();
    }



    /**
     * Blurs the given Bitmap image
     *
     * @param bitmap             Image to blur
     * @param applicationContext Application context
     * @return Blurred bitmap image
     */
    @WorkerThread
    public static Bitmap blurBitmap(@NonNull Bitmap bitmap,
                                    @NonNull Context applicationContext) {

        RenderScript rsContext = null;
        try {

            // Create the output bitmap
            Bitmap output = Bitmap.createBitmap(
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

            // Blur the image
            rsContext = RenderScript.create(applicationContext, RenderScript.ContextType.DEBUG);
            Allocation inAlloc = Allocation.createFromBitmap(rsContext, bitmap);
            Allocation outAlloc = Allocation.createTyped(rsContext, inAlloc.getType());
            ScriptIntrinsicBlur theIntrinsic =
                    ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext));
            theIntrinsic.setRadius(10.f);
            theIntrinsic.setInput(inAlloc);
            theIntrinsic.forEach(outAlloc);
            outAlloc.copyTo(output);

            return output;
        } finally {
            if (rsContext != null) {
                rsContext.finish();
            }
        }
    }

    /**
     * Writes bitmap to a temporary file and returns the Uri for the file
     *
     * @param applicationContext Application context
     * @param bitmap             Bitmap to write to temp file
     * @return Uri for temp file with bitmap
     * @throws FileNotFoundException Throws if bitmap file cannot be found
     */
    public static Uri writeBitmapToFile(
            @NonNull Context applicationContext,
            @NonNull Bitmap bitmap) throws FileNotFoundException {

        String name = String.format("blur-filter-output-%s.png", UUID.randomUUID().toString());
        File outputDir = new File(applicationContext.getFilesDir(), OUTPUT_PATH);
        if (!outputDir.exists()) {
            outputDir.mkdirs(); // should succeed
        }
        File outputFile = new File(outputDir, name);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }

            sImagePath = outputFile.getPath();
        }
        return Uri.fromFile(outputFile);
    }

    public static int sNotifyID;

    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     * <p>
     * For this codelab, this is used to show a notification so that you know when different steps
     * of the background work chain are starting
     *
     * @param message Message shown on the notification
     * @param context Context needed to create Toast
     */
    public static void makeStatusNotification(String message, Context context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Demoset notification channel";
            String description = "This is demoset notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(NOTIFY_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0L, 1000L, 1000L});
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("DemoSet notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show the notification
        NotificationManagerCompat.from(context).notify(sNotifyID, builder.build());

        sNotifyID++;
    }

    /**
     * Method for sleeping for a fixed amount of time to emulate slower work
     */
    public static void delay() {
        try {
            Thread.sleep(DELAY_TIME_MILLIS, 0);
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    interface PARAMS {
        void needFinish();
        String onPackageName();
        String onClasName();
    }
    public static void restartMainActivity(Context ctx, @NonNull PARAMS params) {
        // precondition
        params.needFinish();

        Intent it = new Intent(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        it.setClassName(params.onPackageName(), params.onClasName());
        ctx.startActivity(it);
    }

    // High performance
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Normal
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * Used to check the validity of object.
     * @param objects
     * @return
     */
    public static boolean areAllNotNull(Object ... objects) {
        return Arrays.stream(objects).allMatch(Objects::nonNull);
    }

}
