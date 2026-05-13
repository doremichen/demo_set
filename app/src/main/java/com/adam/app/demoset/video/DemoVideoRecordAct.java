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

package com.adam.app.demoset.video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoVideoRecordBinding;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.video.viewmodel.VideoRecordViewModel;

import java.io.File;

public class DemoVideoRecordAct extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 0x2467;
    private static final String[] RECORD_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private ActivityDemoVideoRecordBinding mBinding;
    private VideoRecordViewModel mViewModel;
    private boolean mIsAllow;
    private String mFilePath;

    private final TextureView.SurfaceTextureListener mTextureViewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Utils.info(this, "onSurfaceTextureAvailable enter");
            mViewModel.openCamera(mBinding.surfaceRecord);
            mViewModel.configureTransform(width, height, getWindowManager().getDefaultDisplay().getRotation());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mViewModel.configureTransform(width, height, getWindowManager().getDefaultDisplay().getRotation());
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_video_record);
        mViewModel = new ViewModelProvider(this).get(VideoRecordViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.rootLayout, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        UIUtils.hideSystemBar(getWindow());

        mBinding.timer.setOnChronometerTickListener(chronometer -> 
            mViewModel.updateTimer(SystemClock.elapsedRealtime())
        );

        if (Utils.askPermission(this, RECORD_PERMISSION, REQUEST_PERMISSION_CODE)) {
            mIsAllow = true;
        }

        mViewModel.startCameraThread();

        observeViewModel();
    }

    private void observeViewModel() {
        mViewModel.isRecording.observe(this, isRecording -> {
            if (isRecording) {
                startRecordingAnimation();
                mBinding.timer.start();
            } else {
                stopRecordingAnimation();
                mBinding.timer.stop();
            }
        });

        mViewModel.errorResult.observe(this, result -> 
            Utils.showAlertDialog(this, "Open camera error result: " + result, (dialog, which) -> finish())
        );

        mViewModel.failMsg.observe(this, msg -> 
            Utils.showAlertDialog(this, msg, null)
        );

        mViewModel.infoMsg.observe(this, msg -> 
            Utils.showToast(this, msg)
        );

        mViewModel.filePath.observe(this, path -> mFilePath = path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == RECORD_PERMISSION.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        mIsAllow = false;
                        this.finish();
                        return;
                    }
                }
                mIsAllow = true;
                if (mBinding.surfaceRecord.isAvailable()) {
                    mViewModel.openCamera(mBinding.surfaceRecord);
                    mViewModel.configureTransform(mBinding.surfaceRecord.getWidth(), mBinding.surfaceRecord.getHeight(), getWindowManager().getDefaultDisplay().getRotation());
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        UIUtils.hideSystemBar(getWindow());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsAllow) {
            if (mBinding.surfaceRecord.isAvailable()) {
                mViewModel.openCamera(mBinding.surfaceRecord);
                mViewModel.configureTransform(mBinding.surfaceRecord.getWidth(), mBinding.surfaceRecord.getHeight(), getWindowManager().getDefaultDisplay().getRotation());
            } else {
                mBinding.surfaceRecord.setSurfaceTextureListener(mTextureViewListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsAllow) {
            if (Boolean.TRUE.equals(mViewModel.isRecording.getValue())) {
                onRecord(null);
            }
            mViewModel.closeCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.stopCameraThread();
    }

    public void onRecord(View v) {
        mViewModel.toggleRecord();
    }

    private void startRecordingAnimation() {
        AlphaAnimation blink = new AlphaAnimation(1.0f, 0.0f);
        blink.setDuration(500);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        mBinding.recDot.startAnimation(blink);
    }

    private void stopRecordingAnimation() {
        mBinding.recDot.clearAnimation();
    }

    public void onPlayVideo(View v) {
        Intent intent = playVideo();
        if (intent != null) {
            this.startActivity(intent);
        }
    }

    public void onExit(View v) {
        this.finish();
    }

    private Intent playVideo() {
        if (this.mFilePath == null) {
            Utils.showToast(this, getString(R.string.demo_video_record_invalid_path_toast));
            return null;
        }
        File file = new File(mFilePath);
        if (!file.exists()) {
            Utils.showToast(this, "No this file");
            return null;
        }
        
        Uri contentUri = FileProvider.getUriForFile(this, "com.adam.app.demoset.filemanager.provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(contentUri, "video/*");
        return intent;
    }
}
