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

package com.adam.app.demoset.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Global constants for the DemoApp project.
 * This class organizes constants by demo items using custom annotations for better classification.
 */
public final class DemoAppConstants {

    private DemoAppConstants() {
        // Prevent instantiation
    }

    /**
     * Annotation to classify constants by demo item.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface DemoItem {
        String value();
    }

    // --- General/Global ---
    @DemoItem("General")
    public static final String LOG_STATUS = "log.status";
    @DemoItem("General")
    public static final String TRUE = "True";
    @DemoItem("General")
    public static final String FALSE = "False";
    @DemoItem("General")
    public static final String KEY_MSG = "msg";
    @DemoItem("General")
    public static final String TAG_DEMO_SET = "DemoSet";
    @DemoItem("General")
    public static final long DELAY_TIME_MILLIS = 3000L;

    // --- Storage Demo Items ---
    @DemoItem("Content Provider")
    public static final String AUTHORITY_MY_NOTE = "com.adam.app.demoset.provider.MyNote";
    @DemoItem("Content Provider")
    public static final String DATABASE_NAME_CP = "Adam";
    @DemoItem("Content Provider")
    public static final String TABLE_NAME_NOTE = "NoteTable";
    @DemoItem("Content Provider")
    public static final String COLUMN_ID = "_id";
    @DemoItem("Content Provider")
    public static final String COLUMN_NOTE = "note";
    @DemoItem("Content Provider")
    public static final String COLUMN_TIMESTAMP = "timestamp";

    @DemoItem("File Manager")
    public static final String AUTHORITY_FILE_PROVIDER = "com.adam.app.demoset.filemanager.provider";
    @DemoItem("File Manager")
    public static final String OP_MANAGE_EXTERNAL_STORAGE = "android:manage_external_storage";
    @DemoItem("File Manager")
    public static final String NOT_APPLICABLE = "N/A";

    @DemoItem("DataStore")
    public static final String USER_SETTINGS = "user_settings";
    @DemoItem("DataStore")
    public static final String LANG_ZH_TW = "zh_TW";

    @DemoItem("Room Database")
    public static final String DATABASE_NAME_ROOM = "NoteRoomDatabase";

    // --- System Demo Items ---
    @DemoItem("Notification")
    public static final String NOTIFY_CHANNEL_ID_DEFAULT = "0x1357";
    @DemoItem("Notification")
    public static final String NOTIFY_CHANNEL_ID_TEST = "test_notification_channel_id";

    @DemoItem("Alarm")
    public static final String ACTION_UPDATE_ALARM_INFO = "com.adam.app.demoset.alarm.ACTION_UPDATE_INFO";

    @DemoItem("Service")
    public static final String ACTION_START_SECUR_SERVICE = "com.adam.app.demo.start.secur_servcie";
    @DemoItem("Service")
    public static final String PERMISSION_SECUR_SERVICE = "com.adam.app.permission.SECUR_SERVICE";

    @DemoItem("WorkManager")
    public static final String IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work";
    @DemoItem("WorkManager")
    public static final String TAG_IMG_OUTPUT = "OUTPUT";
    @DemoItem("WorkManager")
    public static final String OUTPUT_PATH = "blur_filter_outputs";

    @DemoItem("Job Service")
    public static final int JOB_ID_SECUR = 1001;

    // --- UI Demo Items ---
    @DemoItem("UI/Navigation")
    public static final String STATE_SHOWING_CATEGORY = "showing_category";
    @DemoItem("UI/Navigation")
    public static final String STATE_SELECTED_CATEGORY = "selected_category";

    @DemoItem("UI")
    public static final String ACTION_SHOW_SNACKBAR = "show snackbar";
    @DemoItem("UI")
    public static final String KEY_SNACKBAR_MSG = "service status";
    @DemoItem("UI")
    public static final String THE_SELECTED_IMAGE = "The selected image";

    @DemoItem("Floating UI")
    public static final String ACTION_SHOW_FLOATING_DIALOG = "show floating dialog";

    // --- Hardware Demo Items ---
    @DemoItem("Bluetooth")
    public static final String ACTION_FOUND_BT_DEVICE = "find bt device";
    @DemoItem("Bluetooth")
    public static final String ACTION_UPDATE_BT_BOUND_STATE = "bt bound state";
    @DemoItem("Bluetooth")
    public static final String ACTION_UPDATE_CONNECT_INFO = "connect.info";
    @DemoItem("Bluetooth")
    public static final String KEY_DEVICE_LIST = "device.list";
    @DemoItem("Bluetooth")
    public static final String KEY_BT_DEVICE = "bluetooth.device";
    @DemoItem("Bluetooth")
    public static final String KEY_BUNDLE_DEVICE = "bundle device";
    @DemoItem("Bluetooth")
    public static final String KEY_CONNECT_INFO = "key.connect";

    @DemoItem("Camera")
    public static final String FILENAME_FORMAT_CAMERA = "yyyy-MM-dd-HH-mm-ss-SSS";
    @DemoItem("Camera")
    public static final String THREAD_NAME_CAMERA = "CameraBackground";
    @DemoItem("Camera")
    public static final String THREAD_NAME_VIDEO = "camera work thread";

    @DemoItem("Flashlight")
    public static final String PROP_FLASH_LIGHT_ENABLE = "flash light enable";
    @DemoItem("Flashlight")
    public static final String CMD_FLASH_LIGHT_ON = "flash light on";
    @DemoItem("Flashlight")
    public static final String CMD_FLASH_LIGHT_OFF = "flash light off";
    @DemoItem("Flashlight")
    public static final String TAG_FLASH_LIGHT_WORK = "tag.flash.light.work";
    @DemoItem("Flashlight")
    public static final String KEY_COMMEND = "key.commend";
    @DemoItem("Flashlight")
    public static final String KEY_ON = "key.on";

    @DemoItem("USB")
    public static final String USB_PERMISSION = "com.demo.app.usb.permission";

    // --- IPC Demo Items ---
    @DemoItem("Binder")
    public static final String REMOTE_PROCESS_NAME = ":remote";

    // --- Security Demo Items ---
    @DemoItem("Security")
    public static final String UNKNOWN_STATUS = "Unknown";
    @DemoItem("Security")
    public static final String KEY_ALIAS_ENCRYPTION = "encryption_demo_key";
    @DemoItem("Security")
    public static final String KEYSTORE_ANDROID = "AndroidKeyStore";
    @DemoItem("Security")
    public static final String ENCRYPTED_PREFS_NAME = "encrypted_demo_settings";
    @DemoItem("Security")
    public static final String NORMAL_PREFS_NAME = "normal_demo_settings";

    // --- AI / ML Demo Items ---
    @DemoItem("AI/ML")
    public static final String TFLITE_MODEL_FILE = "mobilenet_v2_1.0_224_quant.tflite";

    // --- Performance Demo Items ---
    @DemoItem("Performance")
    public static final String TAG_LEAK_CANARY = "LeakCanary";

    // --- Dynamic Delivery ---
    @DemoItem("Dynamic Delivery")
    public static final String MODULE_NAME_DYNAMIC = "dynamic_feature";

}
