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

package com.adam.app.demoset.shareprovider.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDemoShareProvidBinding;
import com.adam.app.demoset.shareprovider.presentation.viewmodel.ShareViewModel;
import com.adam.app.demoset.utils.UIUtils;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Demo activity for ShareActionProvider using MVVM and Clean Architecture.
 * This activity delegates ShareActionProvider management to the ViewModel.
 */
@AndroidEntryPoint
public class DemoShareProvideAct extends AppCompatActivity {

    private ShareViewModel mViewModel;
    private ActivityDemoShareProvidBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Data Binding
        mBinding = ActivityDemoShareProvidBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setLifecycleOwner(this);

        // Apply UI configurations
        UIUtils.hideSystemBar(getWindow());

        // Set up observers for UI events and data changes
        setupObservers();

        // Initialize UI listeners
        setupListeners();
    }

    /**
     * Set up LiveData observers.
     */
    private void setupObservers() {
        // Handle manual share button click (via Intent Chooser)
        mViewModel.getManualShareEvent().observe(this, intent -> {
            if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, getString(R.string.demo_shared_provider_shared_btn)));
            }
        });

        // Observe Exit Event to finish the activity
        mViewModel.getExitEvent().observe(this, exit -> {
            if (Boolean.TRUE.equals(exit)) {
                finish();
            }
        });
    }

    /**
     * Set up UI interactive listeners.
     */
    private void setupListeners() {
        // Listen to Chip selection to update share content type (Text or Image)
        mBinding.chipGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            mViewModel.getIsImageType().setValue(checkedId == R.id.chip_image);
            // Internal logic is handled by ViewModel observers
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_share, menu);

        // Find the MenuItem and get the ShareActionProvider
        MenuItem itemShare = menu.findItem(R.id.menu_shared);
        ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(itemShare);

        // Pass the provider to ViewModel to handle its logic
        mViewModel.setShareActionProvider(provider);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selections
        int id = item.getItemId();
        if (id == R.id.demo_exit) {
            mViewModel.onExitClicked();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
