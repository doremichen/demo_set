/**
 * Demo Bind service
 */
package com.adam.app.demoset.binder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
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

    private final ServiceConnection mMessengerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.info(this, "onServiceConnected");
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.info(this, "onServiceDisconnected");
            mMessenger = null;
        }
    };

    // Aidl proxy
    private IMyAidlInterface mProxyAidl;

    // Aidl connection
    private final ServiceConnection mAidlConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Utils.info(this, "onServiceConnected");
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
            Utils.info(this, "onServiceDisconnected");
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
        Utils.info(this, "onCreate enter");
        setContentView(R.layout.activity_demo_binder);

        mETInputA = this.findViewById(R.id.et_input_a);
        mETInputB = this.findViewById(R.id.et_input_b);
        mTVOutputC = this.findViewById(R.id.tv_output_c);


        // Bind aidl service
        binderMyService(this, MyAidlService.class, mAidlConn);

        // Bind messenger service
        binderMyService(this, MyMessengerService.class, mMessengerConn);

    }

    private void binderMyService(Context context, Class<?> target, ServiceConnection connection) {
        Utils.info(this, "binderMyService enter");
        Intent intent = new Intent(context, target);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy enter");

        // Unregister callback
        try {
            mProxyAidl.unregisterServiceCB(mAidlCB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Unbind aidl service
        this.unbindService(mAidlConn);

        // Unbind messenger service
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
        // clear input/text data
        this.mETInputA.setText("");
        this.mETInputB.setText("");
        this.mTVOutputC.setText("c:");

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
        Utils.info(this, "onTouchEvent enter");
        View view = this.getCurrentFocus();
        Utils.hideSoftKeyBoardFrom(this, view);
        return true;
    }

    public void onExecuteBinderCall(View v) {
        Utils.info(this, "onExecuteBinderCall enter");

        if (!areAllInputsValid(mETInputA, mETInputB)) {
            Utils.showToast(this, "Please enter numbers!"); // More user-friendly message
            return;
        }

        Utils.hideSoftKeyBoardFrom(this, v);

        try {
            int inputA = Integer.parseInt(mETInputA.getText().toString());
            int inputB = Integer.parseInt(mETInputB.getText().toString());
            Utils.info(this, "inputA = " + inputA); // More descriptive variable names
            Utils.info(this, "inputB = " + inputB);

            if (isMessenger) {
                executeMessengerBinderCall(inputA, inputB);
            } else {
                executeAidlBinderCall(inputA, inputB);
            }
        } catch (NumberFormatException ex) {
            Utils.showToast(this, "Please enter values within the integer range!"); // Clearer error message
        }
    }

    /**
     * Checks if all the provided EditText views contain non-empty text.
     * @Params:
     * editTexts - The EditText views to validate.
     * @Returns:
     * True if all EditTexts contain non-empty text, false otherwise.
     */
    private boolean areAllInputsValid(EditText ... editTexts) {
        for (EditText editText: editTexts) {
            if (TextUtils.isEmpty(editText.getText())) {
                return false;
            }
        }
        return true;
    }

    private void executeMessengerBinderCall(int a, int b) {
        Utils.showToast(this, "Messenger binder call");
        Utils.info(this, "Messenger binder call");
        try {
            Message msg = Message.obtain();
            msg.what = MyMessengerService.ACTION_ADD;
            msg.arg1 = a;
            msg.arg2 = b;
            msg.replyTo = mUIMessenger;
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void executeAidlBinderCall(int a, int b) {
        Utils.showToast(this, "AIDL binder call");
        Utils.info(this, "AIDL binder call");
        try {
            mProxyAidl.add(a, b);
            MyBinderData data = new MyBinderData("Binder data");
            mProxyAidl.sendRequest(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void showResult(int value) {
        Utils.info(this, "showResult");
        if (value == -1) {
            Utils.showToast(this, "The result is overflow!!!");
        }

        mTVOutputC.setText("c: " + value);
    }

    /**
     * This is aidl callback
     */
    private AidlServiceCB mAidlCB = new AidlServiceCB(this);

    private static class AidlServiceCB extends IMyAidlCBInterface.Stub {

        private WeakReference<DemoBinderAct> mRef_act;

        public AidlServiceCB(DemoBinderAct activity) {
            Utils.info(this, "constructor enter");
            mRef_act = new WeakReference<DemoBinderAct>(activity);
        }

        @Override
        public void result(int c) throws RemoteException {
            Utils.info(this, "result");
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
            Utils.info(this, "UI handler");
            int flag = msg.what;

            if (flag == MyMessengerService.ACTION_REPLY_RESULT) {
                int result = msg.arg1;
                mRef_act.get().showResult(result);
            }
        }
    }
}
