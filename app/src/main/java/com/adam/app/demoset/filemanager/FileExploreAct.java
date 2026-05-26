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

package com.adam.app.demoset.filemanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoDataBindingExBinding;
import com.adam.app.demoset.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;

import android.widget.ArrayAdapter;

public class FileExploreAct extends AppCompatActivity {

    static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;
    static final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2;

    private ActivityDemoDataBindingExBinding mBinding;
    private FileExploreViewModel mViewModel;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info("onCreate");

        mBinding = ActivityDemoDataBindingExBinding.inflate(getLayoutInflater());
        mBinding.setLifecycleOwner(this);
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this).get(FileExploreViewModel.class);
        mBinding.setViewModel(mViewModel);

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.toolbar);

        setupUi();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        info("onResume");
        mViewModel.updateState();
    }

    private void setupUi() {
        info("setupUi");
        
        mBinding.toolbar.inflateMenu(R.menu.action_menu_file_manager);
        mBinding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                showAboutDialog();
                return true;
            }
            return false;
        });

        mBinding.permissionButton.setOnClickListener(v -> FileUtils.requestStoragePermission(this));

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        mBinding.filesTreeView.setAdapter(mAdapter);
        mBinding.filesTreeView.setOnItemClickListener((parent, view, position, id) -> {
            File selectedItem = mViewModel.getFileAt(position);
            if (selectedItem != null) {
                mViewModel.open(selectedItem);
            }
        });
    }

    private void observeViewModel() {
        mViewModel.getDisplayItems().observe(this, items -> {
            mAdapter.clear();
            mAdapter.addAll(new ArrayList<>(items));
            mAdapter.notifyDataSetChanged();
        });

        mViewModel.getOpenFileEvent().observe(this, file -> {
            if (file != null) {
                FileUtils.openFile(this, file);
                mViewModel.resetOpenFileEvent();
            }
        });
    }

    private void showAboutDialog() {
        Utils.DialogButton okBtn = new Utils.DialogButton(getString(R.string.label_ok_btn), (dialog, which) -> dialog.dismiss());
        Utils.showAlertDialog(this, R.string.settings_title, getString(R.string.about_file_manager_content), okBtn);
    }

    private void info(String msg) {
        Utils.info(this, msg);
    }
}
