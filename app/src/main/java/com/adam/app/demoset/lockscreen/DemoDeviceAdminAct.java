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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DemoDeviceAdminAct extends AppCompatActivity {


    private static final String ITEM_ENABLE_ADMIN = "Enale admin";
    private static final String ITEM_DISABLE_ADMIN = "Disale admin";
    private static final String ITEM_LOCK_SCREEN = "Force Lock screen";
    private static final String ITEM_SECURITY_LOCK = "Security lock";
    private static final String ITEM_EXIT = "Exit";

    public static final int REQUEST_ENABLE_ADMIN_CODE = 0x0224;
    public static final int REQUEST_SECURE_LOCK_CODE = 0x0112;

    private ListView mList;

    private DevicePolicyManager mDevPolicyManager;
    private ComponentName mCompName;

    private KeyguardManager mKeyguardManager;

    // item strategy
    private abstract class ItemStrategy {
        private String mItem;
        ItemStrategy(String item) {
            this.mItem = item;
        }

        String Name() {
            return this.mItem;
        }

        abstract void process();
    }

    private class ExitStrategy extends ItemStrategy {

        ExitStrategy(String item) {
            super(item);
        }

        @Override
        void process() {
            DemoDeviceAdminAct.this.finish();
        }
    }

    private class EnableAdaminStrategy extends ItemStrategy {

        EnableAdaminStrategy(String item) {
            super(item);
        }

        @Override
        void process() {
            enableDeviceAdmin();
        }
    }

    private class DisableAdminStrategy extends ItemStrategy {

        DisableAdminStrategy(String item) {
            super(item);
        }

        @Override
        void process() {
            disableAdmin();
        }
    }

    private class LockScreenStrategy extends ItemStrategy {

        LockScreenStrategy(String item) {
            super(item);
        }

        @Override
        void process() {
            lockScreen();
        }
    }

    private class SecurityLockStrategy extends ItemStrategy {

        SecurityLockStrategy(String item) {
            super(item);
        }

        @Override
        void process() {
            showAuthentication();
        }
    }

    // Strategy list
    private List<ItemStrategy> mItemStrategyList = new ArrayList<>() {
        {
            add(new ExitStrategy(ITEM_EXIT));
            add(new EnableAdaminStrategy(ITEM_ENABLE_ADMIN));
            add(new DisableAdminStrategy(ITEM_DISABLE_ADMIN));
            add(new LockScreenStrategy(ITEM_LOCK_SCREEN));
            add(new SecurityLockStrategy(ITEM_SECURITY_LOCK));
        }
    };

    // Item list
    private List<String> getList() {
        return this.mItemStrategyList.stream()
                .map(ItemStrategy::Name)
                .collect(Collectors.toList());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_device_admin);

        mDevPolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mCompName = new ComponentName(this, MyAdminReceiver.class);

        mKeyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);

        mList = this.findViewById(R.id.list_demo_admin);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getList());

        mList.setAdapter(adapter);


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Utils.showToast(DemoDeviceAdminAct.this, "item: " + item);

                DemoDeviceAdminAct.this.mItemStrategyList.stream()
                        .filter(strategy -> strategy.Name().equals(item))
                        .findFirst()
                        .ifPresent(ItemStrategy::process);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_ADMIN_CODE) {
            Utils.showToast(this, resultCode == Activity.RESULT_OK ? "enable admin" : "can not enable admin");
        } else if (requestCode == REQUEST_SECURE_LOCK_CODE) {
            Utils.showToast(this, resultCode == Activity.RESULT_OK ? "secure lock is success" : "secure lock is fail");
        }
    }

    private void enableDeviceAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (isAdmin) {
            Utils.showToast(this, "Admin is enabled");
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, this.mCompName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Why need this permission");
        startActivityForResult(intent, REQUEST_ENABLE_ADMIN_CODE);

    }

    private void disableAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (!isAdmin) {
            Utils.showToast(this, "No admin enable");
            return;
        }

        mDevPolicyManager.removeActiveAdmin(this.mCompName);
    }

    private void lockScreen() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (!isAdmin) {
            Utils.showToast(this, "No admin enable");
            return;
        }

        mDevPolicyManager.lockNow();
    }

    private void showAuthentication() {
        // check if secure lock exists?
        if (!mKeyguardManager.isKeyguardSecure()) {
            Utils.showToast(this, "No secure lock");
            return;
        }

        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent("cust secure lock",
                "This is demo");
        if (!Utils.areAllNotNull(intent)) {
            Utils.showToast(this, "No secure lock");
            return;
        }

        this.startActivityForResult(intent, REQUEST_SECURE_LOCK_CODE);

    }

}
