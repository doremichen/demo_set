/**
 * Usb device helper
 */
package com.adam.app.demoset.usb_storage;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import com.adam.app.demoset.Utils;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileInputStream;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsbHelper {
    // The context that processes usb device plugin/out
    private Context mContext;
    // The usb device receiver

    // Usb device list
    private UsbMassStorageDevice[] mDeviceList;

    private USBBroadCastReceiver mUsbRev;
    // The usb state listener
    private USBBroadCastReceiver.UsbListener mUsbListener;
    // The path of current usb device storage
    private UsbFile mCurrentFolder;

    public UsbHelper(@NonNull Context context, @NonNull USBBroadCastReceiver.UsbListener listener) {
        this.mContext = context;
        this.mUsbListener = listener;
        // register usb receiver
        registerReceiver();
    }

    /**
     * Read usb device list
     * @return usb device list
     */
    public UsbMassStorageDevice[] getDeviceList() {
        Utils.info(this, "getDeviceList");
        // usb manager
        UsbManager manager = (UsbManager) this.mContext.getSystemService(Context.USB_SERVICE);
        // get device list
        this.mDeviceList = UsbMassStorageDevice.getMassStorageDevices(this.mContext);
        if (this.mDeviceList.length <= 0) {
            Utils.showToast(this.mContext, "No usb device!!!");
            return this.mDeviceList;
        }

        // Just process only one usb device in this example
        UsbDevice device = this.mDeviceList[0].getUsbDevice();
        // permission pending intent
        PendingIntent graint = PendingIntent.getBroadcast(this.mContext
                , 0
                , new Intent(USBBroadCastReceiver.USB_PERMISSION)
                , PendingIntent.FLAG_IMMUTABLE);

        Utils.info(this, "Need graint permission Usb device name: " + device.getDeviceName());
        // check permission
        // provide to user that graint permssion if the usb device has no permssion
        if (!manager.hasPermission(device)) {
            // request usb permission
            manager.requestPermission(device, graint);
        }

        return this.mDeviceList;
    }

    /**
     * Read files from usb storage device
     * @param device: usb storage device
     * @return All files in the root directory in usb storage device
     */
    public ArrayList<UsbFile> readFilesFrom(UsbMassStorageDevice device) {
        Utils.info(this, "readFilesFromStorage +++");
        ArrayList<UsbFile> files = new ArrayList<>();
        try {
            // init usb device
            device.init();
            // obtain partition
            Partition partition = device.getPartitions().get(0);
            FileSystem currentFileSys = partition.getFileSystem();
            // root directory
            UsbFile rootDir = currentFileSys.getRootDirectory();
            this.mCurrentFolder = rootDir;
            // copy all files to list
            Collections.addAll(files, rootDir.listFiles());
        } catch (IOException e) {
            // log
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        dumpFiles(files);
        Utils.info(this, "readFilesFromStorage xxx");
        return  files;
    }

    /**
     * Read files from the specified usb folder
     * @param folder: the usb folder
     * @return All files in usb folder
     */
    public ArrayList<UsbFile> readFilesFrom(UsbFile folder) {
        Utils.info(this, "readFilesFromFolder +++");
        ArrayList<UsbFile> files = new ArrayList<>();
        this.mCurrentFolder = folder;
        try {
            Collections.addAll(files, folder.listFiles());
        } catch (IOException e) {
            // log
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        dumpFiles(files);
        Utils.info(this, "readFilesFromFolder xxx");
        return  files;
    }

    /**
     * listen to copy file from usb storage device
     */
    public interface DownloadListener {
        void progress(int value);
    }

    /**
     * Copy file to usb storage device
     * @param file: the file need to be copied
     * @param saveFolder: usb root directory
     * @param listener: listen to copy file progress
     * @return the status of copy file to usb storage device
     */
    public boolean saveFileToUsb(File file, UsbFile saveFolder, DownloadListener listener) {
        Utils.info(this, "saveFileToUsb +++");

        UsbFile saveFile = null;
        // look up
        try {
            // check whether does file exist in usb storage device
            // delete if the file exists otherwise do nothing
            for (UsbFile usbFile: saveFolder.listFiles()) {
                if (usbFile.getName().equals(file.getName())) {
                    // delete
                    usbFile.delete();
                }
            }

            // create file in usb storage device
            saveFile = saveFolder.createFile(file.getName());
            // write file content
            FileInputStream fis = new FileInputStream(file);
            // file content size
            int fileSize = fis.available();
            UsbFileOutputStream UsbFos = new UsbFileOutputStream(saveFile);
            int writeCount = 0;
            int bytesRead = 0;
            byte[] buf = new byte[1024*8];
            // start
            while ((bytesRead = fis.read(buf)) != -1) {
                UsbFos.write(buf, 0, bytesRead);
                writeCount += bytesRead;
                // update progress
                listener.progress(writeCount * 100/fileSize);
            }
            // flush
            UsbFos.flush();
            // close
            fis.close();
            UsbFos.close();
            Utils.info(this, "saveFileToUsb xxx");
            return true;
        } catch (IOException e) {
            // log
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Copy file from usb storage device
     * @param usbFile: the file need to be copied
     * @param savePath: the assigned path in local storage
     * @param listener: listen to copy file progress
     * @return the status of copy file from usb storage device
     */
    public boolean saveFileFromUsb(UsbFile usbFile, String savePath, DownloadListener listener) {
        Utils.info(this, "saveFileFromUsb +++");
        try {
            // Usb input stream
            UsbFileInputStream UsbFis = new UsbFileInputStream(usbFile);
            // file output stream
            FileOutputStream fos = new FileOutputStream(savePath);
            // file size
            long fileSize = usbFile.getLength();
            int writeCount = 0;
            int bytesRead = 0;
            byte[] buf = new byte[1024];
            // start
            while ((bytesRead = UsbFis.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
                writeCount += bytesRead;
                listener.progress((int) (writeCount * 100 / fileSize));
            }
            // flush
            fos.flush();
            // close
            UsbFis.close();
            fos.close();
            Utils.info(this, "saveFileFromUsb xxx");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get the folder in usb storage device
     * @param isParent: Parent folder if true otherwise current folder
     * @return Usb folder
     */
    public UsbFile getFolder(boolean isParent) {
        Utils.info(this, "getFolder +++");
        if (this.mCurrentFolder == null) {
            Utils.showToast(this.mContext, "No file or no usb device!!!");
            return null;
        }

        if (isParent) {
            if (!this.mCurrentFolder.isRoot()) {
                Utils.info(this, "getFolder: parent");
                return this.mCurrentFolder.getParent();
            }
        }

        Utils.info(this, "getFolder: current");
        return mCurrentFolder;
    }

    /**
     * Release resource
     */
    public void finishUsbHelper() {
        Utils.info(this, "finishUsbHelper +++");
        // unregister Usb receiver
        this.mContext.unregisterReceiver(this.mUsbRev);
        Utils.info(this, "finishUsbHelper xxx");
    }


    /**
     * Register usb receiver and hook usb listener
     */
    private void registerReceiver() {
        Utils.info(this, "registerReceiver");
        // initial usb receiver
        this.mUsbRev =  new USBBroadCastReceiver();
        this.mUsbRev.setListener(this.mUsbListener);

        // monitor usb state
        IntentFilter monitorUsb = new IntentFilter();
        monitorUsb.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        monitorUsb.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.mContext.registerReceiver(this.mUsbRev, monitorUsb);

        // monitor usb permission state
        IntentFilter graintPermission = new IntentFilter(USBBroadCastReceiver.USB_PERMISSION);
        this.mContext.registerReceiver(this.mUsbRev, graintPermission);
    }


    /**
     * Dump usb files info
     * @param files
     */
    private void dumpFiles(List<UsbFile> files) {
        for (UsbFile file: files) {
            Utils.info(this, "=====================================");
            Utils.info(this, "Name: " + file.getName());
            Utils.info(this, "path: " + file.getAbsolutePath());
            Utils.info(this, "=====================================");
        }
    }

}
