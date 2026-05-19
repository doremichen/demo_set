/*
 * Copyright (c) 2024 Adam Chen
 */

package com.adam.app.demoset.encryption;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoEncryptionBinding;
import com.adam.app.demoset.encryption.adapter.EncryptionAdapter;
import com.adam.app.demoset.encryption.adapter.FullEncryptionAdapter;
import com.adam.app.demoset.encryption.viewmodel.EncryptionViewModel;
import com.adam.app.demoset.encryption.viewmodel.FullEncryptionViewModel;

public class DemoEncryptionAct extends AppCompatActivity {

    private EncryptionViewModel mFieldViewModel;
    private FullEncryptionViewModel mFullViewModel;
    private ActivityDemoEncryptionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Data Binding initialization
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_encryption);
        mBinding.setLifecycleOwner(this);
        
        mFieldViewModel = new ViewModelProvider(this).get(EncryptionViewModel.class);
        mFullViewModel = new ViewModelProvider(this).get(FullEncryptionViewModel.class);

        // Bind ViewModels to the XML variables
        mBinding.setFieldViewModel(mFieldViewModel);
        mBinding.setFullViewModel(mFullViewModel);

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        // Section 1: Field Level
        EncryptionAdapter fieldAdapter = new EncryptionAdapter();
        mBinding.rvItemsField.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvItemsField.setAdapter(fieldAdapter);
        mFieldViewModel.getAllItems().observe(this, fieldAdapter::submitList);

        // Section 2: Full DB
        FullEncryptionAdapter fullAdapter = new FullEncryptionAdapter();
        mBinding.rvItemsFull.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvItemsFull.setAdapter(fullAdapter);
        mFullViewModel.getAllItems().observe(this, fullAdapter::submitList);
    }
}
