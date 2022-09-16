package com.adam.app.demoset.lockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;


public class DemoDeviceAdminAct extends AppCompatActivity {


    private static final String ITEM_ENABLE_ADMIN = "Enale admin";
    private static final String ITEM_DISABLE_ADMIN = "Disale admin";
    private static final String ITEM_LOCK_SCREEN = "Force Lock screen";
    private static final String ITEM_SECURITY_LOCK = "Security lock";
    private static final String ITEM_EXIT = "Exit";

    private static final String[] mItems = {
            ITEM_ENABLE_ADMIN,
            ITEM_DISABLE_ADMIN,
            ITEM_LOCK_SCREEN,
            ITEM_SECURITY_LOCK,
            ITEM_EXIT,
    };
    public static final int REQUEST_ENABLE_ADMIN_CODE = 0x0224;
    public static final int REQUEST_SECURE_LOCK_CODE = 0x0112;

    private ListView mList;

    private DevicePolicyManager mDevPolicyManager;
    private ComponentName mCompName;

    private KeyguardManager mKeygaurdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_device_admin);

        mDevPolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mCompName = new ComponentName(this, MyAdminReceiver.class);

        mKeygaurdManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        mList = this.findViewById(R.id.list_demo_admin);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mItems);

        mList.setAdapter(adapter);


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Utils.showToast(DemoDeviceAdminAct.this, "item: " + item);

                if (ITEM_EXIT.equals(item)) {
                    DemoDeviceAdminAct.this.finish();
                } else if (ITEM_ENABLE_ADMIN.equals(item)) {
                    enableDeviceAdmin();
                } else if (ITEM_DISABLE_ADMIN.equals(item)) {
                    disableAdmin();
                } else if (ITEM_LOCK_SCREEN.equals(item)) {
                    lockScreen();
                } else if (ITEM_SECURITY_LOCK.equals(item)) {
                    showAuthentication();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_ADMIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Utils.showToast(this, "enable admin");
            } else {
                Utils.showToast(this, "can not enable admin");
            }
        } else if (requestCode == REQUEST_SECURE_LOCK_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Utils.showToast(this, "secure lock is success");
            } else {
                Utils.showToast(this, "secure lock is fail");
            }
        }
    }

    private void enableDeviceAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);

        if (!isAdmin) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, this.mCompName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Why need this permission");
            startActivityForResult(intent, REQUEST_ENABLE_ADMIN_CODE);
        } else {
            Utils.showToast(this, "Admin is enabled");
        }

    }

    private void disableAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);

        if (isAdmin) {
            mDevPolicyManager.removeActiveAdmin(this.mCompName);
        } else {
            Utils.showToast(this, "No admin enable");
        }
    }

    private void lockScreen() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);

        if (isAdmin) {
            mDevPolicyManager.lockNow();
        } else {
            Utils.showToast(this, "No admin enable");
        }
    }

    private void showAuthentication() {
        // check if secure lock exists?
        if (mKeygaurdManager.isKeyguardSecure()) {
            Intent intent = mKeygaurdManager.createConfirmDeviceCredentialIntent("cust secure lock",
                    "This is demo");
            if (intent != null) {
                this.startActivityForResult(intent, REQUEST_SECURE_LOCK_CODE);
            } else {
                Utils.showToast(this, "No secure lock");
            }
        } else {
            Utils.showToast(this, "No secure lock");
        }

    }

}
