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


import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;

abstract class FileUtils {

    private static final String AUTHORITY = "com.adam.app.demoset.filemanager.provider";

    public static final String MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage";
    public static final String NOT_APPLICABLE = "N/A";

    public static String getStoragePermissionName() {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return MANAGE_EXTERNAL_STORAGE_PERMISSION;
        return Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    public static void openPermissionSettings(AppCompatActivity activity) {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermissionApi30(activity);
            return;
        }

        activity.startActivity(
                new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity.getPackageName(), null)
                )
        );
    }

    public static String getLegacyStorageStatus() {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return String.valueOf(Environment.isExternalStorageLegacy());

        return NOT_APPLICABLE;
    }


    public static String getPermissionStatus(Context context) {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return checkStoragePermissionApi30(context).toString();
        return checkStoragePermissionApi19(context).toString();
    }

    public static Boolean checkStoragePermission(Context context) {
        Utils.info(FileUtils.class, "checkStoragePermission");
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return checkStoragePermissionApi30(context);
        return checkStoragePermissionApi19(context);
    }

    public static void requestStoragePermission(AppCompatActivity activity) {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermissionApi30(activity);
            return;
        }

        requestStoragePermissionApi19(activity);
    }

    @RequiresApi(30)
    private static Boolean checkStoragePermissionApi30(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        int mode = appOps.unsafeCheckOpNoThrow(
                MANAGE_EXTERNAL_STORAGE_PERMISSION,
                context.getApplicationInfo().uid,
                context.getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @RequiresApi(30)
    private static void requestStoragePermissionApi30(AppCompatActivity activity) {
        Intent intent =new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        // start activity
        activity.startActivityForResult(intent, FileExploreAct.MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
    }

    @RequiresApi(19)
    private static Boolean checkStoragePermissionApi19(Context context) {
        int status = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(19)
    private static void requestStoragePermissionApi19(AppCompatActivity activity) {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                FileExploreAct.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
        );
    }

    private static String getMimeType(String uriStr) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(uriStr);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return (mime != null)? mime: "text/plain";
    }


    public static void  openFile(Context context, File selectedItem) {
        // Get Uri and mime type
        Uri uri = FileProvider.getUriForFile(context.getApplicationContext(), AUTHORITY, selectedItem);
        String mime = getMimeType(uri.toString());

        // Open file with user selected app
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!(context instanceof AppCompatActivity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, mime);
        context.startActivity(intent);
    }


    public static List<File> getFiles(@NonNull File selectedItem) throws FileNotFoundException {
        List<File> resultList = new ArrayList<>();
        // filter not hidden file in list
        File[] files = selectedItem.listFiles();
        if ((files == null) || (files.length == 0)) {
            throw new FileNotFoundException("No files!!!");
        }

        List<File> filterFiles = Arrays.stream(files)
                .filter(file -> !file.isHidden())
                .collect(Collectors.toList());

        // is directory?
        if (selectedItem.equals(Environment.getExternalStorageDirectory())) {
            return filterFiles;
        }

        resultList.add(selectedItem.getParentFile());
        resultList.addAll(filterFiles);
        return resultList;
    }

    public static String ParentLinkLabel(Context context) {
        return context.getString(R.string.go_parent_label);
    }

    public static String ItemLabel(Context context, File file) {
        return (file.isDirectory())? context.getString(R.string.folder_item, file.getName()):
                context.getString(R.string.file_item, file.getName());
    }


}
