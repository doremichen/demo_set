package com.adam.app.demoset.filemanager;


import android.Manifest;
import android.app.AppOpsManager;
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
import com.adam.app.demoset.Utils;

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


    public static String getPermissionStatus(AppCompatActivity activity) {
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return checkStoragePermissionApi30(activity).toString();
        return checkStoragePermissionApi19(activity).toString();
    }

    public static Boolean checkStoragePermission(AppCompatActivity activity) {
        Utils.info(FileUtils.class, "checkStoragePermission");
        // check sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return checkStoragePermissionApi30(activity);
        return checkStoragePermissionApi19(activity);
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
    private static Boolean checkStoragePermissionApi30(AppCompatActivity activity) {
        AppOpsManager appOps = (AppOpsManager) activity.getSystemService(AppOpsManager.class);
        int mode = appOps.unsafeCheckOpNoThrow(
                MANAGE_EXTERNAL_STORAGE_PERMISSION,
                activity.getApplicationInfo().uid,
                activity.getPackageName()
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
    private static Boolean checkStoragePermissionApi19(AppCompatActivity activity) {
        int status = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
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


    public static void  openFile(AppCompatActivity activity, File selectedItem) {
        // Get Uri and mime type
        Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), AUTHORITY, selectedItem);
        String mime = getMimeType(uri.toString());

        // Open file with user selected app
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mime);
        activity.startActivity(intent);
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

    public static String ParentLinkLabel(AppCompatActivity activity) {
        return activity.getString(R.string.go_parent_label);
    }

    public static String ItemLabel(AppCompatActivity activity, File file) {
        return (file.isDirectory())? activity.getString(R.string.folder_item, file.getName()):
                activity.getString(R.string.file_item, file.getName());
    }


}
