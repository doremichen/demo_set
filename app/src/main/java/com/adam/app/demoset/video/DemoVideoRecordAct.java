package com.adam.app.demoset.video;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.io.File;

public class DemoVideoRecordAct extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 0x2467;
    private static final String KEY_PERMISSION = "key.permission";

    private Button mBtnRecorder;
    private Button mBtnPlayVideo;

    private Chronometer mTimer;

    private boolean mIsAllow;

    private MyRecordVideoController mController;

    private TextureView mSurfaceView;

    private String mFilePath;

    //record  permission
    private static final String[] RECORD_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureAvailable enter");
            // Open camera
            mController.openCamera(DemoVideoRecordAct.this, mSurfaceView);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private MyRecordVideoController.ControllerListener mControllerlistener = new MyRecordVideoController.ControllerListener() {
        @Override
        public void onError(int result) {
            // Show alert dialog
            Utils.showAlertDialog(DemoVideoRecordAct.this, "Open camera error result: " + result, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Finish UI
                    DemoVideoRecordAct.this.finish();
                }
            });
        }

        @Override
        public void onFail(String msg) {
            // Show alert dialog
            Utils.showAlertDialog(DemoVideoRecordAct.this, msg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // not need to do something
                }
            });
        }

        @Override
        public void onInfo(String msg) {
            Utils.showToast(DemoVideoRecordAct.this, msg);
        }

        @Override
        public String getPath() {
            Utils.info(this, "getPath enter");
            File fileDir = DemoVideoRecordAct.this.getFilesDir();
            String fileName =  System.currentTimeMillis() + ".mp4";
            File outputDir = new File(fileDir, "videos");
            if (!outputDir.exists()) {
                outputDir.mkdirs(); // should succeed
            }
            File outputFile = new File(outputDir, fileName);

            mFilePath = outputFile.getPath();
            return mFilePath;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_video_record);

        mSurfaceView = this.findViewById(R.id.surface_record);
        mBtnRecorder = this.findViewById(R.id.btn_start_rec);
        mBtnPlayVideo = this.findViewById(R.id.play_vedio);
        mTimer = this.findViewById(R.id.timer);
        // update timer info
        this.mTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time  - h*3600000)/60000;
                int s= (int)(time  - h*3600000 - m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                // update info
                mTimer.setText(TextUtils.concat(hh, ":", mm, ":", ss));

            }
        });


        mController = MyRecordVideoController.newInstance();

        mController.registerListener(mControllerlistener);

        if (Utils.askPermission(this, RECORD_PERMISSION, REQUEST_PERMISSION_CODE)) {
            // Permission is granted
            mIsAllow = true;
        }

        mController.startCameraThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Utils.info(this, "onRequestPermissionsResult enter");
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == RECORD_PERMISSION.length) {
                for (int result : grantResults) {
                    Utils.info(this, "result = " + result);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        mIsAllow = false;
                        // permission denied
                        this.finish();
                        break;
                    } else {
                        mIsAllow = true;
                    }
                }
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Hide systemUI
        View decoreView = this.getWindow().getDecorView();
        Utils.hideSystemUI(decoreView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.info(this, "onResume mIsAllow = " + mIsAllow);
        if (mIsAllow) {
            if (mSurfaceView.isAvailable()) {
                // Open camera
                mController.openCamera(this, mSurfaceView);

            } else {
                this.mSurfaceView.setSurfaceTextureListener(mTextureViewListener);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.info(this, "onPause mIsAllow = " + mIsAllow);
        if (mIsAllow) {
            setEnabledRecording(false);
            mController.closeCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.info(this, "onDestroy mIsAllow = " + mIsAllow);
        mController.stopCameraThread();
    }

    public void onRecord(View v) {
        Utils.info(this, "onRecord: " + String.valueOf(mController.isRecording()));
        setEnabledRecording(!mController.isRecording());
        // change play button function
        int visibility = (!mController.isRecording())? View.VISIBLE: View.INVISIBLE;
        mBtnPlayVideo.setVisibility(visibility);

    }

    private void setEnabledRecording(boolean enabled) {
        Utils.info(this, "setEnabledRecording: " + String.valueOf(enabled));
        if (enabled) {
            // start timer
            this.mTimer.setBase(SystemClock.elapsedRealtime());
            this.mTimer.start();
            mController.startRecord(this);
            mBtnRecorder.setText(this.getResources().getString(R.string.action_stop_record));
        } else {
            // stop timer
            this.mTimer.stop();
            mController.stopRecord();
            mBtnRecorder.setText(this.getResources().getString(R.string.action_start_record));
        }
    }

    public void onPlayVideo(View v) {
        Utils.info(this, "onPlayVideo");

        // Start play video app
        Intent intent = playVideo();
        if (intent != null) {
            this.startActivity(intent);
        }
    }

    public void onExit(View v) {
        this.finish();
    }

    private Intent playVideo() {
        Utils.info(this, "playVedioIntent");
        Utils.info(this, "mFilePath = " + mFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (this.mFilePath == null) {
            Utils.showToast(this, "Please record first!!!");
            return null;
        }
        // Check file exists
        File file = new File(mFilePath);
        if (!file.exists()) {
            Utils.showToast(this, "No this file");
        } else {
            Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.filemanager.provider", file);
            Utils.showToast(this, "<content>" + contentUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "video/*");

        }

        return intent;
    }
}
