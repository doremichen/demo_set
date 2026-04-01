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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoDeviceAdminBinding;
import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.lockscreen.viewmodel.DeviceAdminViewModel;
import com.adam.app.demoset.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


public class DemoDeviceAdminAct extends AppCompatActivity {

    // TAG
    private static final String TAG = "DemoDeviceAdminAct";


    // define admin result launcher
    private final ActivityResultLauncher<Intent> mEnableAdminLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                int resultCode = result.getResultCode();

                Utils.showToast(this, (resultCode == Activity.RESULT_OK) ? "成功啟動設備管理員"
                        : "使用者拒絕啟動");

            }
    );
    // define secure lock result launcher
    private final ActivityResultLauncher<Intent> mSecureLockLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                int resultCode = result.getResultCode();

                Utils.showToast(this, (resultCode == Activity.RESULT_OK) ? "安全驗證成功"
                        : "安全驗證失敗");

            }
    );

    // view binding
    private ActivityDemoDeviceAdminBinding mBinding;
    // view model
    private DeviceAdminViewModel mViewModel;
    // log adapter
    private LogAdapter mLogAdapter;


    private DevicePolicyManager mDevPolicyManager;

    private ComponentName mCompName;
    private KeyguardManager mKeyguardManager;
    // Strategy list
    private List<DeviceAdminViewModel.Strategy> mItemStrategyList = new ArrayList<>();

    // Item list
    private List<String> getList() {
        List<String> list = new ArrayList<>();
        for (DeviceAdminViewModel.Strategy s : mItemStrategyList) {
            list.add(getResources().getString(s.getResIdName()));
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoDeviceAdminBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.tvHeader);

        // init view model
        mViewModel = new ViewModelProvider(this).get(DeviceAdminViewModel.class);

        mDevPolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mCompName = new ComponentName(this, MyAdminReceiver.class);
        mKeyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        
        mViewModel.addLog("System Console Ready...");

        setupLogView();
        observerViewModel();
    }

    private void setupLogView() {
        mLogAdapter = new LogAdapter();
        // setup linear layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // set layout manager
        mBinding.rvLogConsole.setLayoutManager(layoutManager);
        // set adapter
        mBinding.rvLogConsole.setAdapter(mLogAdapter);
    }

    private void observerViewModel() {
        // observer strategy list
        mViewModel.getStrategy().observe(this, this::setupListItem);
        // observer logs
        mViewModel.getLogs().observe(this, this::setupLogConsole);
        // observer navigation
        mViewModel.getNavigation().observe(this, this::navigate);

    }

    private void setupLogConsole(List<String> strings) {
        // submit list
        mLogAdapter.submitList(strings, () -> {
            // scroll to bottom
            mBinding.rvLogConsole.scrollToPosition(mLogAdapter.getItemCount() - 1);
        });
    }

    private void navigate(String s) {
        // UNKNOW
        if (s.equals(DeviceAdminViewModel.UNKNOWN)) {
            return;
        }

        switch (s) {
            case "ENABLE_ADAMIN":
                enableDeviceAdmin();
                break;
            case "DISABLE_ADMIN":
                disableAdmin();
                break;
            case "LOCK_SCREEN":
                lockScreen();
                break;
            case "SECURITY_LOCK":
                showAuthentication();
                break;
            case "EXIT":
                DemoDeviceAdminAct.this.finish();
                break;
            default:
                break;
        }

        // done navigation
        mViewModel.doneNavigation();
    }


    private void setupListItem(List<DeviceAdminViewModel.Strategy> list) {

        mItemStrategyList = list;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getList());

        mBinding.listDemoAdmin.setAdapter(adapter);


        mBinding.listDemoAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                //Utils.showToast(DemoDeviceAdminAct.this, "item: " + item);
                //Utils.info(DemoDeviceAdminAct.this, "mItemStrategyList@onItemClick: " + mItemStrategyList);
                mViewModel.addLog("mItemStrategyList@onItemClick: " + item);

                for (DeviceAdminViewModel.Strategy s : mItemStrategyList) {
                    String sName = getResources().getString(s.getResIdName());
                    if (sName.equals(item)) {
                        // add log
                        mViewModel.addLog(sName);
                        s.process(mViewModel);
                        break;
                    }
                }

            }
        });
    }


    private void enableDeviceAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (isAdmin) {
            //Utils.showToast(this, "Admin is enabled");
            // add log
            mViewModel.addLog("Admin is enabled");
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, this.mCompName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "需要此權限以展示鎖屏功能");

        // launch
        mEnableAdminLauncher.launch(intent);

//        deprecate startActivityForResult(intent, REQUEST_ENABLE_ADMIN_CODE);

    }

    private void disableAdmin() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (!isAdmin) {
            //Utils.showToast(this, "No admin enable");
            // add log
            mViewModel.addLog("No admin enable");
            return;
        }

        mDevPolicyManager.removeActiveAdmin(this.mCompName);
    }

    private void lockScreen() {
        boolean isAdmin = mDevPolicyManager.isAdminActive(this.mCompName);
        if (!isAdmin) {
            //Utils.showToast(this, "No admin enable");
            // add log
            mViewModel.addLog("No admin enable");
            return;
        }

        mDevPolicyManager.lockNow();
    }

    private void showAuthentication() {
        // check if secure lock exists?
        if (!mKeyguardManager.isKeyguardSecure()) {
            //Utils.showToast(this, "No secure lock");
            // add log
            mViewModel.addLog("No secure lock");
            return;
        }

        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent("安全驗證",
                "請驗證身分以繼續");
        if (!Utils.areAllNotNull(intent)) {
            //Utils.showToast(this, "No secure lock");
            // add log
            mViewModel.addLog("No secure lock");
            return;
        }

        // launch
        mSecureLockLauncher.launch(intent);

    }

}
