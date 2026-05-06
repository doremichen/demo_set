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

package com.adam.app.demoset.animation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.animation.view.GifView;
import com.adam.app.demoset.animation.viewmodel.GifViewModel;
import com.adam.app.demoset.databinding.ActivityDemoGifBinding;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;

/**
 * Activity for GIF Animation demo using MVVM and DataBinding.
 */
public class DemoGifAct extends AppCompatActivity implements GifView.GifStateListener {

    private GifViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActivityDemoGifBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_demo_gif);
        mViewModel = new ViewModelProvider(this).get(GifViewModel.class);
        
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);

        UIUtils.applySystemBarInsets(binding.getRoot(), binding.appBarWrapper);

        if (binding.gifView != null) {
            binding.gifView.setGifStateListener(this);
        }

        binding.btnExit.setOnClickListener(v -> finish());
    }

    @Override
    public void onPlayGif() {
        Utils.showToast(this, getString(R.string.demo_animation_start_msg));
    }

    @Override
    public void onStopGif() {
        Utils.showToast(this, getString(R.string.demo_animation_stop_msg));
    }

    @Override
    public void onError(String msg) {
        Utils.showToast(this, getString(R.string.Demo_animation_error_msg, msg));
        mViewModel.stopPlayback(); // Reset state on error
    }
}
