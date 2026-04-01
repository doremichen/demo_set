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

package com.adam.app.demoset.databinding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.utils.UIUtils;

public class DemoBindingAct extends AppCompatActivity {

    // view binding
    private ActivityDemoBindingBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoBindingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // button click
        mBinding.btnNext.setOnClickListener(v -> {
            // hide soft keyboard
            Utils.hideSoftKeyBoardFrom(this, v);
            // get input text: name
            String name = mBinding.nameInput.getText().toString();
            //check if name is empty
            if (name.isEmpty()) {
                // show error message
                mBinding.nameInput.setError(getString(R.string.et_hint_please_input_nonempty_string));
                return;
            }

            // clear input text
            mBinding.nameInput.setText("");

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