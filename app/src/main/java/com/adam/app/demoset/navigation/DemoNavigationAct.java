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

package com.adam.app.demoset.navigation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoNavigationBinding;
import com.adam.app.demoset.navigation.viewmodel.DemoNavigationViewModel;

public class DemoNavigationAct extends AppCompatActivity {

    // view binding
    private ActivityDemoNavigationBinding mBinding;
    // NavController
    private NavController mNavController;
    // appbar configuration
    private AppBarConfiguration mAppBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        mBinding = ActivityDemoNavigationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // init view model
        DemoNavigationViewModel viewModel = new ViewModelProvider(this).get(DemoNavigationViewModel.class);
        // data binding
        mBinding.setViewModel(viewModel);
        mBinding.setLifecycleOwner(this);

        // NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            return;
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.settingFragment)
                .build();

        // NavController
        mNavController = navHostFragment.getNavController();

        // bind BottomNavigationView to NavController
        NavigationUI.setupWithNavController(mBinding.bottomNav, mNavController);

        // bind navigation in action bar
        NavigationUI.setupActionBarWithNavController(this, mNavController);

    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) ||
                super.onSupportNavigateUp();
    }
}