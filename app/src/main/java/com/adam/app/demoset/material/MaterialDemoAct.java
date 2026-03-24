/**
 * Copyright (C) Adam demo app Project. All rights reserved.
 * <p>
 * Description: This is the material demo activity.
 * </p>
 * Author: Adam Chen
 * Date: 2026/03/24
 */
package com.adam.app.demoset.material;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityMaterialDemoBinding;
import com.adam.app.demoset.material.viewmodel.ComponentViewModel;

public class MaterialDemoAct extends AppCompatActivity {

    // view binding
    private ActivityMaterialDemoBinding mBbinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // view binding
        mBbinding = ActivityMaterialDemoBinding.inflate(getLayoutInflater());
        setContentView(mBbinding.getRoot());

        // init view model
        ComponentViewModel vm = new ViewModelProvider(this).get(ComponentViewModel.class);
        // bind view model
        mBbinding.setVm(vm);
        mBbinding.setActivity(this);
        mBbinding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}