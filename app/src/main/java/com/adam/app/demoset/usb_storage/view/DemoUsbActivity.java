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

package com.adam.app.demoset.usb_storage.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoUsbDeviceBinding;
import com.adam.app.demoset.usb_storage.adapter.FileListAdapter;
import com.adam.app.demoset.usb_storage.model.FileItem;
import com.adam.app.demoset.usb_storage.viewmodel.UsbViewModel;
import com.adam.app.demoset.utils.Utils;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.ArrayList;

public class DemoUsbActivity extends AppCompatActivity {

    private UsbViewModel mViewModel;
    private ActivityDemoUsbDeviceBinding mBinding;
    private FileListAdapter mLocalAdapter;
    private FileListAdapter mUsbAdapter;

    private final ActivityResultLauncher<Intent> mSettingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (checkAllFilesPermission()) {
                    mViewModel.initUsbHelper();
                } else {
                    Toast.makeText(this, getString(R.string.msg_all_files_access_required), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_usb_device);
        mViewModel = new ViewModelProvider(this).get(UsbViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        initViews();

        if (checkAllFilesPermission()) {
            mViewModel.initUsbHelper();
        } else {
            requestAllFilesPermission();
        }
    }

    private void initViews() {
        mLocalAdapter = new FileListAdapter(this, new ArrayList<>());
        mBinding.localFileLv.setAdapter(mLocalAdapter);
        mBinding.localFileLv.setOnItemClickListener((parent, view, position, id) -> {
            FileItem item = mLocalAdapter.getItem(position);
            if (item.isDirectory()) {
                mViewModel.loadLocalFiles((File) item.getOriginalFile());
            } else {
                mViewModel.copyFileToUsb((File) item.getOriginalFile());
            }
        });

        mUsbAdapter = new FileListAdapter(this, new ArrayList<>());
        mBinding.usbFileLv.setAdapter(mUsbAdapter);
        mBinding.usbFileLv.setOnItemClickListener((parent, view, position, id) -> {
            FileItem item = mUsbAdapter.getItem(position);
            if (item.isDirectory()) {
                mViewModel.loadUsbFiles((UsbFile) item.getOriginalFile());
            } else {
                mViewModel.copyFileFromUsb((UsbFile) item.getOriginalFile());
            }
        });

        mViewModel.getLocalFiles().observe(this, items -> mLocalAdapter.updateList(items));
        mViewModel.getUsbFiles().observe(this, items -> mUsbAdapter.updateList(items));
        mViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return true; 
    }

    private void requestAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Utils.showAlertDialog(this, 
                    getString(R.string.title_all_files_access_required), 
                    (dialog, which) -> {
                        Toast.makeText(this, getString(R.string.msg_no_usb_device), Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        mSettingsLauncher.launch(intent);
                    });
        } else {
            mViewModel.initUsbHelper();
        }
    }
}
