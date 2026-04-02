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

package com.adam.app.demoset.datastore.java.model;

import androidx.annotation.NonNull;

public class SettingsModel {
    // settings
    private final boolean mDarkMode;
    private final String mLanguage;
    private final float mFontSize;

    /**
     * construct
     * @param darkMode dark mode
     * @param language language
     * @param fontSize font size
     */
    public SettingsModel(boolean darkMode, String language, float fontSize) {
        mDarkMode = darkMode;
        mLanguage = language;
        mFontSize = fontSize;
    }

    // --- getter ---
    public boolean isDarkMode() {
        return mDarkMode;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public float getFontSize() {
        return mFontSize;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SettingsModel ===============").append("\n");
        sb.append("dark mode: ").append(mDarkMode).append("\n");
        sb.append("language: ").append(mLanguage).append("\n");
        sb.append("font size: ").append(mFontSize).append("\n");
        sb.append("==============================").append("\n");
        return sb.toString();
    }
}
