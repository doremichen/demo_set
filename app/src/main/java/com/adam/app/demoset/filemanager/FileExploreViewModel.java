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

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileExploreViewModel extends AndroidViewModel {

    private final MutableLiveData<List<File>> mFiles = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> mDisplayItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> mPermissionViewVisibility = new MutableLiveData<>(View.GONE);
    private final MutableLiveData<Integer> mLegacyStorageViewVisibility = new MutableLiveData<>(View.GONE);
    private final MutableLiveData<Integer> mFilesTreeViewVisibility = new MutableLiveData<>(View.GONE);
    private final MutableLiveData<File> mOpenFileEvent = new MutableLiveData<>();

    public FileExploreViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<String>> getDisplayItems() {
        return mDisplayItems;
    }

    public LiveData<Integer> getPermissionViewVisibility() {
        return mPermissionViewVisibility;
    }

    public LiveData<Integer> getLegacyStorageViewVisibility() {
        return mLegacyStorageViewVisibility;
    }

    public LiveData<Integer> getFilesTreeViewVisibility() {
        return mFilesTreeViewVisibility;
    }

    public LiveData<File> getOpenFileEvent() {
        return mOpenFileEvent;
    }

    public void updateState() {
        boolean hasPermission = FileUtils.checkStoragePermission(getApplication());
        
        if (!hasPermission) {
            mPermissionViewVisibility.setValue(View.VISIBLE);
            mLegacyStorageViewVisibility.setValue(View.GONE);
            mFilesTreeViewVisibility.setValue(View.GONE);
            return;
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (!Environment.isExternalStorageLegacy()) {
                mPermissionViewVisibility.setValue(View.GONE);
                mLegacyStorageViewVisibility.setValue(View.VISIBLE);
                mFilesTreeViewVisibility.setValue(View.GONE);
                return;
            }
        }

        mPermissionViewVisibility.setValue(View.GONE);
        mLegacyStorageViewVisibility.setValue(View.GONE);
        mFilesTreeViewVisibility.setValue(View.VISIBLE);
        
        // Initial open
        if (mFiles.getValue() == null || mFiles.getValue().isEmpty()) {
            open(Environment.getExternalStorageDirectory());
        }
    }

    public void open(File selectedItem) {
        if (selectedItem.isFile()) {
            mOpenFileEvent.setValue(selectedItem);
            return;
        }

        try {
            List<File> fileList = FileUtils.getFiles(selectedItem);
            mFiles.setValue(fileList);
            mDisplayItems.setValue(mapItems(selectedItem, fileList));
        } catch (FileNotFoundException e) {
            Utils.showToast(getApplication(), getApplication().getString(R.string.demo_file_explore_no_file));
        }
    }

    public File getFileAt(int position) {
        List<File> files = mFiles.getValue();
        if (files != null && position >= 0 && position < files.size()) {
            return files.get(position);
        }
        return null;
    }

    private List<String> mapItems(File selectedFile, List<File> fileList) {
        return fileList.stream()
                .map(file -> isParentLink(selectedFile, file) ?
                        FileUtils.ParentLinkLabel(getApplication()) :
                        FileUtils.ItemLabel(getApplication(), file))
                .collect(Collectors.toList());
    }

    private boolean isParentLink(File currentDir, File file) {
        File parent = currentDir.getParentFile();
        return parent != null && file.getPath().equals(parent.getPath());
    }

    public void resetOpenFileEvent() {
        mOpenFileEvent.setValue(null);
    }
}
