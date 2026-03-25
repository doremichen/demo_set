/**
 * Copyright 2025 Adam Chen
 * Description: WelcomeBindingAct Activity use data binding
 * Author: Adam Chen
 * Date: 2025/06/27
 */
package com.adam.app.demoset.databinding;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.UIUtils;

public class WelcomeBindingAct extends AppCompatActivity {

    // view binding
    private ActivityWelcomeBindingBinding mBinding;

    // view model: welcome binding view model
    private WelcomeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityWelcomeBindingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // set view model
        mViewModel = new ViewModelProvider(this).get(WelcomeViewModel.class);
        mBinding.setViewModel(mViewModel);

        // set lifecycle owner
        mBinding.setLifecycleOwner(this);

        // observer user name to update welcome message
        mViewModel.userName.observe(this, name -> {
            mBinding.welcomeMessage.setText(getString(R.string.welcome_message, name));
        });

        // exit button click
        mBinding.btnExit.setOnClickListener(v -> {
            finish();
        });

    }
}