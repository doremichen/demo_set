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

package com.adam.app.demoset.datastore.java;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ActivityDataStoreDemoBinding;
import com.adam.app.demoset.datastore.java.model.SettingsModel;
import com.adam.app.demoset.datastore.java.repository.SettingsRepository;
import com.adam.app.demoset.datastore.java.viewmodel.SettingsViewModel;
import com.adam.app.demoset.utils.LocaleUtils;
import com.adam.app.demoset.utils.LogAdapter;
import com.adam.app.demoset.utils.UIUtils;
import com.adam.app.demoset.utils.Utils;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataStoreDemoAct extends AppCompatActivity {

    // view binding
    private ActivityDataStoreDemoBinding mBinding;
    // view model
    private SettingsViewModel mViewModel;
    // log adapter
    private LogAdapter mLogAdapter;
    private String mCurrentLanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "[onCreate]");

        // view binding
        mBinding = ActivityDataStoreDemoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        UIUtils.applySystemBarInsets(mBinding.getRoot(), mBinding.appBarWrapper);

        // init view model
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // bind view model
        mBinding.setVm(mViewModel);
        // set lifecycle owner
        mBinding.setLifecycleOwner(this);


        // slider listener
        mBinding.sliderFontSize.addOnChangeListener((slider, value, fromUser) -> {
            // log
            Utils.info(this, "slider value: " + value);
            if (fromUser) {
                //TODO: 這裡可以選擇更新 ViewModel 的臨時變數，讓摘要區跳動
            }
        });

        // slider touch listener
        mBinding.sliderFontSize.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {

            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                Utils.info(this, "start tracking");
                //TODO: start
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                Utils.info(this, "stop tracking");
                //TODO: stop
                // update font size
                mViewModel.setFontSize(slider.getValue());
            }
        });

        initLogList();

        observerData();

    }

    private void observerData() {
        Utils.info(this, "[observerData]");
        mViewModel.getSettings().observe(this, this::updateSettings);
        mViewModel.getLog().observe(this, this::updateLog);
    }

    private void updateSettings(SettingsModel settings) {
        Utils.info(this, "[updateSettings]");
        if (settings == null) {
            Utils.error(this, "settings is null");
            return;
        }

        boolean isDarkMode = settings.isDarkMode();
        String language = settings.getLanguage();
        float fontSize = settings.getFontSize();

        // set dark mode
        int themeMode = isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;
        // check if dark mode is changed
        if (AppCompatDelegate.getDefaultNightMode() != themeMode) {
            Utils.info(this, "set dark mode: " + themeMode);
            AppCompatDelegate.setDefaultNightMode(themeMode);
        }

        // set language
        // get app language
        LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
        String currentLang = currentLocales.isEmpty() ? Locale.getDefault().getLanguage() : currentLocales.get(0).getLanguage();

        if (!currentLang.equals(language)) {
            Utils.info(this, "set language: " + language);
            LocaleUtils.applyLocale(this, language);
        }

//        if (!mCurrentLanguage.equals(language)) {
//            mCurrentLanguage = language;
//            // 如果是 API 33+ 建議使用此方法，它會自動處理持久化與重啟
//            // LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(mCurrentLanguage);
//            // AppCompatDelegate.setApplicationLocales(appLocale);
//
//            updateLocale(language);
//            // restart activity
//            //recreate(); // 注意：這可能會導致部分 UI 狀態丟失
//        }
        // update language button
        mBinding.toggleGroupLang.check(mCurrentLanguage.equals(SettingsRepository.LANG_ZH_TW)
                ? R.id.btn_lang_zh : R.id.btn_lang_en);


        // set font size
        float min = mBinding.sliderFontSize.getValueFrom();
        float max = mBinding.sliderFontSize.getValueTo();

        float safeValue = Math.min(Math.max(fontSize, min), max);
        mBinding.sliderFontSize.setValue(safeValue);

    }

    /**
     * Update locale
     *
     * @param langCode language code
     */
    private void updateLocale(String langCode) {
        Utils.info(this, "[updateLocale]");
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        // create configuration context
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void updateLog(List<String> strings) {
        Utils.info(this, "[updateLog]");
        mLogAdapter.submitList(new ArrayList<>(strings), () -> {
            mBinding.rvLogs.scrollToPosition(mLogAdapter.getItemCount() - 1);
        });
    }

    private void initLogList() {
        Utils.info(this, "[initLogList]");

        mLogAdapter = new LogAdapter();

        // lineaer layout manager
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);

        mBinding.rvLogs.setLayoutManager(manager);
        mBinding.rvLogs.setAdapter(mLogAdapter);
    }
}