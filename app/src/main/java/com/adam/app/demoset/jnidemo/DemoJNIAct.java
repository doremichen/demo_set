/**
 * This is JNI demo UI
 */
package com.adam.app.demoset.jnidemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;

import java.lang.ref.WeakReference;

public class DemoJNIAct extends AppCompatActivity {

    private TextView mShow;
    private ConstraintLayout mLayout;

    private static final int ACTION_UPDATE_OBJECTCALLBACK = 0;
    private static final int ACTION_UPDATE_CLAZZCALLBACK = 1;


    /**
     * Ui handler
     */
    private static class UIHanlder extends Handler {

        private WeakReference<DemoJNIAct> mReference;

        public UIHanlder(@NonNull DemoJNIAct act) {
            mReference = new WeakReference<DemoJNIAct>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case ACTION_UPDATE_OBJECTCALLBACK: {
                    Bundle bundle = msg.getData();
                    String data = bundle.getString(KEY_DATA);
                    String method = bundle.getString(KEY_METHOD);
                    mReference.get().showDialog(data, method);
                }
                break;
                case ACTION_UPDATE_CLAZZCALLBACK: {
                    Bundle bundle = msg.getData();
                    boolean data = bundle.getBoolean(KEY_DATA);
                    String method = bundle.getString(KEY_METHOD);
                    mReference.get().showDialog(data, method);
                }
                break;
            }
        }
    }

    private static UIHanlder mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_jni);
        mLayout = (ConstraintLayout) findViewById(R.id.demo_jni_layout);
        mShow = (TextView) this.findViewById(R.id.tv_show_jni);

        mHandler = new UIHanlder(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_only_exit_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.demo_bt_exit:
                // clear data
                NativeUtils.getInstance().clearObjData();
                NativeUtils.clearClazzData();

                this.finish();
                return true;
        }

        return false;
    }

    /**
     * The button call back method
     * @param v
     */
    public void onInvokeJNI(View v) {
        String info = NativeUtils.getInstance().sayHello();
        mShow.setText("JNI info: " + info);


    }

    /**
     * The button call back method
     * @param v
     */
    public void onObjectCB(View v) {
        NativeUtils.getInstance().objectCallBack();
    }

    /**
     * The button call back method
     * @param v
     */
    public void onClazzCB(View v) {
        NativeUtils.classCallBack();
    }

    /**
     * Alert dialog for object call back
     * @param data
     * @param str
     */
    public void showDialog(String data, String str) {
        // Show alertDialog
        StringBuilder stb = new StringBuilder();
        stb.append("Data: " + data + "\n");
        stb.append("Method: " + str + "\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Object CallBack");
        builder.setMessage(stb.toString());
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    /**
     * Alert dialog for clazz call back
     * @param data
     * @param str
     */
    public void showDialog(boolean data, String str) {
        // Show alertDialog
        StringBuilder stb = new StringBuilder();
        stb.append("Data: " + String.valueOf(data) + "\n");
        stb.append("Method: " + str + "\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clazz CallBack");
        builder.setMessage(stb.toString());
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    /**
     * Bundle key
     */
    private static final String KEY_DATA = "key.data";
    private static final String KEY_METHOD = "key.method";

    /**
     * Put data in the ui message queue
     * @param data
     * @param str
     */
    public static void notifyUI(String data, String str) {
        Message message = mHandler.obtainMessage();
        message.what = ACTION_UPDATE_OBJECTCALLBACK;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DATA, data);
        bundle.putString(KEY_METHOD, str);
        message.setData(bundle);
        // Put message in ui queue
        mHandler.sendMessage(message);
    }

    /**
     * Put data in the ui message queue
     * @param data
     * @param str
     */
    public static void notifyUI(boolean data, String str) {
        Message message = mHandler.obtainMessage();
        message.what = ACTION_UPDATE_CLAZZCALLBACK;
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_DATA, data);
        bundle.putString(KEY_METHOD, str);
        message.setData(bundle);
        // Put message in ui queue
        mHandler.sendMessage(message);
    }


}