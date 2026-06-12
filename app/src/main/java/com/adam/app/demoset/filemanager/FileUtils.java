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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.adam.app.demoset.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for file operations and storage permission management.
 * Refactored to use ActivityResultLauncher for better decoupling and memory safety.
 */
public abstract class FileUtils {

    public static final String OP_MANAGE_EXTERNAL_STORAGE = "android:manage_external_storage";
    public static final String NOT_APPLICABLE = "N/A";
    private static final String AUTHORITY = "com.adam.app.demoset.filemanager.provider";

    /**
     * Integrated API to request storage access using ActivityResultLauncher.
     * Uses ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION to jump directly to the app's settings.
     *
     * @param packageName           The package name of the application
     * @param manageStorageLauncher Launcher for Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION (API 30+)
     * @param readStorageLauncher   Launcher for Manifest.permission.READ_EXTERNAL_STORAGE (API < 30)
     */
    public static void requestStorageAccess(
            @NonNull String packageName,
            @NonNull ActivityResultLauncher<Intent> manageStorageLauncher,
            @NonNull ActivityResultLauncher<String> readStorageLauncher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Correct Action for specific package: ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + packageName));
            manageStorageLauncher.launch(intent);
        } else {
            readStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Checks if the app has the necessary storage permissions.
     */
    public static boolean hasStoragePermission(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return isExternalStorageManager(context);
        }
        return isReadExternalStorageGranted(context);
    }

    public static String getStoragePermissionName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return "All Files Access";
        return Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    public static void openPermissionSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        if (!(context instanceof AppCompatActivity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static String getLegacyStorageStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            return String.valueOf(Environment.isExternalStorageLegacy());

        return NOT_APPLICABLE;
    }

    public static String getPermissionStatus(@NonNull Context context) {
        return String.valueOf(hasStoragePermission(context));
    }

    @RequiresApi(30)
    private static boolean isExternalStorageManager(Context context) {
        AppOpsManager appOps = context.getSystemService(AppOpsManager.class);
        if (appOps == null) return false;
        int mode = appOps.unsafeCheckOpNoThrow(
                OP_MANAGE_EXTERNAL_STORAGE,
                context.getApplicationInfo().uid,
                context.getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private static boolean isReadExternalStorageGranted(Context context) {
        int status = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    private static String getMimeType(String uriStr) {

        String ext = MimeTypeMap.getFileExtensionFromUrl(uriStr);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return (mime != null) ? mime : "text/plain";
    }


    public static void openFile(Context context, File selectedItem) {
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
        return (file.isDirectory()) ? context.getString(R.string.folder_item, file.getName()) :
                context.getString(R.string.file_item, file.getName());
    }


}
