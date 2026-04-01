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

package com.adam.app.demoset.tablelayout;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.databinding.ActivityDemoTableBinding;
import com.adam.app.demoset.tablelayout.viewmodel.TicTacToeViewModel;
import com.adam.app.demoset.utils.UIUtils;

public class DemoTableAct extends AppCompatActivity {

    // button array size 9
    private final Button[] mButtons = new Button[9];

    // view biding
    private ActivityDemoTableBinding mBinding;
    // view model
    private TicTacToeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // data binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_demo_table);

        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.scrollView);

        // init view model
        mViewModel = new ViewModelProvider(this).get(TicTacToeViewModel.class);
        // data binding view model and activity
        mBinding.setViewModel(mViewModel);
        mBinding.setActivity(this);
        // data binding lifecycle owner
        mBinding.setLifecycleOwner(this);

        // update message
        mViewModel.getMessageLiveData().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Utils.showToast(this, message);
            }
        });

    }
}
