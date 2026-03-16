/**
 * Copyright (C) 2019 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is the demo jni activity
 * </p>
 * <p>
 * Author: Adam Chen
 * Date: 2018/10/08
 */
package com.adam.app.demoset.jnidemo.legacy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.jnidemo.NativeUtils;

import java.lang.ref.WeakReference;

public class DemoJNIAct extends AppCompatActivity {

    private static final int ACTION_UPDATE_OBJECT_CALLBACK = 0;
    private static final int ACTION_UPDATE_CLAZZ_CALLBACK = 1;
    /**
     * Bundle key
     */
    private static final String KEY_DATA = "key.data";
    private static final String KEY_METHOD = "key.method";
    private static UIHandler mHandler;
    private TextView mShow;

    /**
     * Put data in the ui message queue
     *
     * @param data
     * @param str
     */
    public static void notifyUI(String data, String str) {
        Message message = mHandler.obtainMessage();
        message.what = ACTION_UPDATE_OBJECT_CALLBACK;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DATA, data);
        bundle.putString(KEY_METHOD, str);
        message.setData(bundle);
        // Put message in ui queue
        mHandler.sendMessage(message);
    }

    /**
     * Put data in the ui message queue
     *
     * @param data
     * @param str
     */
    public static void notifyUI(boolean data, String str) {
        Message message = mHandler.obtainMessage();
        message.what = ACTION_UPDATE_CLAZZ_CALLBACK;
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_DATA, data);
        bundle.putString(KEY_METHOD, str);
        message.setData(bundle);
        // Put message in ui queue
        mHandler.sendMessage(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legacy_demo_jni);
        mShow = this.findViewById(R.id.tv_show_jni);

        mHandler = new UIHandler(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_exit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_exit:
                // clear data
                NativeUtils.newInstance().clearObjData();
                NativeUtils.clearClazzData();

                this.finish();
                return true;
        }

        return false;
    }

    /**
     * The button call back method
     *
     * @param v
     */
    public void onInvokeJNI(View v) {
        String info = NativeUtils.newInstance().sayHello();
        mShow.setText(getString(R.string.demo_jni_info, info));


    }

    /**
     * The button call back method
     *
     * @param v
     */
    public void onObjectCB(View v) {
        NativeUtils.newInstance().objectCallBack();
    }

    /**
     * The button call back method
     *
     * @param v
     */
    public void onClazzCB(View v) {
        NativeUtils.classCallBack();
    }

    /**
     * Show dialog
     * @param title dialog title
     * @param data dialog data
     * @param str dialog method
     * @param <T> data type
     */
    public <T> void showDialog(String title, T data, String str) {
        String dataLabel = "Data: ";
        String methodLabel = "Method: ";
        String okBtnLabel = getString(R.string.label_ok_btn);

        StringBuilder stb = new StringBuilder();
        stb.append(dataLabel + data + "\n");
        stb.append(methodLabel + str + "\n");
        // AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(stb.toString());
        builder.setCancelable(false);
        // set positive button
        builder.setPositiveButton(okBtnLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // no action
            }
        });
        // show
        builder.create().show();
    }

    /**
     * Ui handler
     */
    private static class UIHandler extends Handler {

        private WeakReference<DemoJNIAct> mReference;

        public UIHandler(@NonNull DemoJNIAct act) {
            mReference = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ACTION_UPDATE_OBJECT_CALLBACK: {
                    String title = mReference.get().getString(R.string.label_object_call_back);
                    Bundle bundle = msg.getData();
                    String data = bundle.getString(KEY_DATA);
                    String method = bundle.getString(KEY_METHOD);
                    mReference.get().showDialog(title, data, method);
                }
                break;
                case ACTION_UPDATE_CLAZZ_CALLBACK: {
                    String title = mReference.get().getString(R.string.label_clazz_call_back);
                    Bundle bundle = msg.getData();
                    boolean data = bundle.getBoolean(KEY_DATA);
                    String method = bundle.getString(KEY_METHOD);
                    mReference.get().showDialog(title, data, method);
                }
                break;
            }
        }
    }


}
