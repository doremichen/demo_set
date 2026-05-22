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

package com.adam.app.demoset.usb_storage.viewmodel;

import android.app.Application;
import android.hardware.usb.UsbDevice;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.usb_storage.adapter.FileListAdapter;
import com.adam.app.demoset.usb_storage.helper.UsbHelper;
import com.adam.app.demoset.usb_storage.model.FileItem;
import com.adam.app.demoset.usb_storage.receiver.USBBroadCastReceiver;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbViewModel extends AndroidViewModel implements USBBroadCastReceiver.UsbListener {

    private final MutableLiveData<List<FileItem>> mLocalFiles = new MutableLiveData<>();
    private final MutableLiveData<List<FileItem>> mUsbFiles = new MutableLiveData<>();
    private final MutableLiveData<String> mProgressText = new MutableLiveData<>();
    private final MutableLiveData<String> mCurrentLocalPath = new MutableLiveData<>();
    private final MutableLiveData<String> mErrorMessage = new MutableLiveData<>();

    private final String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private UsbHelper mUsbHelper;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    
    // To track current USB folder for "Up" navigation
    private UsbFile mCurrentUsbFolder;

    public UsbViewModel(@NonNull Application application) {
        super(application);
        mCurrentLocalPath.setValue(mRootPath);
    }

    public void initUsbHelper() {
        this.mUsbHelper = new UsbHelper(getApplication(), this);
        loadLocalFiles(new File(mRootPath));
    }

    public LiveData<List<FileItem>> getLocalFiles() {
        return mLocalFiles;
    }

    public LiveData<List<FileItem>> getUsbFiles() {
        return mUsbFiles;
    }

    public LiveData<String> getProgressText() {
        return mProgressText;
    }

    public LiveData<String> getCurrentLocalPath() {
        return mCurrentLocalPath;
    }

    public LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    public void loadLocalFiles(File directory) {
        if (directory == null || !directory.isDirectory()) return;

        mExecutor.execute(() -> {
            File[] files = directory.listFiles();
            List<FileItem> items = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    items.add(new FileItem(
                            file.getName(),
                            file.isDirectory() ? "" : FileListAdapter.toFileSizeInfo(file.length()),
                            FileListAdapter.FileIcon.getResourceIdBy(file.isDirectory(), file.getName()),
                            file,
                            file.isDirectory()
                    ));
                }
            }
            mMainHandler.post(() -> {
                mLocalFiles.setValue(items);
                mCurrentLocalPath.setValue(directory.getAbsolutePath());
            });
        });
    }

    public void updateUsbList() {
        if (mUsbHelper == null) return;
        mExecutor.execute(() -> {
            UsbMassStorageDevice[] devices = mUsbHelper.getDeviceList();
            if (devices.length > 0) {
                // Initialize device and read root
                ArrayList<UsbFile> rootFiles = mUsbHelper.readFilesFrom(devices[0]);
                mCurrentUsbFolder = mUsbHelper.getFolder(false);
                loadUsbFilesFromList(rootFiles);
            } else {
                mMainHandler.post(() -> mUsbFiles.setValue(new ArrayList<>()));
            }
        });
    }

    public void loadUsbFiles(UsbFile folder) {
        if (folder == null || mUsbHelper == null) return;
        if (!folder.isDirectory()) return;
        
        mExecutor.execute(() -> {
            List<UsbFile> files = mUsbHelper.readFilesFrom(folder);
            mCurrentUsbFolder = folder;
            loadUsbFilesFromList(files);
        });
    }

    private void loadUsbFilesFromList(List<UsbFile> files) {
        mExecutor.execute(() -> {
            List<FileItem> items = new ArrayList<>();
            for (UsbFile file : files) {
                items.add(new FileItem(
                        file.getName(),
                        file.isDirectory() ? "" : FileListAdapter.toFileSizeInfo(file.getLength()),
                        FileListAdapter.FileIcon.getResourceIdBy(file.isDirectory(), file.getName()),
                        file,
                        file.isDirectory()
                ));
            }
            mMainHandler.post(() -> mUsbFiles.setValue(items));
        });
    }

    public void copyFileToUsb(File from) {
        if (mUsbHelper == null || mCurrentUsbFolder == null) {
            mErrorMessage.setValue(getApplication().getString(com.adam.app.demoset.R.string.msg_no_usb_device));
            return;
        }

        mExecutor.execute(() -> {
            mUsbHelper.saveFileToUsb(from, mCurrentUsbFolder, value -> mMainHandler.post(() -> {
                mProgressText.setValue("From Local: " + mCurrentLocalPath.getValue() + "\nTo Usb: " + mCurrentUsbFolder.getName() + "\nProgress: " + value + "%");
            }));
            // Refresh current folder after copy
            List<UsbFile> files = mUsbHelper.readFilesFrom(mCurrentUsbFolder);
            loadUsbFilesFromList(files);
        });
    }

    public void copyFileFromUsb(UsbFile from) {
        if (mUsbHelper == null || mCurrentUsbFolder == null) {
            mErrorMessage.setValue(getApplication().getString(com.adam.app.demoset.R.string.msg_no_usb_device));
            return;
        }

        String targetPath = mCurrentLocalPath.getValue() + File.separator + from.getName();
        mExecutor.execute(() -> {
            mUsbHelper.saveFileFromUsb(from, targetPath, value -> mMainHandler.post(() -> {
                mProgressText.setValue("From Usb: " + from.getName() + "\nTo Local: " + mCurrentLocalPath.getValue() + "\nProgress: " + value + "%");
            }));
            // Refresh local list after copy
            loadLocalFiles(new File(mCurrentLocalPath.getValue()));
        });
    }

    public void navigateUpLocal() {
        String current = mCurrentLocalPath.getValue();
        if (current != null && !current.equals(mRootPath)) {
            loadLocalFiles(new File(current).getParentFile());
        }
    }

    public void navigateUpUsb() {
        if (mUsbHelper == null || mCurrentUsbFolder == null || mCurrentUsbFolder.isRoot()) return;
        loadUsbFiles(mCurrentUsbFolder.getParent());
    }

    @Override
    public void onInsert(UsbDevice device) {
        updateUsbList();
    }

    @Override
    public void onRemove(UsbDevice device) {
        mCurrentUsbFolder = null;
        updateUsbList();
    }

    @Override
    public void onHavePermission(UsbDevice device) {
        updateUsbList();
    }

    @Override
    public void onFail(UsbDevice device) {
        mMainHandler.post(() -> mErrorMessage.setValue(getApplication().getString(com.adam.app.demoset.R.string.msg_no_usb_device)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mUsbHelper != null) {
            mUsbHelper.finishUsbHelper();
        }
        mExecutor.shutdown();
    }
}
