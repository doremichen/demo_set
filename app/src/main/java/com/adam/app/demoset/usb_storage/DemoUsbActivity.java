/**
 * Demo usb storage device
 */
package com.adam.app.demoset.usb_storage;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoUsbActivity extends AppCompatActivity implements USBBroadCastReceiver.UsbListener {

    // Local file
    private ImageButton mLocalFileIB;
    private ListView mLocalFilesLV;

    // Usb file
    private ImageButton mUsbFileIB;
    private ListView mUsbFilesLV;

    // progress view
    private TextView mProgressTV;

    private String mLocalRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mLocalCurrentPath = "";

    private UsbHelper mUsbHelper;
    private FileListInfo<File> mLocalFileListInfo;
    private FileListInfo<UsbFile> mUsbFileListInfo;


    // file list info
    private class FileListInfo<T> {
        //file arraylist
        private ArrayList<T> mList;
        // file list adapter
        private FileListAdapter<T> mAdapter;

        /**
         * Initial file list info
         * @param context: Ui context
         */
        public FileListInfo(Context context) {
            this.mList = new ArrayList<>();
            this.mAdapter = new FileListAdapter<>(context, this.mList);
        }

        /**
         * Provider the list adapter
         * @return
         */
        public FileListAdapter<T> getListAdapter() {
            return this.mAdapter;
        }

        /**
         * Get file from list according the position
         * @param position
         * @return
         */
        public T getFile(int position) {
            T file = this.mList.get(position);
            return file;
        }

        /**
         * init file list
         * @param list
         */
        public void initFileList(List<T> list) {
            Utils.info(this, "initFileList +++");
            this.mList.clear();
            this.mList.addAll(list);
            Utils.info(this, "initFileList xxx");
        }


        /**
         * update list item
         * @param list
         */
        public void updateFileList(List<T> list) {
            // clear
            this.mList.clear();
            // add data from input
            this.mList.addAll(list);
            // notify list adapter
            this.mAdapter.notifyDataSetChanged();
        }

        /**
         * clear data in list
         */
        public void emptyData() {
            this.mList.clear();
            this.mAdapter.notifyDataSetChanged();
        }

    }


    //==============================================================================
    // The following methods are callback.
    @Override
    public void onInsert(UsbDevice device) {
        Utils.info(this, "onInsert");

    }

    @Override
    public void onRemove(UsbDevice device) {
        Utils.info(this, "onRemove");
    }

    @Override
    public void onHavePermission(UsbDevice device) {
        Utils.info(this, "onHavePermission");
    }

    @Override
    public void onFail(UsbDevice device) {
        Utils.info(this, "onFail");
    }

    //==============================================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate +++");
        setContentView(R.layout.activity_demo_usb_device);
        // initial ui widget
        this.mLocalFilesLV = this.findViewById(R.id.local_file_lv);
        this.mUsbFilesLV = this.findViewById(R.id.usb_file_lv);
        this.mLocalFileIB = this.findViewById(R.id.local_backspace_iv);
        this.mUsbFileIB = this.findViewById(R.id.usb_backspace_iv);
        this.mProgressTV = this.findViewById(R.id.show_progress_tv);

        initLocalView();
        initUsbView();
        Utils.info(this, "onCreate xxx");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.local_backspace_iv:
                Utils.info(this, "onClick@LocalFileList");
                if (!mLocalCurrentPath.equals(mLocalRootPath)) {
                    openLocalFile(new File(mLocalCurrentPath).getParentFile());
                }
                break;
            case R.id.usb_backspace_iv:
                Utils.info(this, "onClick@UsbFileList");
                if (mUsbHelper.getFolder(true) != null) {
                    openUsbFile(mUsbHelper.getFolder(true));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // release
        this.mUsbHelper.finishUsbHelper();
    }

    /**
     * initial local list view
     */
    private void initLocalView() {
        Utils.info(this, "initLocalView +++");
        mLocalFileListInfo = new FileListInfo<>(this);
        this.mLocalCurrentPath = this.mLocalRootPath;
        // init file list
        File[] files = new File(this.mLocalRootPath).listFiles();
        mLocalFileListInfo.initFileList(Arrays.asList(files));
        // build list view
        this.mLocalFilesLV.setAdapter(mLocalFileListInfo.getListAdapter());
        this.mLocalFilesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Utils.info(this, "onItemClick@LocalFileList");
                Utils.info(this, "position: " + String.valueOf(i));
                File file = mLocalFileListInfo.getFile(i);
                // open directory/file
                openLocalFile(file);
            }
        });
        Utils.info(this, "initLocalView xxx");
    }

    /**
     * initial usb list view
     */
    private void initUsbView() {
        Utils.info(this, "initUsbView +++");
        mUsbFileListInfo = new FileListInfo<>(this);
        mUsbHelper = new UsbHelper(this, this);
        // build list view
        this.mUsbFilesLV.setAdapter(mUsbFileListInfo.getListAdapter());
        this.mUsbFilesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Utils.info(this, "onItemClick@UsbFileList");
                Utils.info(this, "position: " + String.valueOf(i));
                UsbFile file = mUsbFileListInfo.getFile(i);
                // open usb file
                openUsbFile(file);
            }
        });

        // update file list from the usb[0] storage.
        updateUsbList(0);

        Utils.info(this, "initUsbView xxx");
    }



    //===============================================================================
    // copy task

    private class Params<T1, T2> {
        private T1 mFrom;
        private T2 mTo;

        public Params(@NonNull T1 t1, @NonNull T2 t2) {
            this.mFrom = t1;
            this.mTo = t2;
        }
    }


    /**
     * Copy file from Usb to local storage
     */
    private class CopyFileFromUsbTask extends AsyncTask<Params, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Params... params) {
            Utils.info(this, "doInBackground@CopyFileFromUsbTask +++");
            UsbFile file = (UsbFile) params[0].mFrom;
            String targetPath = (String) params[0].mTo;
            // save file from usb
            boolean result = mUsbHelper.saveFileFromUsb(file, targetPath, new UsbHelper.DownloadListener() {
                @Override
                public void progress(int value) {
                    Utils.info(this, "progress@CopyFileFromUsbTask");
                    Utils.info(this, "value: " + String.valueOf(value));
                    publishProgress(value);
                }
            });
            Utils.info(this, "doInBackground@CopyFileFromUsbTask xxx");
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Utils.info(this, "onProgressUpdate@CopyFileFromUsbTask +++");
            Utils.info(this, "value: " + String.valueOf(values[0]));
            // update progress view
            String text = "From Usb " + mUsbHelper.getFolder(false).getName()
                    + "\nTo Local " + mLocalCurrentPath
                    + "\n Progress : " + String.valueOf(values[0]);

            // show text view
            mProgressTV.setText(text);
            Utils.info(this, "onProgressUpdate@CopyFileFromUsbTask xxx");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Utils.info(this, "onPostExecute@CopyFileFromUsbTask +++");
            Utils.info(this, "result: " + result.toString());
            if (Boolean.FALSE.equals(result)) {
                Utils.showToast(DemoUsbActivity.this, "Copy fail...");
                return;
            }

            // open local list view
            openLocalFile(new File(mLocalCurrentPath));
            Utils.info(this, "onPostExecute@CopyFileFromUsbTask xxx");
        }
    }

    /**
     * Copy file to Usb from local storage
     */
    private class CopyFileToUsbTask extends AsyncTask<Params, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // update progress view
            String text = "From Local : " + mLocalCurrentPath
                    + "\nTo Usb : " + mUsbHelper.getFolder(true).getName()
                    + "\nProgress : " + String.valueOf(values[0]);
            // show text view
            mProgressTV.setText(text);
        }

        @Override
        protected Boolean doInBackground(Params... params) {
            Utils.info(this, "doInBackground@CopyFileToUsbTask +++");
            File from = (File)params[0].mFrom;
            UsbFile to = (UsbFile) params[0].mTo;
            // save file to usb
            boolean result = mUsbHelper.saveFileToUsb(from, to, new UsbHelper.DownloadListener() {
                @Override
                public void progress(int value) {
                    Utils.info(this, "progress@CopyFileToUsbTask");
                    Utils.info(this, "value: " + String.valueOf(value));
                    publishProgress(value);
                }
            });
            Utils.info(this, "doInBackground@CopyFileToUsbTask xxx");
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Utils.info(this, "onPostExecute@CopyFileToUsbTask +++");
            Utils.info(this, "result: " + result.toString());
            if (Boolean.FALSE.equals(result)) {
                Utils.showToast(DemoUsbActivity.this, "Copy fail...");
                return;
            }

            // open usb list view
            openUsbFile(mUsbHelper.getFolder(true));
            Utils.info(this, "onPostExecute@CopyFileToUsbTask xxx");
        }
    }

    /**
     * open file in local storage.
     * @param file
     */
    private void openLocalFile(File file) {
        Utils.info(this, "openLocalFile +++");
        // directory
        if (file.isDirectory()) {
            this.mLocalFileListInfo.updateFileList(Arrays.asList(file.listFiles()));
            this.mLocalCurrentPath = file.getAbsolutePath();
            Utils.info(this, "openLocalFile: directory xxx");
            return;
        }

        //copy file to usb
        CopyFileToUsbTask task = new CopyFileToUsbTask();
        Params<File, UsbFile> params = new Params<>(file, mUsbHelper.getFolder(true));
        task.execute(params);
        Utils.info(this, "openLocalFile: file xxx");
    }

    /**
     *
     * open file in usb storage
     * @param file
     */
    private void openUsbFile(UsbFile file) {
        Utils.info(this, "openUsbFile +++");
        // directory
        if (file.isDirectory()) {
            this.mUsbFileListInfo.updateFileList(mUsbHelper.readFilesFrom(file));
            Utils.info(this, "openUsbFile: directory xxx");
            return;
        }
        String filePath = mLocalCurrentPath + File.separator + file.getName();
        // copy file from usb
        CopyFileFromUsbTask task = new CopyFileFromUsbTask();
        Params<UsbFile, String> params = new Params<>(file, filePath);
        task.execute(params);
        Utils.info(this, "openUsbFile: file xxx");
    }

    /**
     * Update usb list view
     * @param position
     */
    private void updateUsbList(int position) {
        Utils.info(this, "updateUsbList +++");
        // usb device list
        UsbMassStorageDevice[] usbMassStorageDevices = mUsbHelper.getDeviceList();
        if (usbMassStorageDevices.length <= 0) {
            Utils.info(this, "No Usb device");
            this.mUsbFileListInfo.emptyData();
            return;
        }

        // update file list in usb storage
        this.mUsbFileListInfo.updateFileList(mUsbHelper.readFilesFrom(usbMassStorageDevices[position]));
        Utils.info(this, "updateUsbList xxx");
    }
}
