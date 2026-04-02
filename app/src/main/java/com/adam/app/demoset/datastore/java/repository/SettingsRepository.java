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

package com.adam.app.demoset.datastore.java.repository;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.adam.app.demoset.datastore.java.model.SettingsModel;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class SettingsRepository {

    // user setting name
    public static final String USER_SETTINGS = "user_settings";

    // define keys ---
    private static final Preferences.Key<Boolean> IS_DARK_MODE = PreferencesKeys.booleanKey("is_dark_mode");
    private static final Preferences.Key<String> LANGUAGE = PreferencesKeys.stringKey("language");
    private static final Preferences.Key<Float> FONT_SIZE = PreferencesKeys.floatKey("font_size");

    public static final String LANG_ZH_TW = "zh_TW";


    // data store
    private final RxDataStore<Preferences> mDataStore;

    private volatile static SettingsRepository INSTANCE;

    /**
     * Constructor
     *
     * @param ctx Context
     */
    private SettingsRepository(Context ctx) {
        mDataStore = new RxPreferenceDataStoreBuilder(ctx, USER_SETTINGS).build();
    }


    /**
     * DLCP
     */
    public static SettingsRepository getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (SettingsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SettingsRepository(ctx.getApplicationContext());
                }
            }
        }

        return INSTANCE;
    }

    /**
     * get settings
     * @return Flowable
     */
    public Flowable<SettingsModel> getSettings() {
        return mDataStore.data()
                .map(preferences -> {
                    boolean isDarkMode = preferences.get(IS_DARK_MODE) == null ? true : preferences.get(IS_DARK_MODE);
                    String language = preferences.get(LANGUAGE) == null ? LANG_ZH_TW : preferences.get(LANGUAGE);
                    float fontSize = preferences.get(FONT_SIZE) == null ? 1.0f : preferences.get(FONT_SIZE);
                    return new SettingsModel(isDarkMode, language, fontSize);
                })
                .onErrorReturnItem(new SettingsModel(true, LANG_ZH_TW, 1.0f));
    }

    // --- setters ---

    /**
     * set dark mode
     * @param isDarkMode dark mode
     * @return single
     */
    public Single<Preferences> setDarkMode(boolean isDarkMode) {
       return mDataStore.updateDataAsync(preferences -> {
           MutablePreferences mutablePreferences = preferences.toMutablePreferences();
           mutablePreferences.set(IS_DARK_MODE, isDarkMode);
           return Single.just(mutablePreferences);
       });
    }

    /**
     * set language
     * @param language language
     * @return single
     */
    public Single<Preferences> setLanguage(String language) {
        return mDataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(LANGUAGE, language);
            return Single.just(mutablePreferences);
        });
    }

    /**
     * set font size
     * @param fontSize font size
     * @return single
     */
    public Single<Preferences> setFontSize(float fontSize) {
        return mDataStore.updateDataAsync(preferences -> {
           MutablePreferences mutablePreferences = preferences.toMutablePreferences();
           mutablePreferences.set(FONT_SIZE, fontSize);
           return Single.just(mutablePreferences);
        });

    }
}
