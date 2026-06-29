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

package com.adam.app.demoset.graphics;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.graphics.strategies.CubeStrategy;
import com.adam.app.demoset.graphics.viewmodel.GraphicsViewModel;
import com.adam.app.demoset.databinding.ActivityDemoGraphicsBinding;
import com.adam.app.demoset.utils.UIUtils;

/**
 * Activity focusing solely on UI observation and system lifecycle forwarding.
 */
public class DemoGraphicsAct extends AppCompatActivity {

    private ActivityDemoGraphicsBinding mBinding;
    private GraphicsViewModel mViewModel;
    private boolean mIsGLRendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_graphics);
        mViewModel = new ViewModelProvider(this).get(GraphicsViewModel.class);
        
        mBinding.setVm(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Observe strategy changes to update UI components
        mViewModel.getActiveStrategy().observe(this, activeStrategy -> {
            if (activeStrategy instanceof CubeStrategy) {
                setupGLPipeline((CubeStrategy) activeStrategy);
            } else {
                mBinding.graphicsView.setStrategy(activeStrategy);
            }
        });

        // Observe animation switch directly for 2D view
        mViewModel.getIsAnimating().observe(this, isAnimating -> {
            mBinding.graphicsView.setAnimating(isAnimating);
        });

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);
        mBinding.btnExit.setOnClickListener(v -> finish());
    }

    private void setupGLPipeline(CubeStrategy strategy) {
        if (!mIsGLRendererSet) {
            mBinding.glSurfaceView.setEGLContextClientVersion(3);
            mBinding.glSurfaceView.setRenderer(strategy.getRenderer());
            mBinding.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            mIsGLRendererSet = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Notify VM first to stop all animators (2D & 3D)
        if (mViewModel != null) {
            mViewModel.onActivityPause();
        }

        // Suspend GL rendering
        if (mBinding != null && mIsGLRendererSet) {
            mBinding.glSurfaceView.onPause();
        }
        
        // Clear focus to reduce IME (keyboard) warnings on Samsung devices
        if (mBinding != null) {
            mBinding.getRoot().clearFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fix for NPE: GLSurfaceView requires setRenderer() before onResume()
        if (mBinding != null && mIsGLRendererSet) {
            mBinding.glSurfaceView.onResume();
        }

        // Notify VM to restore controller state
        if (mViewModel != null) {
            mViewModel.onActivityResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            // Note: Do NOT set renderer to null on GLSurfaceView, it's not supported.
            // Just clearing binding reference to avoid context leak.
            mBinding = null;
        }
    }
}
