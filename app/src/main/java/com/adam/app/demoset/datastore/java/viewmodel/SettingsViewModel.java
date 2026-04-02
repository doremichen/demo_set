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

package com.adam.app.demoset.datastore.java.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adam.app.demoset.datastore.java.model.SettingsModel;
import com.adam.app.demoset.datastore.java.repository.SettingsRepository;
import com.adam.app.demoset.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import com.adam.app.demoset.R;


public class SettingsViewModel extends AndroidViewModel {

    // repository
    private final SettingsRepository mRepository;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    // live data ---
    // user settings
    private final MutableLiveData<SettingsModel> mSettings = new MutableLiveData<>();
    public LiveData<SettingsModel> getSettings() {
        return mSettings;
    }
    // logs
    private final MutableLiveData<List<String>> mLogs = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<String>> getLog() {
        return mLogs;
    }


    public SettingsViewModel(@NonNull Application application) {
        super(application);
        Utils.info(this, "[SettingsViewModel]");
        // init repository
        mRepository = SettingsRepository.getInstance(application);

        mDisposables.add(// register observer
                mRepository.getSettings()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(settings -> {
                            Utils.info(this, "[getSettings]");
                            mSettings.setValue(settings);
                            // add log
                            addLog("get settings: " + settings);
                        }, throwable -> {
                            // add log
                            addLog("get settings error: " + throwable.getMessage());
                        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // clear disposables
        mDisposables.clear();
    }

    /**
     * toggle dark mode
     * @param isDarkMode dark mode
     */
    public void toggleDarkMode(boolean isDarkMode) {
        Utils.info(this, "[toggleDarkMode]");
        mDisposables.add(mRepository.setDarkMode(isDarkMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        prefs -> addLog("Dark mode updated to " + isDarkMode),
                        e -> addLog("Update failed: " + e.getMessage())
                ));
        // add log
        addLog("toggle dark mode: " + isDarkMode);
    }

    /**
     * set language
     * @param language language
     */
    public void changeLanguage(String language) {
        Utils.info(this, "[changeLanguage]");
        mDisposables.add(mRepository.setLanguage(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        prefs -> addLog("Language updated to " + language),
                        e -> addLog("Update failed: " + e.getMessage())
                ));
        // add log
        addLog("change language: " + language);
    }

    /**
     * is language selected
     * @param language language
     * @return true if selected
     */
    public boolean isLanguageSelected(String language) {
        Utils.info(this, "[isLanguageSelected]");
        if (mSettings.getValue() == null) return false;
        return mSettings.getValue().getLanguage().equals(language);
    }

    /**
     * set font size
     * @param fontSize font size
     */
    public void setFontSize(float fontSize) {
        Utils.info(this, "[setFontSize]");
        mDisposables.add(mRepository.setFontSize(fontSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        prefs -> addLog("Font size updated to " + fontSize),
                        e -> addLog("Update failed: " + e.getMessage())
                ));
        // add log
        addLog("set font size: " + fontSize);
    }


    /**
     * get formatted summary
     * @param settings settings
     * @return formatted summary
     */
    public String getFormattedSummary(SettingsModel settings) {
        if (settings == null) return "";

        String mode = getApplication().getString(settings.isDarkMode()
                ? R.string.demo_datastore_mode_dark
                : R.string.demo_datastore_mode_light);

        return getApplication().getString(
                R.string.demo_datastore_settings_summary,
                mode,
                settings.getLanguage(),
                settings.getFontSize()
        );
    }


    /**
     * get font size preview text
     * @param fontSize font size
     * @return font size preview text
     */
    public String getFontSizePreviewText(float fontSize) {
        return getApplication().getString(R.string.demo_datastore_font_size_preview, fontSize);
    }

    /**
     * add log
     * @param msg log message
     */
    public void addLog(String msg) {
        Utils.info(this, "[addLog]: " + msg);
        List<String> logs = mLogs.getValue();
        logs.add(msg);
        mLogs.setValue(logs);
    }

    /**
     * clear log
     */
    public void clearLog() {
        mLogs.setValue(new ArrayList<>());
    }

}
