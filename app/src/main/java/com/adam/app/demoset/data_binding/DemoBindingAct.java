/**
 * Copyright 2025 Adam Chen
 * Description: DemoBindingAct Activity use view binding
 * Author: Adam Chen
 * Date: 2025/06/27
 */
package com.adam.app.demoset.data_binding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoBindingBinding;

public class DemoBindingAct extends AppCompatActivity {

    // view binding
    private ActivityDemoBindingBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoBindingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // button click
        mBinding.btnNext.setOnClickListener(v -> {
            // get input text: name
            String name = mBinding.nameInput.getText().toString();
            //check if name is empty
            if (name.isEmpty()) {
                // show error message
                mBinding.nameInput.setError(getString(R.string.et_hint_please_input_nonempty_string));
                return;
            }

            // start welcome binding activity
            Intent intent = new Intent(this, WelcomeBindingAct.class);
            startActivity(intent);


        });

        // exit button click
        mBinding.btnExit.setOnClickListener(v -> {
            finish();
        });


    }
}