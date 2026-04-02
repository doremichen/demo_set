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

package com.adam.app.demoset.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public final class LocaleUtils {

    private LocaleUtils() {
        throw new UnsupportedOperationException("This is utils, u can't instantiate me...");
    }

    public static void applyLocale(Context context, String langCode) {
        // --- Android 13 (API 33) 以上：使用官方新標準 ---
        // 這會自動觸發 Activity 重啟，且支援系統設定頁面同步
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LocaleListCompat localeList = LocaleListCompat.forLanguageTags(langCode);
            AppCompatDelegate.setApplicationLocales(localeList);
        } else {
            // --- Android 12 (API 32) 以下：傳統相容做法 ---
            // 透過 AppCompatDelegate 依然可以保持一定程度的一致性
            // 或是手動更新 Resources (如您原本的 updateLocale)
            updateLegacyResources(context, langCode);

            // 舊版本通常需要手動觸發 Activity recreate
            if (context instanceof Activity) {
                ((Activity) context).recreate();
            }
        }
    }

    @Deprecated
    private static void updateLegacyResources(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

    }

}
