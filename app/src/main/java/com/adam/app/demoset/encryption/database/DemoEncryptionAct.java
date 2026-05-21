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
package com.adam.app.demoset.encryption.database;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoEncryptionBinding;
import com.adam.app.demoset.encryption.database.adapter.FieldLevelEncryptionAdapter;
import com.adam.app.demoset.encryption.database.adapter.FullDbEncryptionAdapter;
import com.adam.app.demoset.encryption.database.viewmodel.FieldLevelEncryptionViewModel;
import com.adam.app.demoset.encryption.database.viewmodel.FullDbEncryptionViewModel;

public class DemoEncryptionAct extends AppCompatActivity {

    private FieldLevelEncryptionViewModel mFieldViewModel;
    private FullDbEncryptionViewModel mFullViewModel;
    private ActivityDemoEncryptionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Data Binding initialization
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_encryption);
        mBinding.setLifecycleOwner(this);
        
        mFieldViewModel = new ViewModelProvider(this).get(FieldLevelEncryptionViewModel.class);
        mFullViewModel = new ViewModelProvider(this).get(FullDbEncryptionViewModel.class);

        // Bind ViewModels to the XML variables
        mBinding.setFieldViewModel(mFieldViewModel);
        mBinding.setFullViewModel(mFullViewModel);

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        // Section 1: Field Level
        FieldLevelEncryptionAdapter fieldAdapter = new FieldLevelEncryptionAdapter();
        mBinding.rvItemsField.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvItemsField.setAdapter(fieldAdapter);
        mFieldViewModel.getAllItems().observe(this, fieldAdapter::submitList);

        // Section 2: Full DB
        FullDbEncryptionAdapter fullAdapter = new FullDbEncryptionAdapter();
        mBinding.rvItemsFull.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvItemsFull.setAdapter(fullAdapter);
        mFullViewModel.getAllItems().observe(this, fullAdapter::submitList);
    }
}
