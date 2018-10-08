package com.adam.app.demoset.jnidemo;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoJNIAct extends AppCompatActivity implements NativeUtils.INative {

    private TextView mShow;
    private ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_jni);
        mLayout = (ConstraintLayout)findViewById(R.id.demo_jni_layout);
        mShow = (TextView)this.findViewById(R.id.tv_show_jni);

        NativeUtils instance = NativeUtils.Helper.getInstance();
        instance.setNativeInterface(this);

        // Start thread to check update status
        new Thread(new Runnable() {

            private NativeUtils instance = NativeUtils.Helper.getInstance();

            @Override
            public void run() {
                while (instance.getData().equals("unChange")) {
                    ;
                }

                // Show update data stats
                DemoJNIAct.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(mLayout, "dataFromNative: " + instance.getData(),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ;
                                    }
                                })
                                .show();
                    }
                });
            }
        }).start();
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
                this.finish();
                return true;
        }

        return false;
    }

    public void onInvokeJNI(View v) {
        String info = NativeUtils.Helper.getInstance().sayHello();
        mShow.setText("JNI info: " + info);


    }

    @Override
    public void onNotify(String str) {
        Utils.inFo(this, "onNotify enter");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jni Demo");
        builder.setMessage("Info: " + str);
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();


    }
}
