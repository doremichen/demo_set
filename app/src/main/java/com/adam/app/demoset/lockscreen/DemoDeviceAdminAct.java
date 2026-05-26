/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.lockscreen;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoDeviceAdminBinding;
import com.adam.app.demoset.lockscreen.viewmodel.DeviceAdminViewModel;
import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * DemoDeviceAdminAct
 * <p>
 * This activity demonstrates Device Admin privileges and Device Credential confirmation.
 */
public class DemoDeviceAdminAct extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> mEnableAdminLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int resultCode = result.getResultCode();
                Utils.showToast(this, (resultCode == Activity.RESULT_OK) ? "成功啟動設備管理員"
                        : "使用者拒絕啟動");
            }
    );

    private final ActivityResultLauncher<Intent> mSecureLockLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int resultCode = result.getResultCode();
                Utils.showToast(this, (resultCode == Activity.RESULT_OK) ? "安全驗證成功"
                        : "安全驗證失敗");
            }
    );

    private ActivityDemoDeviceAdminBinding mBinding;
    private DeviceAdminViewModel mViewModel;
    private LogAdapter mLogAdapter;
    private DevicePolicyManager mDevPolicyManager;
    private ComponentName mCompName;
    private KeyguardManager mKeyguardManager;
    private List<DeviceAdminViewModel.AdminAction> mItemActionList = new ArrayList<>();

    private List<String> getActionNameList() {
        List<String> list = new ArrayList<>();
        for (DeviceAdminViewModel.AdminAction action : mItemActionList) {
            list.add(getResources().getString(action.getResIdName()));
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDemoDeviceAdminBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.tvHeader);

        mViewModel = new ViewModelProvider(this).get(DeviceAdminViewModel.class);

        mDevPolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mCompName = new ComponentName(this, MyAdminReceiver.class);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        mViewModel.addLog("System Console Ready...");

        setupLogView();
        observerViewModel();
    }

    private void setupLogView() {
        mLogAdapter = new LogAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        mBinding.rvLogConsole.setLayoutManager(layoutManager);
        mBinding.rvLogConsole.setAdapter(mLogAdapter);
    }

    private void observerViewModel() {
        mViewModel.getAdminActions().observe(this, this::setupActionList);
        mViewModel.getLogs().observe(this, this::setupLogConsole);
        mViewModel.getNavigation().observe(this, this::navigate);
    }

    private void setupLogConsole(List<String> strings) {
        mLogAdapter.submitList(strings, () ->
                mBinding.rvLogConsole.scrollToPosition(mLogAdapter.getItemCount() - 1));
    }

    private void navigate(String s) {
        if (s.equals(DeviceAdminViewModel.UNKNOWN)) {
            return;
        }

        try {
            DeviceAdminViewModel.AdminAction action = DeviceAdminViewModel.AdminAction.valueOf(s);
            switch (action) {
                case ENABLE_ADMIN:
                    enableDeviceAdmin();
                    break;
                case DISABLE_ADMIN:
                    disableAdmin();
                    break;
                case LOCK_SCREEN:
                    lockScreen();
                    break;
                case SECURITY_LOCK:
                    showAuthentication();
                    break;
                case EXIT:
                    finish();
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            mViewModel.addLog("Error: Unrecognized action " + s);
        }

        mViewModel.doneNavigation();
    }

    private void setupActionList(List<DeviceAdminViewModel.AdminAction> list) {
        mItemActionList = list;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getActionNameList());
        mBinding.listDemoAdmin.setAdapter(adapter);

        mBinding.listDemoAdmin.setOnItemClickListener((parent, view, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            mViewModel.addLog("Action clicked: " + item);

            for (DeviceAdminViewModel.AdminAction action : mItemActionList) {
                String actionName = getResources().getString(action.getResIdName());
                if (actionName.equals(item)) {
                    action.process(mViewModel);
                    break;
                }
            }
        });
    }

    private void enableDeviceAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(mCompName);
        if (isAdmin) {
            mViewModel.addLog("Admin is already enabled");
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCompName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.demo_device_admin_instruction));

        mEnableAdminLauncher.launch(intent);
    }

    private void disableAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(mCompName);
        if (!isAdmin) {
            mViewModel.addLog("No admin enabled to disable");
            return;
        }
        mDevPolicyManager.removeActiveAdmin(mCompName);
        mViewModel.addLog("Admin privileges removed");
    }

    private void lockScreen() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(mCompName);
        if (!isAdmin) {
            mViewModel.addLog("Error: Device Admin must be active to force lock");
            return;
        }
        mDevPolicyManager.lockNow();
    }

    private void showAuthentication() {
        if (!mKeyguardManager.isKeyguardSecure()) {
            mViewModel.addLog("No secure lock (PIN/Pattern/Password) configured");
            return;
        }

        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(
                getString(R.string.item_security_lock),
                getString(R.string.demo_lock_screen_instruction_fallback)
        );

        if (intent != null) {
            mSecureLockLauncher.launch(intent);
        } else {
            mViewModel.addLog("Error: Could not invoke system credential UI");
        }
    }
}
