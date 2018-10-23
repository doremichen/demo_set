package com.adam.app.demoset.biner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;

public class DemoBinderAct extends AppCompatActivity {

    private boolean isMessenger;

    private EditText mETInputA;
    private EditText mETInputB;
    private TextView mTVOutputC;

    // Messenger proxy
    private Messenger mMessenger;

    private ServiceConnection mMessengerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.inFo(this, "onServiceConnected");
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.inFo(this, "onServiceDisconnected");
            mMessenger = null;
        }
    };

    // Aidl proxy
    private IMyAidlInterface mProxyAidl;

    // Aidl connection
    private ServiceConnection mAidlConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.inFo(this, "onServiceConnected");
            mProxyAidl = IMyAidlInterface.Stub.asInterface(service);
            // register call back
            try {
                mProxyAidl.registerServiceCB(mAidlCB);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.inFo(this, "onServiceDisconnected");
            // register call back
            try {
                mProxyAidl.unregisterServiceCB(mAidlCB);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mProxyAidl = null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.inFo(this, "onCreate enter");
        setContentView(R.layout.activity_demo_binder);

        mETInputA = this.findViewById(R.id.et_input_a);
        mETInputB = this.findViewById(R.id.et_input_b);
        mTVOutputC = this.findViewById(R.id.tv_output_c);


        // Binde aidl service
        binderMyService(this, MyAidlService.class, mAidlConn);

        // Binder messenger service
        binderMyService(this, MyMessengerService.class, mMessengerConn);

    }

    private void binderMyService(Context context, Class<?> target, ServiceConnection connection) {
        Utils.inFo(this, "binderMyService enter");
        Intent intent = new Intent(context, target);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.inFo(this, "onDestroy enter");

        // Unregister callback
        try {
            mProxyAidl.unregisterServiceCB(mAidlCB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Unbinder aidl service
        this.unbindService(mAidlConn);

        // Unbinder messenger service
        this.unbindService(mMessengerConn);

        mProxyAidl = null;
        mMessenger = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_menu_biner, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aidl_config:
                Utils.showToast(this, "AIDL is configured.");
                isMessenger = false;
                return true;
            case R.id.messenger_config:
                Utils.showToast(this, "Messenger is configured.");
                isMessenger = true;
                return true;
            case R.id.exit:
                this.finish();
                return true;
        }

        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Utils.inFo(this, "onTouchEvent enter");
        View view = this.getCurrentFocus();
        Utils.hideSoftKeyBoardFrom(this, view);
        return true;
    }

    public void onExecuteBinderCall(View v) {
        Utils.inFo(this, "onExecuteBinderCall enter");

        // Check if the text is valid
        if (mETInputA.getText() == null || mETInputB.getText() == null) {
            Utils.showToast(this, "please input the valid number.");
            return;
        }

        // Check if the text is valid
        if (mETInputA.getText().toString().equals("")|| mETInputB.getText().toString().equals("")) {
            Utils.showToast(this, "please input the valid number.");
            return;
        }

        // Hide soft keyboard
        Utils.hideSoftKeyBoardFrom(this, v);

        try {
            // Get value from edit text
            int a = Integer.parseInt(mETInputA.getText().toString());
            int b = Integer.parseInt(mETInputB.getText().toString());
            Utils.inFo(this,"a = " + a);
            Utils.inFo(this,"b = " + b);

            if (isMessenger) {
                Utils.showToast(this, "Messenger binder call");
                Utils.inFo(this, "Messenger binder call");
                try {
                    // Get message
                    Message msg = Message.obtain();
                    msg.what = MyMessengerService.ACTION_ADD;
                    msg.arg1 = a;
                    msg.arg2 = b;
                    msg.replyTo = mUIMessenger;
                    // Send message to the remote service by service proxy
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            } else {
                Utils.showToast(this, "AIDL binder call");
                Utils.inFo(this, "AIDL binder call");
                try {
                    mProxyAidl.add(a, b);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        } catch (NumberFormatException ex) {

            Utils.showToast(this, "please input the value between the integer range!!!");

        } finally {

        }

    }

    public void showResult(int value) {
        Utils.inFo(this, "showResult");
        if (value == -1) {
            Utils.showToast(this, "The result is overflow!!!");
        }

        mTVOutputC.setText("c: " + String.valueOf(value));
    }

    /**
     * This is aidl callback
     */
    private AidlServiceCB mAidlCB = new AidlServiceCB(this);
    private static class AidlServiceCB extends IMyAidlCBInterface.Stub {

        private WeakReference<DemoBinderAct> mRef_act;

        public AidlServiceCB(DemoBinderAct activity) {
            Utils.inFo(this, "constructor enter");
            mRef_act = new WeakReference<DemoBinderAct>(activity);
        }

        @Override
        public void result(int c) throws RemoteException {
            Utils.inFo(this, "result");
            mRef_act.get().showResult(c);
        }
    }

    /**
     * This is messenger callback
     */
    private Messenger mUIMessenger = new Messenger(new CallBackHandler(this));
    private static class CallBackHandler extends Handler {

        private WeakReference<DemoBinderAct> mRef_act;

        public CallBackHandler(DemoBinderAct act) {
            mRef_act = new WeakReference<DemoBinderAct>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.inFo(this, "UI handler");
            int flag = msg.what;

            if (flag == MyMessengerService.ACTION_REPLY_RESULT) {
                int result = msg.arg1;
                mRef_act.get().showResult(result);
            }
        }
    }
}
