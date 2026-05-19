/*
 * Copyright (c) 2024 Adam Chen
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
package com.adam.app.demoset.encryption.sharedprefs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoEncryptedSharedPrefsBinding;
import com.adam.app.demoset.encryption.sharedprefs.viewmodel.EncryptedPrefsViewModel;
import com.adam.app.demoset.utils.Utils;

public class DemoEncryptedSharedPrefsAct extends AppCompatActivity {

    private EncryptedPrefsViewModel mViewModel;
    private ActivityDemoEncryptedSharedPrefsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_encrypted_shared_prefs);
        mBinding.setLifecycleOwner(this);

        mViewModel = new ViewModelProvider(this).get(EncryptedPrefsViewModel.class);
        mBinding.setViewModel(mViewModel);

        // observer
        mViewModel.isHideKeyboard().observe(this, this::onHideKeyboard);

    }

    private void onHideKeyboard(Boolean isHide) {
        if (!isHide) return;
        // hide keyboard
        Utils.hideSoftKeyBoardFrom(this, mBinding.etValue);
        // consume event
        mViewModel.ConsumeHideKeyboardEvent();
    }
}

