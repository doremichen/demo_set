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

package com.adam.app.demoset.navigation.repository;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class SettingRepository {

    /**
     * Singleton
     */
    private volatile static SettingRepository sInstance;

    // data store
    private final RxDataStore<Preferences> mDtaaStore;

    // Key
    private final Preferences.Key<Boolean> NOTIFICATION_KEY = PreferencesKeys.booleanKey("notification_enabled");

    /**
     * Constructor
     *
     * @param context Context
     */
    private SettingRepository(Context context) {
        mDtaaStore = new RxPreferenceDataStoreBuilder(
                context.getApplicationContext(),
                "navigation_settings"
        ).build();
    }


    /**
     * DCLP
     */
    public static SettingRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SettingRepository.class) {
                if (sInstance == null) {
                    sInstance = new SettingRepository(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * get notification enabled
     *
     * @return Flowable<Boolean>
     */
    public Flowable<Boolean> getNotificationEnabled() {
        return mDtaaStore.data()
                .map(preferences -> preferences.get(NOTIFICATION_KEY) != null
                        ? preferences.get(NOTIFICATION_KEY)
                        : false);
    }

    /**
     * set notification enabled
     *
     * @param enabled Boolean
     */
    public void setNotificationEnabled(Boolean enabled) {
        mDtaaStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(NOTIFICATION_KEY, enabled);
            return Single.just(mutablePreferences);
        });
    }

    public void clearAllSettings() {
        mDtaaStore.updateDataAsync(
                preferences -> {
                    MutablePreferences mutablePreferences = preferences.toMutablePreferences();
                    mutablePreferences.clear();
                    return Single.just(mutablePreferences);
                }
        );
    }


}
